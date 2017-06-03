package kr.ac.kaist.smartorder.smartorder;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.util.Log;

@SuppressLint("NewApi")
public class BleServer {

    private BluetoothLeAdvertiser mBleAdvertiser;

    private BluetoothGattServer mBleGattServer;

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
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }
    };

    /*
     * Initiate advertising the BLE services.
     */
    public BleServer(Activity context, BluetoothManager bleManager, BluetoothAdapter bleAdapter) {
        // Start advertising.
        mBleAdvertiser = bleAdapter.getBluetoothLeAdvertiser();
        mBleAdvertiser.startAdvertising(
                mAdvertiseSettings,
                mAdvertiseData,
                mAdvertiseScanResponseData,
                mAdvertiseCallback
        );

        // Start the GATT server.
        mBleGattServer = bleManager.openGattServer(context, mBleGattCallback);
        BluetoothGattService service = new BluetoothGattService(BleManager.UUID_SMARTORDER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(BleManager.UUID_SMARTORDER_MENU, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        service.addCharacteristic(characteristic);
        mBleGattServer.addService(service);
    }

}
