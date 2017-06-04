package kr.ac.kaist.smartorder.smartorder;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;


public class BleClient extends Service {

    private Activity mAppContext;

    private BluetoothGatt mBluetoothGatt;

    private Context mContext;

    private BluetoothDevice mDevice;

    private final IBinder mBinder = new LocalBinder();

    private Handler mHandler;

    private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;

    private BluetoothGattCharacteristic mMenuCharacteristic;

    private BluetoothGattCharacteristic mDataCharacteristic;

    private final Charset UTF_8 = Charset.forName("UTF-8");

    private String mMenuData = "";

    private int mWriteChrcIndex = 0;

    private String mOrderData = "";

    private boolean mMenuReceived = false;

    /*
     * Handle BLE GATT Callbacks on the client side of the connection.
     */
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    Log.i("BleClient.mGat..Conn", "onConnectionStateChange: Status = " + status);
                    try {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            mConnectionState = BluetoothProfile.STATE_CONNECTED;
                            Log.i("BleClient.mGat..Conn", "onConnectionStateChange: Connected to GATT server.");
                            Log.i("BleClient.mGat..Conn", "onConnectionStateChange: Attempting to start service discovery");
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothGatt.discoverServices();
                                }
                            }, 2000);
                            mBluetoothGatt.discoverServices();
                            broadcastUpdate(BleManager.ACTION_GATT_CONNECTED);
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
                            Log.i("BleClient.mGat..Conn", "onConnectionStateChange: Disconnected from GATT server.");
                            broadcastUpdate(BleManager.ACTION_GATT_DISCONNECTED);
                        } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                            mConnectionState = BluetoothProfile.STATE_CONNECTING;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    try {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Log.w("BleClient.mGat..Service", "onServicesDiscovered received status: " + status);
                            broadcastUpdate(BleManager.ACTION_GATT_SERVICES_DISCOVERED);

                            // Get a list of characteristics and perform a read on the menu
                            // characteristic.
                            List<BluetoothGattCharacteristic> supportedCharacteristics = getSupportedGattCharacteristics();
                            Log.i("BleClient.mGat..Service", "onServicesDiscovered: " + supportedCharacteristics.toString());
                            for (BluetoothGattCharacteristic characteristic : supportedCharacteristics) {
                                if (BleManager.UUID_SMARTORDER_MENU.equals(characteristic.getUuid())) {
                                    mMenuCharacteristic = characteristic;
                                    if (!mMenuReceived) {
                                        mBluetoothGatt.readCharacteristic(mMenuCharacteristic);
                                    }
                                }
                                if (BleManager.UUID_SMARTORDER_DATA.equals(characteristic.getUuid())) {
                                    mDataCharacteristic = characteristic;
                                }
                            }
                        } else {
                            Log.w("BleClient.mGat..Service", "onServicesDiscovered received: " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    try {
                        Log.i("BleClient.mGat..read", "onCharacteristicRead status: " + status);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Log.i("BleClient.mGat..read", "onCharacteristicRead: " + characteristic.toString());
                            broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);

                            if (characteristic.getUuid().equals(BleManager.UUID_SMARTORDER_MENU)) {
                                final String data = new String(characteristic.getValue(), UTF_8);
                                if (!data.equals(BleManager.END_OF_TRANSMISSION)) {
                                    Log.i("BleClient.mGat..read", "onCharacteristicRead: Requesting next part!");
                                    mBluetoothGatt.readCharacteristic(mMenuCharacteristic);
                                }
                            }
                        } else {
                            Log.i("BleClient.mGat..read", "onCharacteristicRead: Got status = " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    try {
                        Log.i("BleClient.mGat..write", "onCharacteristicWrite status: " + status);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            if (characteristic.getUuid().equals(BleManager.UUID_SMARTORDER_DATA)) {
                                final String data = new String(characteristic.getValue(), UTF_8);
                                if (!data.equals(BleManager.END_OF_TRANSMISSION)) {
                                    Log.i("BleClient.mGat..write", "onCharacteristicWrite: Sending next part of order!");
                                    broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);
                                } else {
                                    Log.i("BleClient.mGat..write", "onCharacteristicWrite: Order has been sent!");
                                }
                            }
                        }  else {
                            Log.i("BleClient.mGat..write", "onCharacteristicWrite: Got status = " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    public BleClient(Activity context) {
        mAppContext = context;
        mHandler = new Handler();
    }

    /*
     * Connect to the BLE GATT server.
     */
    public void connectToDevice(Context context, final BluetoothDevice device) {
        mContext = context;
        mDevice = device;
        mBluetoothGatt = connectToDevice();
    }

    /*
     * Internal function, mostly used to reconnect in case of connection loss.
     */
    private BluetoothGatt connectToDevice() {
        mBluetoothGatt = mDevice.connectGatt(mContext, true, mGattCallback);
        refreshDeviceCache(mBluetoothGatt);
        return mBluetoothGatt;
    }

    public void disconnect() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    /*
     * Handle plain BLE GATT broadcasts (i.e. just strings).
     */
    private void broadcastUpdate(final String action) {
        Log.i("BleClient.broadca..", action);
        Intent intent = new Intent(action);
        mAppContext.sendBroadcast(intent);
    }

    /*
     * Handle more complex BLE GATT broadcasts, that involves characteristics.
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        Log.i("BleClient.broadca..", action);
        Log.i("BleClient.broadca..", characteristic.getUuid().toString());

        // Check what type of characteristic has been received.
        Intent intent = new Intent(action);
        if (BleManager.UUID_SMARTORDER_MENU.equals(characteristic.getUuid())) {
            final String data = new String(characteristic.getValue(), UTF_8);
            // Check if we reached the end of the transmission.
            if (!data.equals(BleManager.END_OF_TRANSMISSION)) {
                Log.i("BleClient.broadca..", String.format("Building menu: %s", data));
                mMenuData = mMenuData + data;
            } else {
                Log.e("BleClient.broadca..", String.format("Received full menu: %s", mMenuData));
                intent.putExtra("uuid", BleManager.UUID_SMARTORDER_MENU.toString());
                intent.putExtra(BleManager.EXTRA_DATA, mMenuData);
                mAppContext.sendBroadcast(intent);
                mMenuReceived = true;

                // Reset the menu data builder.
                mMenuData = "";
            }
        } else if (BleManager.UUID_SMARTORDER_DATA.equals(characteristic.getUuid())) {
            final String data = new String(characteristic.getValue(), UTF_8);
            if (!data.equals(BleManager.END_OF_TRANSMISSION)) {
                // Continue sending the order data.
                Log.d("BleClient.broadca..", String.format("Received data: %s", data));
                Log.d("BleClient.broadca..", "Continuing order transmission");

                byte[] orderData = mOrderData.getBytes(UTF_8);
                byte[] request = new byte[20];
                int requestIndex = 0;
                // If the order has been written, send the END_OF_TRANSMISSION, else continue sending.
                if (mWriteChrcIndex >= orderData.length) {
                    mDataCharacteristic.setValue(BleManager.END_OF_TRANSMISSION.getBytes(UTF_8));
                    mBluetoothGatt.writeCharacteristic(mDataCharacteristic);
                    mWriteChrcIndex = 0;

                    intent.putExtra("uuid", BleManager.UUID_SMARTORDER_DATA.toString());
                    intent.putExtra(BleManager.EXTRA_DATA, "Order submitted!");
                    mAppContext.sendBroadcast(intent);

                } else {
                    // Only go through one loop for each characteristic write request.
                    for (; mWriteChrcIndex < orderData.length; mWriteChrcIndex++) {
                        request[requestIndex] = orderData[mWriteChrcIndex];
                        // Send a response every 20 bytes.
                        if (requestIndex >= 19 || mWriteChrcIndex == (orderData.length - 1)) {
                            // Fill the remaining response with spaces.
                            if (requestIndex < 19) {
                                requestIndex++;
                                for (int i = requestIndex; i <= 19; i++) {
                                    request[i] = " ".getBytes(UTF_8)[0];
                                }
                            }
                            // Send the response.
                            mDataCharacteristic.setValue(request);
                            mBluetoothGatt.writeCharacteristic(mDataCharacteristic);
                            // Increment mWriteChrcIndex manually here, because it never reaches the end
                            // the loop, where the increment happens.
                            mWriteChrcIndex++;
                            break;
                        }
                        requestIndex++;
                    }
                }
            } else {
                // When the GATT server receives a BleManager.END_OF_TRANSMISSION, it responds with
                // the order confirmation, meaning we can assume the result is here if it's not a
                // continuation message.
                Log.d("BleClient.broadca..", String.format("Received order confirmation: %s", data));
                intent.putExtra("uuid", BleManager.UUID_SMARTORDER_DATA.toString());
                intent.putExtra(BleManager.EXTRA_DATA, data);
                mAppContext.sendBroadcast(intent);
            }
        } else {
            Log.d("BleClient.broadca..", String.format("Received unknown data (from UUID %s)", characteristic.getUuid()));
        }
    }

    /*
     * Submit an order: This writes to a characteristic indicating that it wants to start transmission
     * and then sends the order data on subsequent transmissions, ending with a END_OF_TRANSMISSION.
     */
    public boolean submitOrder(String order) {
        if (mConnectionState != BluetoothProfile.STATE_CONNECTED) {
            mBluetoothGatt = connectToDevice();
            return false;
        }
        if (mDataCharacteristic != null && mBluetoothGatt != null) {
            mOrderData = order;
            // Initiate the order write request.
            Log.d("BleClient.submitOrder", "Initiating order transmission");
            mDataCharacteristic.setValue(BleManager.START_TRANSMISSION.getBytes(UTF_8));
            mBluetoothGatt.writeCharacteristic(mDataCharacteristic);
            return true;
        }
        return false;
    }

    /*
     * Update the restaurant menu, by initiating a read on the mMenuCharacteristic.
     */
    public boolean updateMenu() {
        mMenuReceived = false;
        if (mConnectionState != BluetoothProfile.STATE_CONNECTED) {
            mBluetoothGatt = connectToDevice();
            return false;
        }
        if (mMenuCharacteristic != null && mBluetoothGatt != null) {
            mBluetoothGatt.readCharacteristic(mMenuCharacteristic);
            return true;
        }
        return false;
    }

    /*
     * Create a Binder for the BleClient service.
     */
    public class LocalBinder extends Binder {
        BleClient getService() {
            return BleClient.this;
        }
    }

    /*
     * Return the BleClient object.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
     * Close the BLE GATT connection and unbind the object.
     */
    @Override
    public boolean onUnbind(Intent intent) {
        disconnect();
        return super.onUnbind(intent);
    }

    /*
     * Get a list of all the services that the device supports.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    /*
     * Get the characteristics for the SmartOrder service.
     */
    public List<BluetoothGattCharacteristic> getSupportedGattCharacteristics() {
        List<BluetoothGattService> supportedServices = getSupportedGattServices();
        if (supportedServices == null) {
            return null;
        }
        for (BluetoothGattService service : supportedServices) {
            if (BleManager.UUID_SMARTORDER.equals(service.getUuid())) {
                return service.getCharacteristics();
            }
        }
        return null;
    }

    /*
     * Create a intent filter that handles all our GATT operations.
     */
    public static IntentFilter gattIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleManager.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleManager.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleManager.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleManager.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e("BleClient.refreshDevi..", "An exception occured while refreshing device");
        }
        return false;
    }
}
