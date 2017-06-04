package kaist.customerapplication.RestaurantOwnerApplicationCommunication;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.ContactInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.GeneralInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuEntry;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuItem;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Order;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;

import static org.junit.Assert.assertTrue;

public class JsonConverterUnitTest {

    @Test
    public void canConvertMenu() throws Exception {
        Menu expectedMenu = generateExpectedMenuOutput();

        Menu actualMenu = RestaurantInfoJsonSerializer.deserialize(TestJsonData.EXAMPLE_JSON_INFO).menu;

        for (MenuEntry category: expectedMenu.menuCategories) {
            int pos = expectedMenu.menuCategories.indexOf(category);
            String expectedCategoryName = category.name;
            String actualCategoryName = actualMenu.menuCategories.get(pos).name;
            assertTrue(actualCategoryName.equals(expectedCategoryName));
        }
    }

    @Test
    public void canSerializeInfoToAndFromJson() throws Exception {
        RestaurantInfo resInfo = RestaurantInfoJsonSerializer.deserialize(TestJsonData.EXAMPLE_JSON_INFO);
        String serializedjson = RestaurantInfoJsonSerializer.serialize(resInfo);
        assertTrue(serializedjson.equals(TestJsonData.EXAMPLE_JSON_INFO));

    }

    @Test
    public void canSerializeOrderToAndFromJson() throws Exception {
        Order order = OrderJsonSerializer.deserialize(TestJsonData.EXAMPLE_JSON_ORDER);
        String serializedjson = OrderJsonSerializer.serialize(order);
        assertTrue(serializedjson.equals(TestJsonData.EXAMPLE_JSON_ORDER));

    }

    @Ignore
    @Test
    public void canDeserializeOrderFromJson() throws Exception {
        Order order = OrderJsonSerializer.deserialize(TestJsonData.EXAMPLE_JSON_ORDER);
        assertTrue(true);
    }

    @Ignore
    @Test
    public void canDeserializeInfoFromJson() throws Exception {
        RestaurantInfo resInfo = RestaurantInfoJsonSerializer.deserialize(TestJsonData.EXAMPLE_JSON_INFO);
        assertTrue(true);
    }

    private Menu generateExpectedMenuOutput(){
        Menu menu = new Menu();
        MenuEntry entry = new MenuEntry();
        entry.name = "food";
        menu.menuCategories.add(entry);

        entry = new MenuEntry();
        entry.name = "beverage";
        menu.menuCategories.add(entry);

        entry = new MenuEntry();
        entry.name = "Side-dishes";
        menu.menuCategories.add(entry);

        return menu;
    }

    private RestaurantInfo createRestaurantInfoData(){
        RestaurantInfo restaurantInfo = new RestaurantInfo();
        restaurantInfo.info = createInfo();
        restaurantInfo.menu = createMenu();
        return restaurantInfo;
    }

    private GeneralInfo createInfo() {
        GeneralInfo info = new GeneralInfo("Gosomi Chicken", "21st Cool Street", "Daejeon", "Awesome fried chicken!", createContactInfo());
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



    private static class TestJsonData{
        public final static String EXAMPLE_JSON_INFO = "{\n" +
                "  \"info\": {\n" +
                "    \"name\": \"Gosomi Chicken\",\n" +
                "    \"street\": \"21st Cool Street\",\n" +
                "    \"city\": \"Daejeon\",\n" +
                "    \"description\": \"Awesome fried chicken!\",\n" +
                "    \"contact\": {\n" +
                "      \"phone\": \"010-9999-9999\",\n" +
                "      \"mail\": \"chicken@example.com\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"menu\": {\n" +
                "    \"menuCategories\": [\n" +
                "      {\n" +
                "        \"name\": \"Main-dishes\",\n" +
                "        \"menuItems\": [\n" +
                "          {\n" +
                "            \"id\": 12,\n" +
                "            \"category\": \"Main-dishes\",\n" +
                "            \"name\": \"Tender Crispy Chicken\",\n" +
                "            \"price\": 9000.00,\n" +
                "            \"description\": \"Very tender chicken\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 13,\n" +
                "            \"category\": \"Main-dishes\",\n" +
                "            \"name\": \"Hot Chicken\",\n" +
                "            \"price\": 12000.00,\n" +
                "            \"description\": \"Careful! Quite hot chicken\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 14,\n" +
                "            \"category\": \"Main-dishes\",\n" +
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
                "            \"category:\": \"beverage\",\n" +
                "            \"name\": \"Coca Cola\",\n" +
                "            \"price\": 500.00,\n" +
                "            \"description\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 16,\n" +
                "            \"category:\": \"beverage\",\n" +
                "            \"name\": \"Pepsi\",\n" +
                "            \"price\": 600.00,\n" +
                "            \"description\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 17,\n" +
                "            \"category:\": \"beverage\",\n" +
                "            \"name\": \"Cass Beer\",\n" +
                "            \"price\": 3000.00,\n" +
                "            \"description\": \"Good for stamina!\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 18,\n" +
                "            \"category:\": \"beverage\",\n" +
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
                "            \"category\": \"Side-dishes\",\n" +
                "            \"name\": \"Pommes Frites\",\n" +
                "            \"price\": 4500.00,\n" +
                "            \"description\": \"Crispy oven made fries\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 17,\n" +
                "            \"category\": \"Side-dishes\",\n" +
                "            \"name\": \"Ketchup\",\n" +
                "            \"price\": 100.00,\n" +
                "            \"description\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 17,\n" +
                "            \"category\": \"Side-dishes\",\n" +
                "            \"name\": \"Mustard\",\n" +
                "            \"price\": 100.00,\n" +
                "            \"description\": \"\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        public final static String EXAMPLE_JSON_ORDER = "{\n" +
                "  \"tableNumber\": 25,\n" +
                "  \"orderItems\": [\n" +
                "    { \n" +
                "      \"tableNumber\": 1,\n" +
                "      \"menuItem\": {\n" +
                "        \"id\": 12,\n" +
                "        \"category\": \"Main-dishes\",\n" +
                "        \"name\": \"Tender Crispy Chicken\",\n" +
                "        \"price\": 9000.00,\n" +
                "        \"description\": \"Very tender chicken\"\n" +
                "      },\n" +
                "      \"quantity\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"tableNumber\": 2,\n" +
                "      \"menuItem\": {\n" +
                "        \"id\": 13,\n" +
                "        \"category\": \"Main-dishes\",\n" +
                "        \"name\": \"Hot Chicken\",\n" +
                "        \"price\": 12000.00,\n" +
                "        \"description\": \"Careful! Quite hot chicken\"\n" +
                "      },\n" +
                "      \"quantity\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }


}