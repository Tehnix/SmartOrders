package kaist.restaurantownerapp.data;


import java.util.List;

public class Order {
    private int tableNumber;
    private List<OrderItem> orderItems;

    public Order(int tableNumber, List<OrderItem> orderItems) {
        this.tableNumber = tableNumber;
        this.orderItems = orderItems;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
