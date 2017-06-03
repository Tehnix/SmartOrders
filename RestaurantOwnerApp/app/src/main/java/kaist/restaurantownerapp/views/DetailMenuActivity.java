package kaist.restaurantownerapp.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.MenuItem;
import kaist.restaurantownerapp.data.handler.DBConnector;

public class DetailMenuActivity extends AppCompatActivity {

    private EditText name;
    private Spinner category;
    private EditText description;
    private EditText price;
    private Button deleteMenuItem;

    int itemNumber;

    private DBConnector db;

    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = MainActivity.getDatabase();

        name = (EditText) findViewById(R.id.changeMenuItemName);
        category = (Spinner) findViewById(R.id.changeMenuItemCategory);
        description = (EditText) findViewById(R.id.changeMenuItemDescription);
        price = (EditText) findViewById(R.id.changeMenuItemPrice);
        deleteMenuItem = (Button) findViewById(R.id.deleteMenuItem);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Intent myIntent = getIntent();
        itemNumber = myIntent.getIntExtra("id",0);

        item = db.getMenuItem(itemNumber);

        name.setText(item.getName());
        String categoryName = item.getCategory();
        if(categoryName.equals(R.string.category_dessert)){
            category.setSelection(3);
        }else if(categoryName.equals(R.string.category_drink)){
            category.setSelection(0);
        }else if(categoryName.equals(R.string.category_main_dish)){
            category.setSelection(2);
        }else if(categoryName.equals(R.string.category_appetizer)){
            category.setSelection(1);
        }

        description.setText(item.getDescription());
        price.setText(String.valueOf(item.getPrice()));

        deleteMenuItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                db.deleteMenuItem(item);
                                MainActivity.updateMenu();
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailMenuActivity.this);
                builder.setMessage("Delete this Menu Item?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

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
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.save_button) {
            MenuItem entry = new MenuItem(itemNumber, name.getText().toString(), category.getSelectedItem().toString(), description.getText().toString(), Double.parseDouble(price.getText().toString()));
            db.updateMenuItem(entry);
            MainActivity.updateMenu();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
