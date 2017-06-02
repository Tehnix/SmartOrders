package kaist.restaurantownerapp.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Spinner;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.data.handler.DBConnector;

public class CreateMenuItemActivity extends AppCompatActivity {

    private EditText name;
    private Spinner category;
    private EditText description;
    private EditText price;

    private DBConnector db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = MainActivity.getDatabase();

        name = (EditText) findViewById(R.id.inputMenuItemName);
        category = (Spinner) findViewById(R.id.inputMenuItemCategory);
        description = (EditText) findViewById(R.id.inputMenuItemDescription);
        price = (EditText) findViewById(R.id.inputMenuItemPrice);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.save_button) {
            MenuItem entry = new MenuItem(name.getText().toString(), category.getSelectedItem().toString(), description.getText().toString(), Double.parseDouble(price.getText().toString()));
            db.addMenuItem(entry);
            MainActivity.updateMenu();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
