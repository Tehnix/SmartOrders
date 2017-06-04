package kaist.restaurantownerapp.data;

public class OrderItem {
    private MenuItem menuItem;
    private int tableNumber;
    private int quantity;

    public OrderItem(MenuItem menuItem, int tableNumber, int quantity) {
        this.menuItem = menuItem;
        this.tableNumber = tableNumber;
        this.quantity = quantity;
    }

    public OrderItem(){}

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
