package kaist.restaurantownerapp.data;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuEntry> menuCategories;

    public Menu(List<MenuEntry> menu) {
        this.menuCategories = menu;
    }

    public List<MenuEntry> getMenu() {
        return menuCategories;
    }

    public void setMenu(List<MenuEntry> menu) {
        this.menuCategories = menu;
    }
}