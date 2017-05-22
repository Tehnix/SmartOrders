package kr.ac.kaist.smartorder.smartorder;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


public class BleManager {

    private static final int REQUEST_ENABLE_BT = 144;

    private static final long BLE_SCAN_PERIOD = 12000;

    private Activity mAppContext;

    private BluetoothAdapter mBleAdapter;

    private boolean isScanning = false;

    private Handler mScanHandler = new Handler();

    private String bleAddress = null;

    /*
     * Handle BLE scan results.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (bleAddress != null && bleAddress.equals(device.getAddress())) {
                        Log.i("BleManager.mLeScanCal..", "Found device matching bleAddress: " + device.getAddress());
                        // Stop the scan if the device was already found.
                        stopLeScan();
                    }
                }
            };

    /*
     * Set up the Bluetooth Adapter and store the app context.
     */
    public BleManager(Activity activity) {
        mAppContext = activity;
        final BluetoothManager bleManager = (BluetoothManager) mAppContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = bleManager.getAdapter();
        checkBleEnabled();
    }

    /*
     * Check if BLE is enabled, else request it be enabled.
     */
    private boolean checkBleEnabled() {
        if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mAppContext.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    /*
     * Start (and stop) the BLE discovery process. Returns false if BLE is not enabled,
     * and true if the scan has been started.
     */
    public boolean scanForDevices(String deviceAddress) {
        if (!checkBleEnabled()) {
            return false;
        }
        bleAddress = deviceAddress;
        // Stop the scan after it has run for BLE_SCAN_PERIOD ms.
        mScanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLeScan();
            }
        }, BLE_SCAN_PERIOD);

        Log.d("BleManager.scanForDev..", "Starting BLE scan");
        Log.i("BleManager.scanForDev..", "Looking for device with address: " + bleAddress);
        isScanning = true;
        return mBleAdapter.startLeScan(mLeScanCallback);
    }

    /*
     * Wrapper around the regular stopLeScan, checking if it has already stopped.
     */
    private void stopLeScan() {
        if (isScanning) {
            Log.d("BleManager.scanForDev..", "Stopping BLE scan");
            isScanning = false;
            mBleAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /*
     * Get the devices own address.
     */
    public Either<String, String> getOwnAddress() {
        Either<String, String> address = Either.left("Ble must be enabled!");
        if (checkBleEnabled()) {
            address = Either.right(mBleAdapter.getAddress());
        }
        return address;
    }
}
