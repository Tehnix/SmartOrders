package kaist.restaurantownerapp.communication;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

public class NfcManager {

    private static final int REQUEST_ENABLE_NFC = 143;

    public static final String NFC_MIME_TYPE = "application/smartorder";

    public static final String AAR_RECORD = CommunicationManager.IDENTIFIER;

    private Activity mAppContext;

    private NfcAdapter mNfcAdapter;

    private Tag mNfcTag;

    private PendingIntent pendingIntent;

    private boolean mNfcEnabled = false;

    private final Charset US_ASCII = Charset.forName("US-ASCII");

    private boolean mForegroundDispatchEnabled = false;

    /*
     * Set up the NFC Adapter and store the app context.
     */
    public NfcManager(Activity activity) {
        mAppContext = activity;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter != null) {
            mNfcEnabled = true;
        }
        checkNfcEnabled();
    }

    /*
     * Check if BLE is enabled, else request it be enabled.
     */
    private boolean checkNfcEnabled() {
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            mAppContext.startActivityForResult(enableBtIntent, REQUEST_ENABLE_NFC);
            return false;
        }
        return true;
    }

    /*
     * Disable the foreground dispatch system.
     */
    public void disableForegroundDispatch() {
        if (!mNfcEnabled) {
            Log.e("NfcManager (dispatch)", "NFC is not enabled on the device!");
            return;
        }
        Log.d("NfcManager (dispatch)", "Disabling foreground dispatch");
        if (mForegroundDispatchEnabled) {
            mNfcAdapter.disableForegroundDispatch(mAppContext);
        }

        mForegroundDispatchEnabled = false;
    }

    /*
     * Setup and enable the foreground dispatch system.
     */
    public void enableForegroundDispatch() {
        if (!mNfcEnabled) {
            Log.e("NfcManager (dispatch)", "NFC is not enabled on the device!");
            return;
        }
        if (pendingIntent == null) {
            pendingIntent = createPendingIntent();
        }
        Log.d("NfcManager (dispatch)", "Enabling foreground dispatch");
        if (!mForegroundDispatchEnabled) {
            mNfcAdapter.enableForegroundDispatch(mAppContext, pendingIntent, null, null);
        }

        mForegroundDispatchEnabled = true;
    }

    /*
     * Handle NFC tags that have triggered the intent-filter. If there was a message successfully
     * found, then it returns Either.right(payload), else it will return and error message contained
     * in Either.left(error).
     *
     * Only react if the intent is an ACTION_NDEF_DISCOVERED, since we are only writing to the
     * NFC tags in NDEF format in the first place. The ACTION_NDEF_DISCOVERED intent is also the
     * highest priority intent.
     *
     * Alternatively, if it's not an ACTION_NDEF_DISCOVERED, we check for ACTION_TAG_DISCOVERED
     * and store the tag information, so we can write to it.
     */
    public Either<String, String> readNfcTag(Intent intent) {
        // Make sure we only handle the intent if the NFC adapter is enabled, and the intent
        // actually contains a value.
        if (!mNfcEnabled) {
            Log.e("NfcManager.readNfcTag", "NFC is not enabled on the device!");
            checkNfcEnabled();
            return Either.left("NFC is not enabled on the device!");
        } else if (intent == null) {
            return Either.left("No intent received");
        }

        // Check if the intent either contains a message or is simply just a discovered tag.
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.d("NfcManager.readNfcTag", "NFC NDEF Discovered");
            mNfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i("NfcManager.readNfcTag", "Tag: " + mNfcTag.toString());

            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages == null) {
                return Either.left("No NDEF messages found");
            }

            // Go through each message and inspect the payload and records.
            Either<String, String> payload = Either.left("No payload!");
            Log.d("NfcManager.readNfcTag", "Reading " + rawMessages.length + " message(s)");
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                Log.i("NfcManager.readNfcTag", "Message: " + messages[i].toString());

                // Go through each record and payload.
                NdefRecord[] records = messages[i].getRecords();
                for (int r = 0; r < records.length; r++) {
                    String _payload = new String(records[r].getPayload(), US_ASCII);
                    String _type = new String(records[r].getType(), US_ASCII);

                    // If the type of the message matches our MIME type, then store it as
                    // the payload data.
                    if (_type.toLowerCase().equals(NFC_MIME_TYPE.toLowerCase())) {
                        Log.d("NfcManager.readNfcTag", "Matches MIME type, found payload in #" + r);
                        payload = Either.right(_payload);
                    }
                    Log.i("NfcManager.readNfcTag", "Payload(" + r + "): " + _payload);
                    Log.i("NfcManager.readNfcTag", "Type(" + r + "): " + _type);
                }
            }
            return payload;
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // If we have discovered a tag without an NDEF message, then we store the tag object so
            // that we may write to it later.
            Log.d("NfcManager.readNfcTag", "NFC Tag Discovered");
            mNfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i("NfcManager.readNfcTag", "Tag: " + mNfcTag.toString());
            return Either.left("Discovered a new NFC tag!");
        }
        return Either.left("Not an NFC intent");
    }

    /*
     * Write a message to an NFC tag.
     *
     * - Will throw an IOException if something goes wrong while connecting to it.
     * - Will throw a FormatException if there is something wrong with the NFC message that it is
     * trying to write.
     */
    public Either<String, String> writeNfcTag(String payload) {
        // Make sure we only handle the write if the NFC adapter is enabled, and that we actually
        // have an NFC tag discovered.
        if (!mNfcEnabled) {
            Log.e("NfcManager.writeNfcTag", "NFC is not enabled on the device!");
            checkNfcEnabled();
            return Either.left("NFC is not enabled on the device!");
        }
        if (mNfcTag == null) {
            Log.d("NfcManager.writeNfcTag", "No NFC tag found!");
            return Either.left("No NFC tag found!");
        }
        Log.d("NfcManager.writeNfcTag", "Preparing the NFC message");
        NdefMessage message = prepareMessage(payload);

        // Connect to the NFC tag by trying different methods, in order: Ndef, NdefFormatable,
        // MifareUltralight and finally NfcA if all else fails. The last two only connect but
        // do not actually write.
        Log.d("NfcManager.writeNfcTag", "Trying to connect to the NFC tag with ndef");
        Ndef ndefConnection = Ndef.get(mNfcTag);
        if (ndefConnection != null) {
            // If the tag is actually writable, and not locked, then write the message on it.
            if (ndefConnection.isWritable()) {
                try {
                    ndefConnection.connect();
                    Log.d("NfcManager.writeNfcTag", "Writing the NFC message");
                    ndefConnection.writeNdefMessage(message);
                    ndefConnection.close();
                } catch (FormatException e) {
                    e.printStackTrace();
                    return Either.left("An error occurred while writing!");
                } catch (IOException e) {
                    e.printStackTrace();
                    return Either.left("An error occurred while connecting or writing!");
                }
            }
            return Either.right("Successfully wrote the message!");
        }

        // Fall back on connecting first with NdefFormatable.
        Log.d("NfcManager.writeNfcTag", "Trying to connect to the NFC tag with formatable");
        NdefFormatable formatConnection = NdefFormatable.get(mNfcTag);
        if (formatConnection != null) {
            try {
                formatConnection.connect();
                Log.d("NfcManager.writeNfcTag", "Writing the NFC message");
                formatConnection.format(message);
                formatConnection.close();
            } catch (FormatException e) {
                e.printStackTrace();
                return Either.left("An error occurred while writing!");
            } catch (IOException e) {
                e.printStackTrace();
                return Either.left("An error occurred while connecting or writing!");
            }
            return Either.right("Successfully wrote the message!");
        }

        // Try to see if it's a MifareUltralight compatible chip.
        Log.d("NfcManager.writeNfcTag", "Trying to connect to the NFC tag with Mifare Ultralight");
        MifareUltralight mifareConnection = MifareUltralight.get(mNfcTag);
        if (mifareConnection != null) {
            try {
                mifareConnection.connect();
                mifareConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
                return Either.left("An error occurred while connecting");
            }
            return Either.left("MifareUltralight chips are not implemented for writing yet!");
        }

        // Finally, try to see if it's a NfcA compatible chip.
        Log.d("NfcManager.writeNfcTag", "Trying to connect to the NFC tag with NfcA");
        NfcA nfcaConnection = NfcA.get(mNfcTag);
        if (nfcaConnection != null) {
            try {
                nfcaConnection.connect();
                nfcaConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
                return Either.left("An error occurred while connecting");
            }
            return Either.left("MifareUltralight chips are not implemented for writing yet!");
        }

        Log.e("NfcManager.writeNfcTag", "Failed to connect to the NFC tag!");
        return Either.left("Failed to connect to the NFC tag!");
    }

    /*
     * Construct an NFC message with the app MIME data and an AAR (Application Record).
     */
    private NdefMessage prepareMessage(String payload) {
        NdefMessage message = _prepareMessage(payload, true);
        return message;
    }

    /*
     * Construct an NFC message with the app MIME data *without* an AAR (Application Record).
     */
    private NdefMessage prepareMessageWithoutAAR(String payload) {
        NdefMessage message = _prepareMessage(payload, false);
        return message;
    }

    private NdefMessage _prepareMessage(String payload, boolean useAAR) {
        // Prepare the header/record data with a TNF of TNF_MIME_MEDIA, and insert the message into
        // it in byte format encoded as US-ASCII.
        NdefRecord mimeRecord = NdefRecord.createMime(
                NFC_MIME_TYPE,
                payload.getBytes(US_ASCII)
        );
        // Create the record array for the message, depending on wheter or not to include the AAR.
        NdefRecord[] ndefRecords = new NdefRecord[] { mimeRecord };
        if (useAAR) {
            ndefRecords = new NdefRecord[] {
                    mimeRecord,
                    NdefRecord.createApplicationRecord(AAR_RECORD)
            };
        }
        // Create the message containing the record array.
        NdefMessage message = new NdefMessage(ndefRecords);
        return message;
    }

    /*
     * Create an intents filter, based on the MIME type.
     */
    private IntentFilter[] createIntentsFilter() {
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType(NFC_MIME_TYPE);
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Malformed Mime Type!", e);
        }
        return new IntentFilter[] { ndefFilter };
    }

    /*
     * Create an empty techs list filter.
     */
    private String[][] createTechsFilter() {
        return null;
    }

    /*
     * Create a PendingIntent object, to hold new intents for the foreground dispatch system.
     */
    private PendingIntent createPendingIntent() {
        return PendingIntent.getActivity(
                mAppContext,
                0,
                new Intent(mAppContext, mAppContext.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0
        );
    }

}
