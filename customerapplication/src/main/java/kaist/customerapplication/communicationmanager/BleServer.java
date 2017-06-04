package kaist.customerapplication.communicationmanager;


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
                Log.e("BleServer.onCha..Read..", "Respond with menu");
                String menuResponse = mRestaurantData.getMenu();
                mBleGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        menuResponse.getBytes(UTF_8));
            } else {
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
                Log.e("BleServer.onCh..Write..", "Accept order");

                boolean orderStatus = mRestaurantData.handleOrder(decodedValue);
                String orderResponse = "Invalid order!";
                if (orderStatus) {
                    orderResponse = "Order received!";
                }
                mBleGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        orderResponse.getBytes(UTF_8));
            } else {
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
