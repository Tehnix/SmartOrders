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
        RestaurantInfo restaurantInfo = RestaurantInfoJsonSerializer.deserializeToRestaurantInfo(EXAMPLE_JSON);
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

    private final String EXAMPLE_JSON = "{\n" +
            "  \"info\": {\n" +
            "    \"name\": \"Gosomi Chicken\",\n" +
            "    \"location\": \"21st Cool Street, Daejeon\",\n" +
            "    \"description\": \"Awesome fried chicken!\",\n" +
            "    \"contact\": {\n" +
            "      \"phone\": \"010-9999-9999\",\n" +
            "      \"mail\": \"chicken@example.com\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"menu\": {\n" +
            "    \"menuCategories\": [\n" +
            "      {\n" +
            "        \"name\": \"food\",\n" +
            "        \"menuItems\": [\n" +
            "          {\n" +
            "            \"id\": 12,\n" +
            "            \"name\": \"Tender Crispy Chicken\",\n" +
            "            \"price\": 9000.00,\n" +
            "            \"description\": \"Very tender chicken\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 13,\n" +
            "            \"name\": \"Hot Chicken\",\n" +
            "            \"price\": 12000.00,\n" +
            "            \"description\": \"Careful! Quite hot chicken\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 14,\n" +
            "            \"name\": \"Steak\",\n" +
            "            \"price\": 15000.00,\n" +
            "            \"description\": \"A nice well seasoned steak\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"beverage\",\n" +
            "        \"menuItems\": [\n" +
            "          {\n" +
            "            \"id\": 15,\n" +
            "            \"name\": \"Coca Cola\",\n" +
            "            \"price\": 500.00,\n" +
            "            \"description\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 16,\n" +
            "            \"name\": \"Pepsi\",\n" +
            "            \"price\": 600.00,\n" +
            "            \"description\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 17,\n" +
            "            \"name\": \"Cass Beer\",\n" +
            "            \"price\": 3000.00,\n" +
            "            \"description\": \"Good for stamina!\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 18,\n" +
            "            \"name\": \"Soju\",\n" +
            "            \"price\": 3000.00,\n" +
            "            \"description\": \"Very refreshing!\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Side-dishes\",\n" +
            "        \"menuItems\": [\n" +
            "          {\n" +
            "            \"id\": 19,\n" +
            "            \"name\": \"Pommes Frites\",\n" +
            "            \"price\": 4500.00,\n" +
            "            \"description\": \"Crispy oven made fries\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 17,\n" +
            "            \"name\": \"Ketchup\",\n" +
            "            \"price\": 100.00,\n" +
            "            \"description\": \"\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": 17,\n" +
            "            \"name\": \"Mustard\",\n" +
            "            \"price\": 100.00,\n" +
            "            \"description\": \"\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}\n";
}