package kaist.customerapplication;

import kaist.customerapplication.RestaurantOwnerApplicationCommunication.data.Table;

public class TableManager {
    static Table table;

    public static void setTableNumber(int tableNumber){
        Table table = new Table(tableNumber);
    }
}
