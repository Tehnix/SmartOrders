package kaist.restaurantownerapp.data;

public class OrderItemDB extends OrderItem {
    private int id;

    public OrderItemDB(int id, MenuItem menuItem, int tableNumber, int quantity) {
        super(menuItem, tableNumber, quantity);
        this.id = id;
    }

    public OrderItemDB(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
