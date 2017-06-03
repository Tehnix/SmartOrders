package kaist.restaurantownerapp.data.handler;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import java.util.List;

import kaist.restaurantownerapp.data.ContactInfo;
import kaist.restaurantownerapp.data.GeneralInfo;
import kaist.restaurantownerapp.data.Menu;
import kaist.restaurantownerapp.data.MenuEntry;
import kaist.restaurantownerapp.data.MenuItem;
import kaist.restaurantownerapp.data.Order;
import kaist.restaurantownerapp.data.OrderItem;
import kaist.restaurantownerapp.data.Table;
import kaist.restaurantownerapp.listviewhandler.OrderAdapter;

public class DBConnector extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "SmartOrder.db";

    // Tables Table
    private static final String TABLES_TABLE_NAME = "tables";
    private static final String TABLES_COLUMN_NUMBER = "number";
    private static final String TABLES_COLUMN_SEATS = "seats";
    private static final String TABLES_COLUMN_STATE = "state";

    // Restaurant Table
    private static final String RESTAURANT_TABLE_NAME = "restaurant";
    private static final String RESTAURANT_COLUMN_ID = "id";
    private static final String RESTAURANT_COLUMN_NAME = "name";
    private static final String RESTAURANT_COLUMN_STREET = "street";
    private static final String RESTAURANT_COLUMN_CITY = "city";
    private static final String RESTAURANT_COLUMN_DESCRIPTION = "description";
    private static final String RESTAURANT_COLUMN_PHONE = "phone";
    private static final String RESTAURANT_COLUMN_EMAIL = "email";

    // Menu Table
    private static final String MENU_TABLE_NAME = "menuitem";
    private static final String MENU_COLUMN_ID = "id";
    private static final String MENU_COLUMN_NAME = "name";
    private static final String MENU_COLUMN_DESCRIPTION = "description";
    private static final String MENU_COLUMN_PRICE = "price";
    private static final String MENU_COLUMN_CATEGORY = "category";

    // Orders Table
    private static final String ORDERS_TABLE_NAME = "orders";
    private static final String ORDERS_COLUMN_ID = "id";
    private static final String ORDERS_COLUMN_TABLEID = "tableid";
    private static final String ORDERS_COLUMN_MENUID = "menuid";
    private static final String ORDERS_COLUMN_NUMBER = "number";

    private HashMap hp;

    public DBConnector(Context context) {

        super(context, DATABASE_NAME , null, 1);
        Log.d("dbConnector", "constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dbConnector", "onCreate");

        try{
            db.execSQL(
                    "CREATE TABLE tables " +
                            "(number INTEGER PRIMARY KEY NOT NULL, seats INTEGER NOT NULL, state INTEGER NOT NULL)"
            );
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS restaurant " +
                            "(id INTEGER PRIMARY KEY, name TEXT, street TEXT,city TEXT, description TEXT, phone TEXT, email TEXT)"
            );
            generateDefaultRestaurantInfo(db);
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS menuitem " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, category TEXT NOT NULL,  description TEXT, price INTEGER NOT NULL)"
            );
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS orders " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT, tableid INTEGER, menuid INTEGER, number INTEGER NOT NULL, FOREIGN KEY(tableid) REFERENCES tables(id), FOREIGN KEY(menuid) REFERENCES menu(id))"
            );
            Log.d("db test", "test");
        }catch (SQLException e){
            e.printStackTrace();
            Log.d("db error", e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tables");
        db.execSQL("DROP TABLE IF EXISTS restaurant");
        db.execSQL("DROP TABLE IF EXISTS menu");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    // Adding new table
    public boolean addTable(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLES_COLUMN_NUMBER, table.getTableNumber());
        contentValues.put(TABLES_COLUMN_SEATS, table.getTableSeats());
        contentValues.put(TABLES_COLUMN_STATE, table.getTableState());
        db.insert(TABLES_TABLE_NAME, null, contentValues);
        return true;
    }

    // Getting one table
    public Table getTable(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLES_TABLE_NAME, new String[] {
                        TABLES_COLUMN_NUMBER, TABLES_COLUMN_SEATS, TABLES_COLUMN_STATE}, TABLES_COLUMN_NUMBER + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Table table = new Table(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), Boolean.parseBoolean(cursor.getString(1)));
        return table;
    }

    // Getting All Tables
    public List<Table> getAllTables() {
        List<Table> tableList = new ArrayList<Table>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLES_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Table table = new Table();
                table.setTableNumber(Integer.parseInt(cursor.getString(0)));
                table.setTableSeats(Integer.parseInt(cursor.getString(1)));
                table.setTableState(Boolean.parseBoolean(cursor.getString(2)));
                // Adding contact to list
                tableList.add(table);
            } while (cursor.moveToNext());
        }
        // return table list
        return tableList;
    }

    // Updating single table
    public int updateTable(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLES_COLUMN_SEATS, table.getTableSeats());
        values.put(TABLES_COLUMN_STATE, table.getTableState());

        // updating row
        return db.update(TABLES_TABLE_NAME, values, TABLES_COLUMN_NUMBER + " = ?",
                new String[] { String.valueOf(table.getTableNumber())});
    }

    // Deleting single table
    public void deleteTable(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLES_TABLE_NAME, TABLES_COLUMN_NUMBER + " = ?",
                new String[] { String.valueOf(table.getTableNumber())});
        db.close();
    }

    // Getting Number of Tables
    public int getCountTables(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLES_TABLE_NAME);
        db.close();
        return numRows;
    }

    // Generate default Restaurant Info
    public boolean generateDefaultRestaurantInfo(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT_COLUMN_ID, 0);
        contentValues.put(RESTAURANT_COLUMN_NAME, "Name");
        contentValues.put(RESTAURANT_COLUMN_STREET, "Street");
        contentValues.put(RESTAURANT_COLUMN_CITY, "City");
        contentValues.put(RESTAURANT_COLUMN_DESCRIPTION, "Description");
        contentValues.put(RESTAURANT_COLUMN_PHONE, "Phone");
        contentValues.put(RESTAURANT_COLUMN_EMAIL, "E-Mail");
        db.insert(RESTAURANT_TABLE_NAME, null, contentValues);
        return true;
    }

    // Getting Restaurant Info
    public GeneralInfo getRestaurantInfo(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(RESTAURANT_TABLE_NAME, new String[] {
                        RESTAURANT_COLUMN_NAME, RESTAURANT_COLUMN_STREET, RESTAURANT_COLUMN_CITY, RESTAURANT_COLUMN_DESCRIPTION, RESTAURANT_COLUMN_PHONE, RESTAURANT_COLUMN_EMAIL}, RESTAURANT_COLUMN_ID + "=?",
                new String[] { String.valueOf(0) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        GeneralInfo info = new GeneralInfo(cursor.getString(0),cursor.getString(1),cursor.getString(2), cursor.getString(3), new ContactInfo(cursor.getString(4), cursor.getString(5)));
        return info;
    }

    // Update Restaurant Info
    public int updateRestaurantInfo(GeneralInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RESTAURANT_COLUMN_ID, 0);
        values.put(RESTAURANT_COLUMN_NAME, info.getName());
        values.put(RESTAURANT_COLUMN_STREET, info.getStreet());
        values.put(RESTAURANT_COLUMN_CITY, info.getCity());
        values.put(RESTAURANT_COLUMN_DESCRIPTION, info.getDescription());
        values.put(RESTAURANT_COLUMN_PHONE, info.getContact().getPhone());
        values.put(RESTAURANT_COLUMN_EMAIL, info.getContact().getMail());
        // updating row
        return db.update(RESTAURANT_TABLE_NAME, values, RESTAURANT_COLUMN_ID + " = ?",
                new String[] { String.valueOf(0)});
    }

    // Adding new Menu Item
    public boolean addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MENU_COLUMN_NAME, item.getName());
        contentValues.put(MENU_COLUMN_CATEGORY, item.getCategory());
        contentValues.put(MENU_COLUMN_DESCRIPTION, item.getDescription());
        contentValues.put(MENU_COLUMN_PRICE, item.getPrice());
        db.insert(MENU_TABLE_NAME, null, contentValues);
        return true;
    }

    // Getting one Menu Item
    public MenuItem getMenuItem(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MENU_TABLE_NAME, new String[] {
                        MENU_COLUMN_ID, MENU_COLUMN_NAME, MENU_COLUMN_CATEGORY, MENU_COLUMN_DESCRIPTION, MENU_COLUMN_PRICE}, MENU_COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MenuItem item = new MenuItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3), Double.parseDouble(cursor.getString(4)));
        return item;
    }

    // Getting Menu
    public Menu getMenu() {

        List<MenuItem> drinksList = new ArrayList<>();
        List<MenuItem> mainDishList = new ArrayList<>();
        List<MenuItem> dessertList = new ArrayList<>();
        List<MenuItem> appetizerList = new ArrayList<>();
        List<MenuEntry> menu = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + MENU_TABLE_NAME + " ORDER BY " + MENU_COLUMN_CATEGORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)));
                // Adding Menu Item to list
                String test = item.getCategory();
                if (item.getCategory().equals("main dishes")) {
                    mainDishList.add(item);
                } else if(item.getCategory().equals("appetizer")){
                    appetizerList.add(item);
                } else if(item.getCategory().equals("drink")){
                    drinksList.add(item);
                }else if(item.getCategory().equals("dessert")) {
                    dessertList.add(item);
                }
            } while (cursor.moveToNext());
        }

        menu.add(new MenuEntry("main dishes", mainDishList));
        menu.add(new MenuEntry("appetizer", appetizerList));
        menu.add(new MenuEntry("drinks", drinksList));
        menu.add(new MenuEntry("dessert", dessertList));
        return new Menu(menu);
    }

    // Updating single menu item
    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MENU_COLUMN_NAME, item.getName());
        values.put(MENU_COLUMN_CATEGORY, item.getCategory());
        values.put(MENU_COLUMN_DESCRIPTION, item.getDescription());
        values.put(MENU_COLUMN_PRICE, item.getPrice());

        // updating row
        return db.update(MENU_TABLE_NAME, values, MENU_COLUMN_ID + " = ?",
                new String[] {String.valueOf(item.getId())});
    }

    // Deleting single menu item
    public void deleteMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MENU_TABLE_NAME, MENU_COLUMN_ID + " = ?",
                new String[] { String.valueOf(item.getId())});
        db.close();
    }

    // Adding new Menu Item
    public boolean addOrder(OrderItem order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDERS_COLUMN_MENUID, order.getMenuItem().getId());
        contentValues.put(ORDERS_COLUMN_TABLEID, order.getTableNumber());
        contentValues.put(ORDERS_COLUMN_NUMBER, order.getQuantity());
        db.insert(ORDERS_TABLE_NAME, null, contentValues);
        return true;
    }

    // Getting one Menu Item
    public OrderItem getOrderItem(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ORDERS_TABLE_NAME, new String[] {
                        ORDERS_COLUMN_MENUID, ORDERS_COLUMN_TABLEID, ORDERS_COLUMN_NUMBER}, ORDERS_COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MenuItem menuItem = getMenuItem(Integer.parseInt(cursor.getString(0)));

        OrderItem orderItem = new OrderItem(menuItem, Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)));

        return orderItem;
    }

    public List<OrderItem> getOrderItems(){
        List<OrderItem> orderList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ORDERS_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MenuItem menuItem = getMenuItem(Integer.parseInt(cursor.getString(0)));
                OrderItem orderItem = new OrderItem(menuItem, Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                orderList.add(orderItem);
            } while (cursor.moveToNext());
        }
        return orderList;
    }
}
