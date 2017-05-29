package kaist.restaurantownerapp.data;

public class Table {
    private int tableNumber;
    private int tableSeats;
    private boolean tableState;

    public Table(){};

    public Table(int number, int seats, boolean state){
        this.tableNumber = number;
        this.tableSeats = seats;
        this.tableState = state;
    }

    public int getTableNumber(){
        return tableNumber;
    }

    public void setTableNumber(int number){
        tableNumber = number;
    }

    public int getTableSeats(){
        return tableSeats;
    }

    public void setTableSeats(int seats){
        tableSeats = seats;
    }

    public boolean getTableState(){
        return tableState;
    }

    public void setTableState(boolean state){
        tableState = state;
    }
}
