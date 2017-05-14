package kaist.customerapplication.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.kaist.antr.kaist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.RestaurantOwnerApplicationWrapper;
import kaist.customerapplication.adapters.MenuExpandableListAdapter;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;


public class MenuActivity extends AppCompatActivity {

    ExpandableListView menuListView;
    RestaurantOwnerApplicationWrapper restaurantOwnerApplicationWrapper;

    ExpandableListAdapter listAdapter;
    //ExpandableListView expListView;
    List<String> listDataHeaderLISTCLASS;
    HashMap<String, List<String>> listDataChildLISTCLASS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantOwnerApplicationWrapper = new RestaurantOwnerApplicationWrapper();
        //menuListView = (ExpandableListView) findViewById(R.id.menuList);
        //List<MenuItem> menuItemList = restaurantOwnerApplicationWrapper.getRestaurantInfo().menu.menuCategories.get(0).menuItems;

        //MenuItemAdapter arrayAdapter = new MenuItemAdapter(this,menuItemList);
        //menuListView.setAdapter(arrayAdapter);

        menuListView = (ExpandableListView) findViewById(R.id.menuList);
        Menu menu = restaurantOwnerApplicationWrapper.getRestaurantInfo().menu;
        //prepareListData();
        listAdapter = new MenuExpandableListAdapter(this, menu);
        menuListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeaderLISTCLASS = new ArrayList<String>();
        listDataChildLISTCLASS = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeaderLISTCLASS.add("Top 250");
        listDataHeaderLISTCLASS.add("Now Showing");
        listDataHeaderLISTCLASS.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChildLISTCLASS.put(listDataHeaderLISTCLASS.get(0), top250); // Header, Child data
        listDataChildLISTCLASS.put(listDataHeaderLISTCLASS.get(1), nowShowing);
        listDataChildLISTCLASS.put(listDataHeaderLISTCLASS.get(2), comingSoon);
    }
}
