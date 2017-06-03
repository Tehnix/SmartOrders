package kaist.customerapplication.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kaist.antr.kaist.R;

import kaist.customerapplication.CommonObjectManager;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.RestaurantOwnerApplicationWrapper;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.ContactInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.GeneralInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RestaurantOwnerApplicationWrapper restaurantOwnerApplicationWrapper;
    ConstraintLayout mContentView;
    RestaurantInfo restaurantInfo;

    TextView nameView;
    TextView descriptionView;
    TextView addressView;
    TextView emailView;
    TextView phoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        restaurantOwnerApplicationWrapper = CommonObjectManager.restaurantOwnerApplicationWrapper;
        restaurantInfo = restaurantOwnerApplicationWrapper.getRestaurantInfo();

        mContentView = (ConstraintLayout) findViewById(R.id.mainContent);
        if(restaurantInfo != null){
            setRestaurantInfoView(restaurantInfo);
        }else{
            setNoRestaurantView();
        }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_see_menu) {
            startActivity(MenuActivity.class);
        } else if (id == R.id.nav_scan_tag) {
            startActivity(ScanTagActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivity(Class activityClass){
        Intent intent = new Intent(getApplicationContext(), activityClass);
        startActivity(intent);
    }

    private void setRestaurantInfoView(RestaurantInfo resInfo){
        GeneralInfo genInfo = resInfo.info;
        ContactInfo contact = genInfo.getContact();

        mContentView.removeAllViews();
        View restaurantInfoView = getLayoutInflater().inflate(R.layout.restaurant_info_layout, null);
        mContentView.addView(restaurantInfoView);

        nameView = (TextView) findViewById(R.id.resNameText);
        descriptionView = (TextView) findViewById(R.id.descriptionText);
        addressView = (TextView) findViewById(R.id.addressText);
        emailView = (TextView) findViewById(R.id.emailText);
        phoneView = (TextView) findViewById(R.id.phoneText);

        nameView.setText(genInfo.getName());
        descriptionView.setText(genInfo.getDescription());
        addressView.setText(genInfo.getStreet() + ", "  +genInfo.getCity());
        emailView.setText(contact.mail);
        phoneView.setText(contact.phone);

    }

    private void setNoRestaurantView(){
        mContentView.removeAllViews();
        View restaurantInfoView = getLayoutInflater().inflate(R.layout.no_restaurant_layout, null);
        mContentView.addView(restaurantInfoView);
    }
}
