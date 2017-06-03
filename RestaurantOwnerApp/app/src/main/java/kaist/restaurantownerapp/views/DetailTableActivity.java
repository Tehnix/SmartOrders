package kaist.restaurantownerapp.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.String;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.data.handler.DBConnector;

public class DetailTableActivity extends AppCompatActivity {

    private TextView tableLabel;
    private Button identifyNFC;
    private Button deleteTable;
    private EditText seatsInput;

    private int tableNumber;
    private Table table;

    private DBConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = MainActivity.getDatabase();

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent myIntent = getIntent();
        tableNumber = myIntent.getIntExtra("id",0);

        // get Table information
        table = db.getTable(tableNumber);

        tableLabel = (TextView) findViewById(R.id.detailTableLabel);
        identifyNFC = (Button) findViewById(R.id.detailIdentifyNFC);
        deleteTable = (Button) findViewById(R.id.detailDeleteTable);
        seatsInput = (EditText) findViewById(R.id.detailSeats);

        deleteTable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                db.deleteTable(table);
                                MainActivity.updateTableAdapter();
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailTableActivity.this);
                builder.setMessage("Delete this Table?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });



        tableLabel.setText("Table: "+ table.getTableNumber());
        int tmp = table.getTableSeats();
        seatsInput.setText(String.valueOf(table.getTableSeats()));


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
            db.updateTable(new Table(tableNumber, Integer.parseInt(seatsInput.getText().toString()), false));
            MainActivity.updateTableAdapter();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
