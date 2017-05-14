package kaist.customerapplication.RestaurantOwnerApplicationCommunication;


import java.util.ArrayList;
import java.util.List;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.ContactInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.GeneralInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuEntry;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuItem;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;

public class RestaurantOwnerApplicationWrapper {
    public RestaurantInfo getRestaurantInfo(){
        //TODO: implement
        RestaurantInfo restaurantInfo = createRestaurantInfoDummy();
        return restaurantInfo;
    }

    private RestaurantInfo createRestaurantInfoDummy() {
        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.info = createInfo();
        restaurantInfo.menu = createMenu();
        return restaurantInfo;
    }

    private GeneralInfo createInfo() {
        GeneralInfo info = new GeneralInfo();
        info.name ="Gosomi Chicken";
        info.location ="21st Cool Street, Daejeon";
        info.description ="Awesome fried chicken!";
        info.contact = createContactInfo();
        return info;
    }

    private ContactInfo createContactInfo() {
        ContactInfo contact = new ContactInfo();
        contact.phone = "010-9999-9999";
        contact.mail = "chicken@example.com";
        return contact;
    }

    private Menu createMenu() {
        Menu menu = new Menu();
        menu.menuCategories = populateCategories();
        return menu;
    }

    private List<MenuEntry> populateCategories() {
        List<MenuEntry> categories = new ArrayList<>();
        categories.add(createFoodCategory());
        categories.add(createBeverageCategory());
        return categories;
    }

    private MenuEntry createFoodCategory() {
        MenuEntry foods = new MenuEntry();
        foods.name = "Foods";
        //MenuEntry chicken = new MenuEntry();
        foods.menuItems.add(createMenuItem(12,"Tender Crispy Chicken",9000.00,"Very tender chicken"));
        //foods.menuItems.add(chicken);
        foods.menuItems.add(createMenuItem(14, "Steak", 15000.00, "A nice well seasoned steak"));
        return foods;
    }

    private MenuItem createMenuItem(int id, String name, double price, String description){
        MenuItem item = new MenuItem();
        item.id = id;
        item.name = name;
        item.price = price;
        item.description = description;
        return item;
    }

    private MenuEntry createBeverageCategory() {
        MenuEntry bevs = new MenuEntry();
        bevs.name = "Beverages";
        bevs.menuItems.add(createMenuItem(15, "Coca Cola", 500.00, ""));
        return bevs;
    }
}