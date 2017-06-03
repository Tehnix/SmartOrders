package kr.ac.kaist.smartorder.smartorder;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;


public class BleManager {

    private static final int REQUEST_ENABLE_BT = 144;

    private static final long BLE_SCAN_PERIOD = 12000;

    private Activity mAppContext;

    private BluetoothManager mBleManager;

    private BluetoothAdapter mBleAdapter;

    private BleClient mBleClient;

    private BleServer mBleServer;

    private Handler mScanHandler = new Handler();

    private String mBleAddress = null;

    private boolean mIsScanning = false;

    private boolean mIsConnected = false;

    /*
     * Data/intent identifiers.
     */
    public final static String ACTION_GATT_CONNECTED = CommunicationManager.IDENTIFIER + ".ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = CommunicationManager.IDENTIFIER + ".ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = CommunicationManager.IDENTIFIER + ".ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = CommunicationManager.IDENTIFIER + ".ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = CommunicationManager.IDENTIFIER + ".EXTRA_DATA";

    /*
     * UUIDs to identify the data type.
     */
    public final static UUID UUID_SMARTORDER = UUID.fromString("0x2AB7");
    public final static UUID UUID_SMARTORDER_MENU = UUID.fromString("0x2AB8");
    public final static UUID UUID_SMARTORDER_DATA = UUID.fromString("0x2AB9");

    /*
     * Handle BLE scan results.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (mBleAddress != null && mBleAddress.equals(device.getAddress())) {
                        Log.i("BleManager.mLeScanCal..", "Found device matching bleAddress: " + device.getAddress());
                        // Stop the scan if the device was already found.
                        stopLeScan();
                        // Connect to the device.
                        mBleClient = new BleClient();
                        mBleClient.connectToDevice(mAppContext, device);
                    }
                }
            };

    /*
     * Handle BLE GATT broadcasts.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleManager.ACTION_GATT_CONNECTED.equals(action)) {
                mIsConnected = true;
            } else if (BleManager.ACTION_GATT_DISCONNECTED.equals(action)) {
                mIsConnected = false;
            } else if (BleManager.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.i("BleManager.mGattUpdat..", mBleClient.getSupportedGattServices().toString());
            } else if (BleManager.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i("BleManager.mGattUpdat..", intent.getStringExtra(BleManager.EXTRA_DATA));
            }
        }
    };

    /*
     * Set up the Bluetooth Adapter and store the app context.
     */
    public BleManager(Activity activity) {
        mAppContext = activity;
        mBleManager = (BluetoothManager) mAppContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBleManager.getAdapter();
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
        mBleAddress = deviceAddress;
        // Stop the scan after it has run for BLE_SCAN_PERIOD ms.
        mScanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLeScan();
            }
        }, BLE_SCAN_PERIOD);

        Log.d("BleManager.scanForDev..", "Starting BLE scan");
        Log.i("BleManager.scanForDev..", "Looking for device with address: " + mBleAddress);
        mIsScanning = true;
        //UUID[] uuidServices = new UUID[] { BleManager.UUID_SMARTORDER };
        //return mBleAdapter.startLeScan(uuidServices, mLeScanCallback);
        return mBleAdapter.startLeScan(mLeScanCallback);
    }

    /*
     * Wrapper around the regular stopLeScan, checking if it has already stopped.
     */
    private void stopLeScan() {
        if (mIsScanning) {
            Log.d("BleManager.scanForDev..", "Stopping BLE scan");
            mIsScanning = false;
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

    /*
     * Start a BLE GATT server that clients can connect to. Note that the server requires
     * at least Android SDK version 21.
     */
    public boolean startBleServer() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mBleServer = new BleServer(mAppContext, mBleManager, mBleAdapter);
        }
        return false;
    }

    public boolean connectToBleServer() {
        return false;
    }
}
