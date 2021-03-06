package kaist.restaurantownerapp.communication;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class CommunicationManager {

    public static final String IDENTIFIER = "com.kaist.antr.kaist";

    private Activity mAppContext;

    private NfcManager mNfcManager;

    private BleManager mBleManager;

    private String tableId = null;

    private String bleAddress = null;

    private boolean isConnectedToDevice = false;

    /*
     * Start the NFC and BLE adapters.
     */
    public CommunicationManager(Activity activity) {
        mAppContext = activity;
        mBleManager = new BleManager(mAppContext);
        mNfcManager = new NfcManager(mAppContext);
    }

    /*
     * Reads the NFC tag and checks if there is a valid payload in the NFC message. A
     * valid payload contains a table ID and a BLE address for the host device.
     *
     * This method is identical to CommunicationManager.getNfcTag, except this starts
     * the BLE scan automatically.
     *
     * @see NfcManager.readNfcTag
     */
    public Either<String, String> readNfcTag(Intent intent, ClientData clientData) {
        Either<String, String> nfcMessage = mNfcManager.readNfcTag(intent);
        if (nfcMessage.isRight()) {
            // Split the payload into table id and BLE address on the delimiter ";;".
            String[] payload = nfcMessage.right().split(";;");
            if (payload.length > 1) {
                tableId = payload[0];
                bleAddress = payload[1];
                Log.d("CommMan.readNfcTag", "Found NFC payload");
                Log.i("CommMan.readNfcTag", "Table ID: " + tableId);
                Log.i("CommMan.readNfcTag", "BLE address: " + bleAddress);
                // Start scanning for the BLE address.
                scanForDevices(clientData);
            } else {
                return Either.left("Malformed payload, must contain at least table id and BLE address");
            }
        }
        return nfcMessage;
    }

    /*
     * Reads the NFC tag and checks if there is a valid payload in the NFC message. A
     * valid payload contains a table ID and a BLE address for the host device.
     *
     * This method is identical to CommunicationManager.readNfcTag, except this method
     * does *NOT* start the BLE scan automatically.
     *
     * @see NfcManager.readNfcTag
     */
    public Either<String, String> getNfcTag(Intent intent) {
        Either<String, String> nfcMessage = mNfcManager.readNfcTag(intent);
        if (nfcMessage.isRight()) {
            // Split the payload into table id and BLE address on the delimiter ";;".
            String[] payload = nfcMessage.right().split(";;");
            if (payload.length > 1) {
                tableId = payload[0];
                bleAddress = payload[1];
                Log.d("CommMan.readNfcTag", "Found NFC payload");
                Log.i("CommMan.readNfcTag", "Table ID: " + tableId);
                Log.i("CommMan.readNfcTag", "BLE address: " + bleAddress);
            } else {
                return Either.left("Malformed payload, must contain at least table id and BLE address");
            }
        }
        return nfcMessage;
    }

    /*
     * Only writes to the NFC tag if it has access to the devices BLE address,
     * since this is a vital part of the payload data.
     *
     * @see NfcManager.writeNfcTag
     * @see BleManager.getOwnAddress
     */
    public Either<String, String> writeNfcTag(String tableId) {
        Either<String, String> bleAddress = mBleManager.getOwnAddress();
        if (bleAddress.isRight()) {
            String payload = tableId + ";;" + bleAddress.right();
            return mNfcManager.writeNfcTag(payload);
        }
        return bleAddress;

    }

    /*
     * Convenience function to handle all the things on app pause.
     *
     * @see NfcManager.disableForegroundDispatch
     */
    public void handlePause() {
        mNfcManager.disableForegroundDispatch();
        mBleManager.unregisterReceiver();
    }

    /*
     * Convenience function to handle all the things on app resume.
     *
     * @see NfcManager.disableForegroundDispatch
     */
    public void handleResume() {
        mNfcManager.enableForegroundDispatch();
        mBleManager.registerReceiver();
    }

    /*
     * Convenience function to handle all the things on app destroy.
     *
     * @see NfcManager.disableForegroundDispatch
     */
    public void handleDestroy() {
        mBleManager.destroyService();
        mBleManager.disconnectFromServer();
    }

    /*
     * Submit the order to the BLE GATT server. Returns true if the order was submitted,
     * and false otherwise.
     *
     * @see BleManager.submitOrder
     */
    public boolean submitOrder(String order) {
        return mBleManager.submitOrder(order);
    }

    /*
     * Force fetching the menu from the BLE GATT server again.
     *
     * @see BleManager.updateMenu
     */
    public boolean updateMenu() {
        return mBleManager.updateMenu();
    }

    /*
     * Relies on there being set a BLE address. Returns true if the device scan is
     * started, and false if not.
     *
     * @see BleManager.scanForDevices
     */
    public boolean scanForDevices(ClientData clientData) {
        if (bleAddress != null) {
            mBleManager.scanForDevices(bleAddress, clientData);
            return true;
        } else {
            return false;
        }
    }

    /*
     * Start a BLE GATT server that clients can connect to.
     *
     * @see BleManager.startBleServer
     */
    public boolean startBleServer(RestaurantData restaurantData) {
        return mBleManager.startBleServer(restaurantData);
    }

}
