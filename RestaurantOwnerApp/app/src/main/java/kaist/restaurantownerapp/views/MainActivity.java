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
import android.widget.ExpandableListView;
import android.widget.BaseExpandableListAdapter;
import android.util.Log;

import java.util.List;
import java.util.HashMap;

import kaist.restaurantownerapp.R;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.data.handler.DBConnector;
import kaist.restaurantownerapp.listviewhandler.MenuAdapter;
import kaist.restaurantownerapp.listviewhandler.OrderAdapter;
import kaist.restaurantownerapp.listviewhandler.TableAdapter;
import kaist.restaurantownerapp.communication.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewFlipper vf;
    private ListView tableListView;
    private ListView orderListView;
    private ContentState currentContent = ContentState.ORDERS;

    public static String [] prgmNameList={"1","2","3","4","5","6","7","8","9"};

    private Intent createNewTable;
    private Intent changeRestaurantInfo;
    private Intent createNemMenuItem;
    private Intent detailMenuItem;

    private FloatingActionButton fab;

    public static DBConnector db;

    public static TableAdapter tableAdapter;
    public static OrderAdapter orderAdapter;

    // Content Settings Views
    public static TextView restaurantName;
    public static TextView restaurantStreet;
    public static TextView restaurantCity;
    public static TextView restaurantDescription;
    public static TextView restaurantPhone;
    public static TextView restaurantEMail;
    public static Button restaurantInfoChange;

    private ExpandableListView menuListView;
    public static MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBConnector(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNewTable = new Intent(this, CreateTableActivity.class);
        createNewTable.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        changeRestaurantInfo = new Intent(this, ChangeRestaurantInfoActivity.class);
        changeRestaurantInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        createNemMenuItem = new Intent(this, CreateMenuItemActivity.class);
        createNemMenuItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        detailMenuItem = new Intent(this, DetailMenuActivity.class);
        detailMenuItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentContent == ContentState.TABLES){
                    startActivity(createNewTable);
                }

                if (currentContent == ContentState.MENU){
                    startActivity(createNemMenuItem);
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
        initMenuList();
        initOrderList();
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
        tableAdapter.refreshTables();
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

    public static void updateMenu(){
        menuAdapter.refreshMenu();
    }

    public static void updateOrders(){
        orderAdapter.refreshOrders();
    }


    private void initContentTable(){
        tableListView = (ListView) findViewById(R.id.tableListView);
        tableAdapter = new TableAdapter(this);
        tableListView.setAdapter(tableAdapter);
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

    private void initMenuList(){
        menuListView = (ExpandableListView) findViewById(R.id.menuList);
        menuAdapter = new MenuAdapter(this);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int i = (int) menuAdapter.getChildId(groupPosition, childPosition);
                kaist.restaurantownerapp.data.MenuItem childObject = (kaist.restaurantownerapp.data.MenuItem) menuAdapter.getChild(groupPosition, childPosition);
                detailMenuItem.putExtra("id", childObject.getId());
                startActivity(detailMenuItem);
                return false;
            }
        });
    }

    private void initOrderList(){
        orderListView = (ListView) findViewById(R.id.orderListView);
        orderAdapter = new OrderAdapter(this);
        orderListView.setAdapter(orderAdapter);
    }
}
