package kaist.restaurantownerapp.communication;

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

import java.util.List;


public class BleClient extends Service {

    private Activity mAppContext;

    private BluetoothGatt mBluetoothGatt;

    private final IBinder mBinder = new LocalBinder();

    private int mConnectionState = STATE_DISCONNECTED;

    private BluetoothGattCharacteristic mMenuCharacteristic;

    /*
     * Connection states.
     */
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    /*
     * Handle BLE GATT Callbacks on the client side of the connection.
     */
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    try {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            mConnectionState = STATE_CONNECTED;
                            Log.i("BleClient.mGattCall..", "onConnectionStateChange: Connected to GATT server.");
                            Log.i("BleClient.mGattCall..", "onConnectionStateChange: Attempting to start service discovery");
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothGatt.discoverServices();
                                }
                            }, 2000);
                            mBluetoothGatt.discoverServices();
                            broadcastUpdate(BleManager.ACTION_GATT_CONNECTED);
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            mConnectionState = STATE_DISCONNECTED;
                            Log.i("BleClient.mGattCall..", "onConnectionStateChange: Disconnected from GATT server.");
                            broadcastUpdate(BleManager.ACTION_GATT_DISCONNECTED);
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
                            Log.w("BleClient.mGattCall..", "onServicesDiscovered received status: " + status);
                            broadcastUpdate(BleManager.ACTION_GATT_SERVICES_DISCOVERED);

                            // Get a list of characteristics and perform a read on the menu
                            // characteristic.
                            List<BluetoothGattCharacteristic> supportedCharacteristics = getSupportedGattCharacteristics();
                            Log.i("BleClient.mGattCall..", "onServicesDiscovered: " + supportedCharacteristics.toString());
                            for (BluetoothGattCharacteristic characteristic : supportedCharacteristics) {
                                if (BleManager.UUID_SMARTORDER_MENU.equals(characteristic.getUuid())) {
                                    mMenuCharacteristic = characteristic;
                                    mBluetoothGatt.readCharacteristic(mMenuCharacteristic);
                                }
                            }
                        } else {
                            Log.w("BleClient.mGattCall..", "onServicesDiscovered received: " + status);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    try {
                        Log.i("BleClient.mGattCall..", "onCharacteristicRead status: " + status);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Log.i("BleClient.mGattCall..", "onCharacteristicRead: " + characteristic.toString());
                            broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    try {
                        Log.i("BleClient.mGattCall..", "onCharacteristicChanged");
                        broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };



    public BleClient(Activity context) {
        mAppContext = context;
    }

    /*
     * Connect to the BLE GATT server.
     */
    public void connectToDevice(Context context, final BluetoothDevice device) {
        mBluetoothGatt = device.connectGatt(context, true, mGattCallback);
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
            String data = characteristic.getStringValue(0);
            Log.d("BleClient.broadca..", String.format("Received menu: %s", data));
            intent.putExtra(BleManager.EXTRA_DATA, data);
            mAppContext.sendBroadcast(intent);
        } else if (BleManager.UUID_SMARTORDER_DATA.equals(characteristic.getUuid())) {
            final String data = characteristic.getStringValue(0);
            Log.d("BleClient.broadca..", String.format("Received data: %s", data));
            intent.putExtra(BleManager.EXTRA_DATA, data);
            mAppContext.sendBroadcast(intent);
        } else {
            Log.d("BleClient.broadca..", String.format("Received unknown data (from UUID %s)", characteristic.getUuid()));
        }
    }

    public boolean submitOrder(String order) {
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
}
