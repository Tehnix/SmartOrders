package kaist.customerapplication.RestaurantOwnerApplicationCommunication.data;

public class OrderItem {
    public MenuItem menuItem;
    private int tableNumber;
    public int quantity;

    public OrderItem(MenuItem menuItem, int tableNumber) {
        this.menuItem = menuItem;
        this.quantity = 0;
        this.tableNumber = tableNumber;
    }

    public int addQuantity() {
        return ++quantity;
    }

    public int removeQuantity() {
        if(quantity>0){
            --quantity;
        }
        return quantity;
    }
}
