package kaist.customerapplication.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kaist.antr.kaist.R;

import kaist.customerapplication.communicationmanager.CommunicationManager;
import kaist.customerapplication.communicationmanager.Either;

public class ScanTagActivity extends AppCompatActivity {

    Button scanBtn;
    TextView statusText;
    private TextView uiNfcDataText;
    final static String SCANNING_TEXT = "Scanning...";
    private String mNfcMessage = "";


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


    }

    public void scanForNFC(View view){
        statusText.setText(SCANNING_TEXT);
        statusText.setText("NFC tag found. Connecting to restaurant...");
        goBackToMainActivity();
    }

    /*
     * @NFC:
     * Handle new intents, which can include NFC tag readings and also the NFC tag discovery.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Either<String, String> nfcMessage = mCommunicationManager.readNfcTag(intent);
        if (nfcMessage.isRight()) {
            mNfcMessage = nfcMessage.right();
            Toast.makeText(this, "Successfully connected to restaurant!", Toast.LENGTH_SHORT).show();
            goBackToMainActivity();
        } else {
            mNfcMessage = nfcMessage.left();
        }
        uiNfcDataText.setText(mNfcMessage);
    }

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
