package kaist.restaurantownerapp.listviewhandler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kaist.restaurantownerapp.data.handler.DBConnector;
import kaist.restaurantownerapp.views.*;
import kaist.restaurantownerapp.data.*;
import kaist.restaurantownerapp.R;

public class OrderAdapter extends BaseAdapter{
    private DBConnector db;
    private List<Order> orders;
    private Context context;
    private Intent detailTable;

    private static LayoutInflater inflater=null;

    public OrderAdapter(MainActivity mainActivity) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = MainActivity.getDatabase();
        //tables = db.getOrders();
        detailTable = new Intent(this.context, DetailTableActivity.class);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return tables.size();
        return 0;
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
        TextView tv;
    }

    public void refreshTables() {
        //tables = db.getAllTables();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.table_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.tableListNumber);
        /*holder.tv.setText("Table: " + tables.get(position).getTableNumber());
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                detailTable.putExtra("id", tables.get(position).getTableNumber());
                context.startActivity(detailTable);
            }
        });*/
        return rowView;
    }
}