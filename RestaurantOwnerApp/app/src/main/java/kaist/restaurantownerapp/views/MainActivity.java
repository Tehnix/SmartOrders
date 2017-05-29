package kaist.restaurantownerapp.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.content.Intent;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.data.handler.DBConnector;
import kaist.restaurantownerapp.listviewhandler.TableAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewFlipper vf;
    private ListView lv;
    private ContentState currentContent = ContentState.ORDERS;

    public static String [] prgmNameList={"1","2","3","4","5","6","7","8","9"};

    private Intent createNewTable;
    private Intent changeRestaurantInfo;

    private FloatingActionButton fab;

    public static DBConnector db;
    public static TableAdapter tAdapter;

    // Content Settings Views
    public static TextView restaurantName;
    public static TextView restaurantStreet;
    public static TextView restaurantCity;
    public static TextView restaurantDescription;
    public static TextView restaurantPhone;
    public static TextView restaurantEMail;
    public static Button restaurantInfoChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBConnector(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNewTable = new Intent(this, CreateTableActivity.class);
        changeRestaurantInfo = new Intent(this, ChangeRestaurantInfoActivity.class);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentContent == ContentState.TABLES){
                    startActivity(createNewTable);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_orders);

        vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(0);

        initContentTable();
        initContentSettings();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            currentContent = ContentState.ORDERS;
            fab.setVisibility(View.VISIBLE);
            vf.setDisplayedChild(0);
        } else if (id == R.id.nav_menu) {
            currentContent = ContentState.MENU;
            fab.setVisibility(View.VISIBLE);
            vf.setDisplayedChild(1);
        } else if (id == R.id.nav_tables) {
            currentContent = ContentState.TABLES;
            fab.setVisibility(View.VISIBLE);
            vf.setDisplayedChild(2);
        } else if (id == R.id.nav_settings) {
            currentContent = ContentState.SETTINGS;
            fab.setVisibility(View.INVISIBLE);
            vf.setDisplayedChild(3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static DBConnector getDatabase(){
        return db;
    }

    public static void updateTableAdapter(){
        tAdapter.refreshTables();
    }

    public static void updateRestaurantInfo(){
        GeneralInfo info = db.getRestaurantInfo();
        restaurantName.setText("Name: " + info.getName());
        restaurantStreet.setText("Street: " + info.getStreet());
        restaurantCity.setText("City: " + info.getCity());
        restaurantDescription.setText("Description: " + info.getDescription());
        restaurantPhone.setText("Phone: " + info.getContact().getPhone());
        restaurantEMail.setText("E-Mail: " + info.getContact().getMail());
    }

    private void initContentTable(){
        lv = (ListView) findViewById(R.id.tableListView);
        tAdapter = new TableAdapter(this);
        lv.setAdapter(tAdapter);
    }

    private void initContentSettings(){
        restaurantName = (TextView) findViewById(R.id.viewName);
        restaurantStreet = (TextView) findViewById(R.id.viewStreet);
        restaurantCity = (TextView) findViewById(R.id.viewCity);
        restaurantDescription = (TextView) findViewById(R.id.viewDescription);
        restaurantPhone = (TextView) findViewById(R.id.viewPhone);
        restaurantEMail = (TextView) findViewById(R.id.viewMail);
        restaurantInfoChange = (Button) findViewById(R.id.infoChange);

        updateRestaurantInfo();

        restaurantInfoChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(changeRestaurantInfo);
            }
        });
    }
}
