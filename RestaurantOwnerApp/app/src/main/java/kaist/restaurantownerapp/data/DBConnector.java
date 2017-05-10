package kaist.restaurantownerapp.data;

/**
 * Created by Michael on 11.05.2017.
 */
import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBConnector extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "SmartOrders.db";

    // Tables Table
    private static final String TABLES_TABLE_NAME = "tables";
    private static final String TABLES_COLUMN_ID = "id";
    private static final String TABLES_COLUMN_NUMBER = "number";
    private static final String TABLES_COLUMN_SEATS = "seats";
    private static final String TABLES_COLUMN_STATE = "state";

    // Restaurant Table
    private static final String RESTAURANT_TABLE_NAME = "restaurant";
    private static final String RESTAURANT_COLUMN_ID = "id";
    private static final String RESTAURANT_COLUMN_NAME = "name";
    private static final String RESTAURANT_COLUMN_LOCATION = "location";
    private static final String RESTAURANT_COLUMN_DESCRIPTION = "description";
    private static final String RESTAURANT_COLUMN_PHONE = "phone";
    private static final String RESTAURANT_COLUMN_EMAIL = "email";

    // Menu Table
    private static final String MENU_TABLE_NAME = "menu";
    private static final String MENU_COLUMN_NAME = "name";
    private static final String MENU_COLUMN_DESCRIPTION = "description";
    private static final String MENU_COLUMN_PRICE = "price";
    private static final String MENU_COLUMN_CATEGORIE = "categorie";
    private static final String MENU_COLUMN_SUBCATEGORIE = "subcategorie";

    // Orders Table
    private static final String ORDERS_TABLE_NAME = "orders";
    private static final String ORDERS_COLUMN_ID = "id";
    private static final String ORDERS_COLUMN_TABLEID = "tableid";
    private static final String ORDERS_COLUMN_MENUID = "menuid";
    private static final String ORDERS_COLUMN_NUMBER = "number";

    private HashMap hp;

    public DBConnector(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table tables " +
                        "(id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, number INTEGER NOT NULL, seats INTEGER NOT NULL)"
        );
        db.execSQL(
                "create table restaurant " +
                        "(name TEXT NOT NULL, location TEXT NOT NULL, description TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL)"
        );
        db.execSQL(
                "create table menu " +
                        "(id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, categorie TEXT NOT NULL, subcategorie TEXT NOT NULL, price INTEGER NOT NULL)"
        );
        db.execSQL(
                "create table orders " +
                        "(id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, tableid INTEGER, menuid INTEGER, number INTEGER NOT NULL, FOREIGN KEY(tableid) REFERENCES tables(id), FOREIGN KEY(menuid) REFERENCES menu(id))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tables");
        db.execSQL("DROP TABLE IF EXISTS restaurant");
        db.execSQL("DROP TABLE IF EXISTS menu");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    public boolean insertTableTables(int number, int seats, boolean state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLES_COLUMN_NUMBER, number);
        contentValues.put(TABLES_COLUMN_SEATS, seats);
        contentValues.put(TABLES_COLUMN_STATE, state);
        db.insert(TABLES_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertTableRestaurant(String name, String description, String location, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT_COLUMN_NAME, name);
        contentValues.put(RESTAURANT_COLUMN_DESCRIPTION, description);
        contentValues.put(RESTAURANT_COLUMN_LOCATION, location);
        contentValues.put(RESTAURANT_COLUMN_PHONE, phone);
        contentValues.put(RESTAURANT_COLUMN_EMAIL, email);
        db.insert(RESTAURANT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertTableMenu(String name, String description, String categorie, String subcategorie, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MENU_COLUMN_NAME, name);
        contentValues.put(MENU_COLUMN_DESCRIPTION, description);
        contentValues.put(MENU_COLUMN_CATEGORIE, categorie);
        contentValues.put(MENU_COLUMN_SUBCATEGORIE, subcategorie);
        contentValues.put(MENU_COLUMN_PRICE, price);
        db.insert(MENU_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertTableOrders(int tableid, int menuid, int number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDERS_COLUMN_TABLEID, tableid);
        contentValues.put(ORDERS_COLUMN_MENUID, menuid);
        contentValues.put(ORDERS_COLUMN_NUMBER, number);
        db.insert(ORDERS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getDataOfTables(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tables where id="+id+"", null );
        return res;
    }

    public Cursor getDataOfRestaurant(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from restaurant where id="+id+"", null );
        return res;
    }

    public Cursor getDataOfMenu(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from menu where id="+id+"", null );
        return res;
    }

    public Cursor getDataOfOrders(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from orders where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLES_TABLE_NAME);
        return numRows;
    }

    public boolean updateTable(Integer id, int number, int seats, boolean state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLES_COLUMN_NUMBER, number);
        contentValues.put(TABLES_COLUMN_SEATS, seats);
        contentValues.put(TABLES_COLUMN_STATE, state);
        db.update(TABLES_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteTableTables (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLES_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteTableRestaurant (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(RESTAURANT_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteTableMenu (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MENU_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteTableOrders (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ORDERS_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getTables() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tables", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(TABLES_TABLE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getMenu() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from menu", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(MENU_TABLE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getOrders() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from orders", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(ORDERS_TABLE_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
