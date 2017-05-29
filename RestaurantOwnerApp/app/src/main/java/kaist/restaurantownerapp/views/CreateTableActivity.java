package kaist.restaurantownerapp.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.data.handler.DBConnector;

public class CreateTableActivity extends AppCompatActivity{

    private TextView tableLabel;
    private Button identifyNFC;
    private EditText seatsInput;

    private DBConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        db = MainActivity.getDatabase();

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tableLabel = (TextView) findViewById(R.id.textViewTableLabel);
        identifyNFC = (Button) findViewById(R.id.buttonIdentifyNFC);
        seatsInput = (EditText) findViewById(R.id.editTextSeats);

        tableLabel.setText("Table: " + (db.getCountTables() + 1));

        identifyNFC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.save_button) {
            db.addTable(new Table(db.getCountTables()+1,Integer.valueOf(seatsInput.getText().toString()), false));
            MainActivity.updateTableAdapter();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}
