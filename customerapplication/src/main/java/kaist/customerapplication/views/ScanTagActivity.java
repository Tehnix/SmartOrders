package kaist.customerapplication.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kaist.antr.kaist.R;

import kaist.customerapplication.CommonObjectManager;
import kaist.customerapplication.communicationmanager.CommunicationManager;
import kaist.customerapplication.communicationmanager.Either;

public class ScanTagActivity extends AppCompatActivity {

    Button scanBtn;
    TextView statusText;
    private TextView uiNfcDataText;
    final static String SCANNING_TEXT = "Scanning...";
    private String mNfcMessage = "";
    boolean scanning;


    CommunicationManager mCommunicationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_tag);

        scanBtn = (Button) findViewById(R.id.scanBtn);
        statusText = (TextView) findViewById(R.id.statusText);
        uiNfcDataText = (TextView) findViewById(R.id.txtNfcData);
        uiNfcDataText.setText(mNfcMessage);

        mCommunicationManager = new CommunicationManager(this);
        scanning = false;


    }

    public void scanForNFC(View view){
        statusText.setText(SCANNING_TEXT);
        scanning = true;
    }

    /*
     * @NFC:
     * Handle new intents, which can include NFC tag readings and also the NFC tag discovery.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(scanning){
            Either<String, String> nfcMessage = mCommunicationManager.readNfcTag(intent);
            if (nfcMessage.isRight()) {
                mNfcMessage = nfcMessage.right();
                Toast.makeText(this, "Restaurant chip detected", Toast.LENGTH_SHORT).show();

                String[] payload = nfcMessage.right().split(";;");
                String tableNumber = payload[0];

                CommonObjectManager.restaurantOwnerApplicationWrapper.setNewConnectionToRestaurant(mCommunicationManager, tableNumber);
                goBackToMainActivity();
            } else {
                mNfcMessage = nfcMessage.left();
            }
            //statusText.setText(mNfcMessage);
        }
    }

    /*public void onWriteButtonClick(View v) {
        Either<String, String> writeResult = mCommunicationManager.writeNfcTag("1234");
        if (writeResult.isRight()) {
            Toast.makeText(this, "NFC Write Result", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error in NFC Write Result", Toast.LENGTH_SHORT).show();
        }
    }*/


    /*
     * @NFC:
     * Disable catching intents in the foreground, when the app is not in focus.
     */
    @Override
    public void onPause() {
        super.onPause();
        mCommunicationManager.disableForegroundDispatch();
    }

    /*
     * @NFC:
     * Enable catching intents in the foreground, when the app is in focus.
     */
    @Override
    public void onResume() {
        super.onResume();
        mCommunicationManager.enableForegroundDispatch();
    }

    private void goBackToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
