package kaist.restaurantownerapp.listviewhandler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kaist.restaurantownerapp.data.handler.DBConnector;
import kaist.restaurantownerapp.views.*;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.R;

public class OrderAdapter extends BaseAdapter{
    private DBConnector db;
    private List<OrderItemDB> orders;
    private Context context;

    private static LayoutInflater inflater=null;

    public OrderAdapter(MainActivity mainActivity) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = MainActivity.getDatabase();
        orders = db.getOrderItems();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tableNumber;
        TextView menuItemName;
        TextView quantity;
    }

    public void refreshOrders() {
        orders = db.getOrderItems();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.order_list, null);
        holder.tableNumber = (TextView) rowView.findViewById(R.id.orderTableNumber);
        holder.menuItemName = (TextView) rowView.findViewById(R.id.orderMenuItemName);
        holder.quantity = (TextView) rowView.findViewById(R.id.orderQuantity);

        holder.tableNumber.setText("Table: " + (orders.get(position).getTableNumber() + 1));
        holder.menuItemName.setText(orders.get(position).getMenuItem().getName());
        holder.quantity.setText("Quantity: " + orders.get(position).getQuantity());

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                db.deleteOrderItem(orders.get(position));
                                refreshOrders();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mMainActivity);
                builder.setMessage("Order served?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

        });
        return rowView;
    }
}