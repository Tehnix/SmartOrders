package kaist.restaurantownerapp.data;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuEntry> menu;

    public Menu(List<MenuEntry> menu) {
        this.menu = menu;
    }

    public List<MenuEntry> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuEntry> menu) {
        this.menu = menu;
    }
}