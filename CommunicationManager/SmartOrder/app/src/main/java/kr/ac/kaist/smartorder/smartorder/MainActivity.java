package kr.ac.kaist.smartorder.smartorder;

import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mContentView;

    private CommunicationManager mCommunicationManager;

    private TextView uiNfcDataText;

    private String mNfcMessage = "";

    private TextView uiBleDataText;

    private String mBleName = "";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setHomeView();
                    return true;
                case R.id.navigation_nfc:
                    setNFCView();
                    return true;
                case R.id.navigation_ble:
                    setBLEView();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // @NFC: Set up the NFC Manager.
        mCommunicationManager = new CommunicationManager(this);

        mContentView = (FrameLayout) findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_nfc);
    }

    private void setHomeView() {
        mContentView.removeAllViews();
        View homeChild = getLayoutInflater().inflate(R.layout.content_home, null);
        mContentView.addView(homeChild);
    }

    private void setNFCView() {
        mContentView.removeAllViews();
        View nfcChild = getLayoutInflater().inflate(R.layout.content_nfc, null);
        mContentView.addView(nfcChild);
        uiNfcDataText = (TextView) findViewById(R.id.txtNfcData);
        uiNfcDataText.setText(mNfcMessage);
        Button btnWriteToNFC = (Button) findViewById(R.id.btnWriteToNfcTag);
        btnWriteToNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWriteButtonClick();
            }
        });
    }

    private void setBLEView() {
        mContentView.removeAllViews();
        View bleChild = getLayoutInflater().inflate(R.layout.content_ble, null);
        mContentView.addView(bleChild);
        uiBleDataText = (TextView) findViewById(R.id.txtBleName);
        uiBleDataText.setText(mBleName);
        Button btnScanForBle = (Button) findViewById(R.id.btnScanForBle);
        btnScanForBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanButtonClick();
            }
        });
    }

    /*
     * @NFC:
     * Handle new intents, which can include NFC tag readings and also the NFC tag discovery.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Either<String, String> nfcMessage = mCommunicationManager.readNfcTag(intent);
        uiNfcDataText = (TextView) findViewById(R.id.txtNfcData);
        if (nfcMessage.isRight()) {
            mNfcMessage = nfcMessage.right();
            Toast.makeText(this, "Read NFC successfully!", Toast.LENGTH_SHORT).show();
        } else {
            mNfcMessage = nfcMessage.left();
        }
        uiNfcDataText.setText(mNfcMessage);
    }

    /*
     * @NFC:
     * Write data to a NFC tag.
     */
    protected void onWriteButtonClick() {
        String tableNumber = "12";
        Either<String, String> writeResult = mCommunicationManager.writeNfcTag(tableNumber);
        if (writeResult.isRight()) {
            Toast.makeText(this, writeResult.right(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, writeResult.left(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * @NFC:
     * Disable catching intents in the foreground, when the app is not in focus.
     */
    public void onPause() {
        super.onPause();
        mCommunicationManager.disableForegroundDispatch();
    }

    /*
     * @NFC:
     * Enable catching intents in the foreground, when the app is in focus.
     */
    public void onResume() {
        super.onResume();
        mCommunicationManager.enableForegroundDispatch();
    }


    protected void onScanButtonClick() {
        mCommunicationManager.scanForDevices();
    }
}
