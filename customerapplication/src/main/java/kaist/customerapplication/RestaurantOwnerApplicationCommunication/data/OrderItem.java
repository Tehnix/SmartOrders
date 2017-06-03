package kaist.customerapplication.RestaurantOwnerApplicationCommunication.data;

public class OrderItem {
    public MenuItem menuItem;
    public int quantity;

    public OrderItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        this.quantity = 0;
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
