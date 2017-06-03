package kaist.customerapplication.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaist.antr.kaist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaist.customerapplication.CommonObjectManager;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.RestaurantOwnerApplicationWrapper;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuItem;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Order;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.OrderItem;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;
import kaist.customerapplication.adapters.MenuExpandableListAdapter;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;


public class MenuActivity extends AppCompatActivity {

    ExpandableListView menuListView;
    RestaurantOwnerApplicationWrapper restaurantOwnerApplicationWrapper;

    ExpandableListAdapter listAdapter;
    //ExpandableListView expListView;
    List<String> listDataHeaderLISTCLASS;
    HashMap<String, List<String>> listDataChildLISTCLASS;

    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantOwnerApplicationWrapper = CommonObjectManager.restaurantOwnerApplicationWrapper;
        order = new Order();
        //menuListView = (ExpandableListView) findViewById(R.id.menuList);
        //List<MenuItem> menuItemList = restaurantOwnerApplicationWrapper.getRestaurantInfo().menu.menuCategories.get(0).menuItems;

        //MenuItemAdapter arrayAdapter = new MenuItemAdapter(this,menuItemList);
        //menuListView.setAdapter(arrayAdapter);

        menuListView = (ExpandableListView) findViewById(R.id.menuList);
        RestaurantInfo restaurantInfo = restaurantOwnerApplicationWrapper.getRestaurantInfo();
        Menu menu = null;
        if(restaurantInfo!=null){
            menu = restaurantInfo.menu;
            listAdapter = new MenuExpandableListAdapter(this, menu);
            menuListView.setAdapter(listAdapter);
        }

        //prepareListData();

    }

    public void addItem(View view) {
        int childPosition=(Integer)view.getTag(R.string.childPosition);
        int groupPosition=(Integer)view.getTag(R.string.groupPosition);
        MenuItem menuItem = (MenuItem) listAdapter.getChild(groupPosition,childPosition);

        int itemQuantity = order.addMenuItem(menuItem);

        TextView amountText = (TextView) view.getTag(R.string.amountTextView);
        amountText.setText(Integer.toString(itemQuantity));

    }

    public void removeItem(View view) {
        int childPosition=(Integer)view.getTag(R.string.childPosition);
        int groupPosition=(Integer)view.getTag(R.string.groupPosition);
        MenuItem menuItem = (MenuItem) listAdapter.getChild(groupPosition,childPosition);

        int itemQuantity = order.removeMenuItem(menuItem);

        TextView amountText = (TextView) view.getTag(R.string.amountTextView);
        amountText.setText(Integer.toString(itemQuantity));
    }

    public void placeOrder(View view) {
        try{
            restaurantOwnerApplicationWrapper.orderFromMenu(order);
            Toast.makeText(this, "Order successfully placed!", Toast.LENGTH_SHORT).show();
            goBackToMainActivity();
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong, could not place order.", Toast.LENGTH_SHORT).show();
        }

        //TODO: implement
    }

    private void goBackToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


}
