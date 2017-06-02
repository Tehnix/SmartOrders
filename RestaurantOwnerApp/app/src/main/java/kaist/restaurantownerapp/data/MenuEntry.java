package kaist.restaurantownerapp.data;


import java.util.ArrayList;
import java.util.List;

public class MenuEntry {
    private String name = "";
    private List<MenuItem> menuItems = new ArrayList<>();

    public MenuEntry(String name, List<MenuItem> menuItems) {
        this.name = name;
        this.menuItems = menuItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
