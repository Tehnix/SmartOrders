package kaist.restaurantownerapp.data;

public class RestaurantInfo {
    private GeneralInfo info;
    private Menu menu;

    public RestaurantInfo(GeneralInfo info, Menu menu) {
        this.info = info;
        this.menu = menu;
    }

    public RestaurantInfo(){};

    public GeneralInfo getInfo() {
        return info;
    }

    public void setInfo(GeneralInfo info) {
        this.info = info;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}