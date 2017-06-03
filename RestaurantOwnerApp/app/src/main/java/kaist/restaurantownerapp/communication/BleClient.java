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
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;


public class BleClient extends Service {

    private BluetoothGatt mBluetoothGatt;

    private final IBinder mBinder = new LocalBinder();

    private int mConnectionState = STATE_DISCONNECTED;

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
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mConnectionState = STATE_CONNECTED;
                        Log.i("BleManager.mGattCall..", "Connected to GATT server.");
                        Log.i("BleManager.mGattCall..", "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                        broadcastUpdate(BleManager.ACTION_GATT_CONNECTED);

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i("BleManager.mGattCall..", "Disconnected from GATT server.");
                        broadcastUpdate(BleManager.ACTION_GATT_DISCONNECTED);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.w("BleManager.mGattCall..", "onServicesDiscovered received: " + status);
                        broadcastUpdate(BleManager.ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w("BleManager.mGattCall..", "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i("BleManager.mGattCall..", characteristic.toString());
                        broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    broadcastUpdate(BleManager.ACTION_DATA_AVAILABLE, characteristic);
                }
            };

    public void connectToDevice(Context context, final BluetoothDevice device) {
        device.connectGatt(context, false, mGattCallback);
    }

    private void broadcastUpdate(final String action) {
        Log.i("BleManager.broadca..", action);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.i("BleManager.broadca..", action);
        Log.i("BleManager.broadca..", characteristic.getUuid().toString());
        if (BleManager.UUID_SMARTORDER_MENU.equals(characteristic.getUuid())) {
            final String data = characteristic.getStringValue(0);
            Log.d("BleManager.broadca..", String.format("Received menu: %s", data));
            intent.putExtra(BleManager.EXTRA_DATA, data);
            sendBroadcast(intent);
        } else if (BleManager.UUID_SMARTORDER_DATA.equals(characteristic.getUuid())) {
            final String data = characteristic.getStringValue(0);
            Log.d("BleManager.broadca..", String.format("Received data: %s", data));
            intent.putExtra(BleManager.EXTRA_DATA, data);
            sendBroadcast(intent);
        } else {
            Log.d("BleManager.broadca..", String.format("Received unknown data (from unknown UUID %s)", characteristic.getUuid()));
        }
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
        mBluetoothGatt.close();
        return super.onUnbind(intent);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }
}
