package kaist.customerapplication.RestaurantOwnerApplicationCommunication.data;


import java.util.ArrayList;
import java.util.List;

public class Order {
    public int tableNumber;
    public List<OrderItem> orderItems;

    public Order(int tableNumber, List<OrderItem> orderItems) {
        this.tableNumber = tableNumber;
        this.orderItems = orderItems;
    }

    public Order(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Order() {
        orderItems = new ArrayList<OrderItem>();
    }

    public int addMenuItem(MenuItem menuItem){
        OrderItem orderItem;
        int menuItemIndex = indexOf(menuItem);
        if(menuItemIndex>=0){
            orderItem = orderItems.get(menuItemIndex);
        }else{
            orderItem = new OrderItem(menuItem, tableNumber);
            orderItems.add(orderItem);
        }
        return orderItem.addQuantity();
    }

    public int removeMenuItem(MenuItem menuItem){
        OrderItem orderItem;
        int menuItemIndex = indexOf(menuItem);
        if(menuItemIndex>=0){
            orderItem = orderItems.get(menuItemIndex);
            int quantity = orderItem.removeQuantity();
            if(quantity==0){
                orderItems.remove(menuItemIndex);
            }
        }else{
            return 0;
        }
        return orderItem.quantity;
    }

    private int indexOf(MenuItem menuItem) {
        for(int i = 0; i<orderItems.size(); i++){
            if(orderItems.get(i).menuItem.id == menuItem.id){
                return i;
            }
        }
        return -1;
    }

}
