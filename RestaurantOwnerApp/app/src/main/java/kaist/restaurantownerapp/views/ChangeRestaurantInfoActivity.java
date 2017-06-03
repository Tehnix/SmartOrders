package kaist.restaurantownerapp.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.ContactInfo;
import kaist.restaurantownerapp.data.GeneralInfo;
import kaist.restaurantownerapp.data.handler.DBConnector;

public class ChangeRestaurantInfoActivity extends AppCompatActivity {

    private EditText name;
    private EditText street;
    private EditText city;
    private EditText description;
    private EditText phone;
    private EditText mail;

    private DBConnector db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_restaurant_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = MainActivity.getDatabase();

        name = (EditText) findViewById(R.id.inputChangeName);
        street = (EditText) findViewById(R.id.inputChangeStreet);
        city = (EditText) findViewById(R.id.inputChangeCity);
        description = (EditText) findViewById(R.id.inputChangeDescription);
        phone = (EditText) findViewById(R.id.inputChangePhone);
        mail = (EditText) findViewById(R.id.inputChangeMail);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        GeneralInfo info = db.getRestaurantInfo();
        name.setText(info.getName());
        street.setText(info.getStreet());
        city.setText(info.getCity());
        description.setText(info.getDescription());
        phone.setText(info.getContact().getPhone());
        mail.setText(info.getContact().getMail());
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
            db.updateRestaurantInfo(new GeneralInfo(name.getText().toString(), street.getText().toString(), city.getText().toString(), description.getText().toString(), new ContactInfo(phone.getText().toString(), mail.getText().toString())));
            MainActivity.updateRestaurantInfo();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
