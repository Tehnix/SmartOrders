package kaist.restaurantownerapp.communication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.HashMap;

@SuppressLint("NewApi")
public class BleServer {

    private Activity mAppContext;

    private BluetoothLeAdvertiser mBleAdvertiser;

    private BluetoothGattServer mBleGattServer;

    private RestaurantData mRestaurantData;

    private int mReadChrcIndex = 0;

    private String mWriteChrcOrder = "";

    private final Charset UTF_8 = Charset.forName("UTF-8");

    private HashMap<String, BluetoothDevice> mConnectedDevices = new HashMap<>();

    AdvertiseSettings mAdvertiseSettings = new AdvertiseSettings.Builder()
            .setConnectable(true)
            .build();

    AdvertiseData mAdvertiseData = new AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .build();

    AdvertiseData mAdvertiseScanResponseData = new AdvertiseData.Builder()
            .addServiceUuid(new ParcelUuid(BleManager.UUID_SMARTORDER))
            .setIncludeTxPowerLevel(true)
            .build();

    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("BleServer.mAdvertiseC..", "BLE advertisement added successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("BleServer.mAdvertiseC..", "Failed to add BLE advertisement, reason: " + errorCode);
        }
    };

    BluetoothGattServerCallback mBleGattCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (mConnectedDevices.containsKey(device.getAddress())) {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("BleServer.onConn...", "Device disconnected: " + device.getAddress());
                    mConnectedDevices.remove(device.getAddress());
                }
            } else {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BleServer.onConn...", "Device connected: " + device.getAddress());
                    mConnectedDevices.put(device.getAddress(), device);
                }
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.d("BleServer.onCha..Read..", "Got a characteristic read request.");

            // Respond with the menu if it is a read request for that.
            if (BleManager.UUID_SMARTORDER_MENU.equals(characteristic.getUuid())) {
                if (mReadChrcIndex == 0) {
                    Log.e("BleServer.onCha..Read..", "Responding with menu");
                }
                byte[] menuResponse = mRestaurantData.getMenu().getBytes(UTF_8);
                byte[] response = new byte[20];
                int responseIndex = 0;
                if (mReadChrcIndex >= menuResponse.length) {
                    mBleGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            BleManager.END_OF_TRANSMISSION.getBytes(UTF_8));
                    mReadChrcIndex = 0;
                } else {
                    // Only go through one loop for each characteristic read request.
                    for (; mReadChrcIndex < menuResponse.length; mReadChrcIndex++) {
                        response[responseIndex] = menuResponse[mReadChrcIndex];
                        // Send a response every 20 bytes.
                        if (responseIndex >= 19 || mReadChrcIndex == (menuResponse.length - 1)) {
                            // Fill the remaining response with spaces.
                            if (responseIndex < 19) {
                                responseIndex++;
                                for (int i = responseIndex; i <= 19; i++) {
                                    response[i] = " ".getBytes(UTF_8)[0];
                                }
                            }
                            // Send the response.
                            mBleGattServer.sendResponse(device,
                                    requestId,
                                    BluetoothGatt.GATT_SUCCESS,
                                    0,
                                    response);
                            // Increment mReadChrcIndex manually here, because it never reaches the end
                            // the loop, where the increment happens.
                            mReadChrcIndex++;
                            break;
                        }
                        responseIndex++;
                    }
                }
            } else {
                // We don't know the requested characteristic, so return an error.
                mBleGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.d("BleServer.onCh..Write..", "Got a characteristic write request.");
            String decodedValue = new String(value, UTF_8);
            Log.d("BleServer.onCh..Write..", "Got write value: " + decodedValue);

            // Do nothing if the request does not expect a response.
            if (!responseNeeded) {
                return;
            }

            // Respond with the response to an order if it is a write request for that.
            if (BleManager.UUID_SMARTORDER_DATA.equals(characteristic.getUuid())) {
                if (decodedValue.equals(BleManager.START_TRANSMISSION)) {
                    // Indicate that the server is ready for the next part of the response.
                    mBleGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            BleManager.CONTINUE_TRANSMISSION.getBytes(UTF_8));
                } else if (!decodedValue.equals(BleManager.END_OF_TRANSMISSION)) {
                    mWriteChrcOrder = mWriteChrcOrder + decodedValue;
                    Log.i("BleServer.onCh..Write..", "Building order: " + decodedValue);
                    // Indicate that the server is ready for the next part of the response.
                    mBleGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            BleManager.CONTINUE_TRANSMISSION.getBytes(UTF_8));
                } else {
                    Log.e("BleServer.onCh..Write..", "Got full order: " + mWriteChrcOrder);
                    String orderResponse = mRestaurantData.handleOrder(mWriteChrcOrder);
                    mBleGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            orderResponse.getBytes(UTF_8));

                    // Reset the order data builder.
                    mWriteChrcOrder = "";
                }
            } else {
                // We don't know the requested characteristic, so return an error.
                mBleGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }
    };

    /*
     * Initiate advertising the BLE services.
     */
    public BleServer(Activity context, BluetoothManager bleManager, BluetoothAdapter bleAdapter, RestaurantData restaurantData) {
        mAppContext = context;
        mRestaurantData = restaurantData;
        // Start advertising.
        mBleAdvertiser = bleAdapter.getBluetoothLeAdvertiser();
        mBleAdvertiser.startAdvertising(
                mAdvertiseSettings,
                mAdvertiseData,
                mAdvertiseScanResponseData,
                mAdvertiseCallback
        );
        if (mBleAdvertiser == null) {
            Log.e("BleServer", "Couldn't instantiate BLE Advertiser");
        }

        // Start the GATT server.
        mBleGattServer = bleManager.openGattServer(mAppContext, mBleGattCallback);
        if (mBleGattServer == null) {
            Log.e("BleServer", "Couldn't instantiate BLE GATT server!!");
        }
        BluetoothGattService service = new BluetoothGattService(BleManager.UUID_SMARTORDER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic menuCharacteristic = new BluetoothGattCharacteristic
                (BleManager.UUID_SMARTORDER_MENU,
                        BluetoothGattCharacteristic.PROPERTY_READ,
                        BluetoothGattCharacteristic.PERMISSION_READ
                );
        BluetoothGattCharacteristic orderCharacteristic = new BluetoothGattCharacteristic
                (BleManager.UUID_SMARTORDER_DATA,
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                        BluetoothGattCharacteristic.PERMISSION_WRITE
                );
        service.addCharacteristic(menuCharacteristic);
        service.addCharacteristic(orderCharacteristic);
        mBleGattServer.addService(service);
    }

}
