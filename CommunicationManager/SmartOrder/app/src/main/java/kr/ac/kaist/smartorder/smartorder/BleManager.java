package kr.ac.kaist.smartorder.smartorder;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.UUID;


public class BleManager {

    private static final int REQUEST_ENABLE_BT = 144;

    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 123;

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

    private boolean mReceiverRegistered = false;

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
    public final static UUID UUID_SMARTORDER = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SMARTORDER_MENU = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fc");
    public final static UUID UUID_SMARTORDER_DATA = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fd");

    /*
     * Handle BLE scan results.
     */
    private final BluetoothAdapter.LeScanCallback mBleScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    ScanRecord record = ScanRecord.parseFromBytes(scanRecord);
                    if (record != null && record.getServiceUuids() != null && record.getServiceUuids().get(0).toString().equals(UUID_SMARTORDER.toString()) && mBleAddress != null && mBleAddress.equals(device.getName())) {
                        Log.i("BleManager.mBleScanCa..", "Found device matching bleAddress: " + device.getName() + ", with UUID: " + record.getServiceUuids().get(0).toString());
                        // Stop the scan if the device was already found.
                        stopLeScan();
                        // Connect to the device.
                        Log.i("BleManager.mBleScanCa..", "Binding service");
                        Intent gattServiceIntent = new Intent(mAppContext, BleClient.class);
                        mAppContext.bindService(gattServiceIntent, mBleServiceConnection, mAppContext.BIND_AUTO_CREATE);
                        Log.i("BleManager.mBleScanCa..", "Connecting to device");
                        new ConnectToBle(device).execute();
                    }
                }
            };

    /*
     * Handle BLE GATT broadcasts in the client side.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BleManager.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i("BleManager.mGattUpdat..", "Connected");
                mIsConnected = true;
            } else if (BleManager.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i("BleManager.mGattUpdat..", "Disconnected");
                mIsConnected = false;
            } else if (BleManager.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                Log.i("BleManager.mGattUpdat..", mBleClient.getSupportedGattServices().toString());
            } else if (BleManager.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i("BleManager.mGattUpdat..", intent.getStringExtra(BleManager.EXTRA_DATA));
            }
        }
    };

    private final ServiceConnection mBleServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleClient = ((BleClient.LocalBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleClient = null;
        }
    };

    /*
     * Set up the Bluetooth Adapter and store the app context.
     */
    public BleManager(Activity activity) {
        mAppContext = activity;
        mBleManager = (BluetoothManager) mAppContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = mBleManager.getAdapter();
        mBleClient = new BleClient(mAppContext);
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
    public boolean scanForDevices(String deviceAddress, ClientData clientData) {
        if (!checkBleEnabled()) {
            return false;
        }
        // To scan for devices we also need to request coarse location permissions.
        if (ContextCompat.checkSelfPermission(mAppContext.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mAppContext,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
            );
            return false;
        }
        mBleAddress = deviceAddress;

        Log.d("BleManager.scanForDev..", "Starting BLE scan");
        Log.i("BleManager.scanForDev..", "Looking for device with address: " + mBleAddress);
        mIsScanning = true;
        // Stop the scan after it has run for BLE_SCAN_PERIOD ms.
        mScanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopLeScan();
            }
        }, BLE_SCAN_PERIOD);
        //UUID[] uuidServices = new UUID[] { BleManager.UUID_SMARTORDER };
        //return mBleAdapter.startLeScan(uuidServices, mBleScanCallback);
        return mBleAdapter.startLeScan(mBleScanCallback);
    }

    /*
     * Wrapper around the regular stopLeScan, checking if it has already stopped.
     */
    private void stopLeScan() {
        if (mIsScanning) {
            Log.d("BleManager.scanForDev..", "Stopping BLE scan");
            mIsScanning = false;
            mBleAdapter.stopLeScan(mBleScanCallback);
        }
    }

    /*
     * Get the devices own address.
     */
    public Either<String, String> getOwnAddress() {
        Either<String, String> address = Either.left("Ble must be enabled!");
        if (checkBleEnabled()) {
            address = Either.right(mBleAdapter.getName());
        }
        return address;
    }

    /*
     * Register GATT response receivers for the BLE client.
     */
    public void registerReceiver() {
        if (!mReceiverRegistered) {
            mAppContext.registerReceiver(mGattUpdateReceiver, BleClient.gattIntentFilter());
        }
        mReceiverRegistered = true;
    }

    /*
     * Unregister GATT response receivers for the BLE client.
     */
    public void unregisterReceiver() {
        if (mReceiverRegistered) {
            mAppContext.unregisterReceiver(mGattUpdateReceiver);
        }
        mReceiverRegistered = false;
    }

    /*
     * Unbind the service.
     */
    public void destroyService() {
        mAppContext.unbindService(mBleServiceConnection);
    }

    /*
     * Disconnect from the BLE GATT server.
     */
    public void disconnectFromServer() {
        mBleClient.disconnect();
    }

    public boolean submitOrder(String order) {
        if (mIsConnected) {
            return mBleClient.submitOrder(order);
        }
        return false;
    }

    /*
     * Start a BLE GATT server that clients can connect to. Note that the server requires
     * at least Android SDK version 21.
     */
    public boolean startBleServer(final RestaurantData restaurantData) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!checkBleEnabled()) {
                return false;
            }
            Log.d("BleManager.startBleSe..", "Starting BLE GATT server");
            new StartBleServer(restaurantData) {
                @Override
                protected void onPostExecute(BleServer bleServer) {
                    super.onPostExecute(bleServer);
                    mBleServer = bleServer;
                }
            }.execute();
            return true;
        } else {
            Log.e("BleManager.startBleSe..", "SDK version is too low to start the BLE GATT server!");
        }
        return false;
    }

    private class StartBleServer extends AsyncTask<Void, Void, BleServer> {

        private RestaurantData mRestaurantData;

        public StartBleServer(RestaurantData restaurantData) {
            mRestaurantData = restaurantData;
        }

        @Override
        protected BleServer doInBackground(Void... params) {
            mBleServer = new BleServer(mAppContext, mBleManager, mBleAdapter, mRestaurantData);
            return mBleServer;
        }
    }

    private class ConnectToBle extends AsyncTask<Void, Void, Void> {

        private BluetoothDevice mDevice;

        public ConnectToBle(BluetoothDevice device) {
            mDevice = device;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mBleClient.connectToDevice(mAppContext, mDevice);
            return null;
        }
    }
}
