package kaist.customerapplication.RestaurantOwnerApplicationCommunication;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.ContactInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.GeneralInfo;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Menu;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuEntry;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.MenuItem;
import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.RestaurantInfo;

import static org.junit.Assert.assertTrue;

public class JsonConverterUnitTest {

    @Test
    public void canConvertMenu() throws Exception {
        Menu expectedMenu = generateExpectedMenuOutput();

        Menu actualMenu = RestaurantInfoJsonSerializer.deserializeToRestaurantInfo(TestJsonData.EXAMPLE_JSON).menu;

        for (MenuEntry category: expectedMenu.menuCategories) {
            int pos = expectedMenu.menuCategories.indexOf(category);
            String expectedCategoryName = category.name;
            String actualCategoryName = actualMenu.menuCategories.get(pos).name;
            assertTrue(actualCategoryName.equals(expectedCategoryName));
        }
    }

    @Test
    public void canSerializeInfoToAndFromJson() throws Exception {
        RestaurantInfo resInfo = RestaurantInfoJsonSerializer.deserializeToRestaurantInfo(TestJsonData.EXAMPLE_JSON);
        String serializedjson = RestaurantInfoJsonSerializer.serializeToJson(resInfo);
        assertTrue(serializedjson.equals(TestJsonData.EXAMPLE_JSON));

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

    private static class TestJsonData{
        public final static String EXAMPLE_JSON = "{\"info\":{\"name\":\"Gosomi Chicken\",\"location\":\"21st Cool Street, Daejeon\",\"description\":\"Awesome fried chicken!\",\"contact\":{\"phone\":\"010-9999-9999\",\"mail\":\"chicken@example.com\"}},\"menu\":{\"menuCategories\":[{\"name\":\"food\",\"menuItems\":[{\"id\":12,\"name\":\"Tender Crispy Chicken\",\"price\":9000.0,\"description\":\"Very tender chicken\"},{\"id\":13,\"name\":\"Hot Chicken\",\"price\":12000.0,\"description\":\"Careful! Quite hot chicken\"},{\"id\":14,\"name\":\"Steak\",\"price\":15000.0,\"description\":\"A nice well seasoned steak\"}]},{\"name\":\"beverage\",\"menuItems\":[{\"id\":15,\"name\":\"Coca Cola\",\"price\":500.0,\"description\":\"\"},{\"id\":16,\"name\":\"Pepsi\",\"price\":600.0,\"description\":\"\"},{\"id\":17,\"name\":\"Cass Beer\",\"price\":3000.0,\"description\":\"Good for stamina!\"},{\"id\":18,\"name\":\"Soju\",\"price\":3000.0,\"description\":\"Very refreshing!\"}]},{\"name\":\"Side-dishes\",\"menuItems\":[{\"id\":19,\"name\":\"Pommes Frites\",\"price\":4500.0,\"description\":\"Crispy oven made fries\"},{\"id\":17,\"name\":\"Ketchup\",\"price\":100.0,\"description\":\"\"},{\"id\":17,\"name\":\"Mustard\",\"price\":100.0,\"description\":\"\"}]}]}}";
    }


}