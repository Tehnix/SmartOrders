package kaist.customerapplication.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kaist.antr.kaist.R;

import java.util.HashMap;
import java.util.List;

import kaist.customerapplication.data.restaurantinfo.Menu;
import kaist.customerapplication.data.restaurantinfo.MenuEntry;
import kaist.customerapplication.data.restaurantinfo.MenuItem;

/**
 * Adapted from www.androidhive.info
 */

public class MenuExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MenuEntry> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<MenuItem>> listDataChild;

    private Menu menu;

    public MenuExpandableListAdapter(Context context, Menu menu) {
        this.context = context;
        this.menu = menu;
        this.listDataHeader = menu.menuCategories;
        this.listDataChild = constructListDataChild();
    }

    private HashMap<String,List<MenuItem>> constructListDataChild(){
        HashMap<String,List<MenuItem>> hashMap = new HashMap<>();
        for (MenuEntry entry : listDataHeader) {
            hashMap.put(entry.name, entry.menuItems);
        }
        return hashMap;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).name)
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        MenuItem childObject = (MenuItem) getChild(groupPosition, childPosition);
        String childName = childObject.name;
        String childDescription = childObject.description;
        String childPrice = Double.toString(childObject.price);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_item_layout, null);
        }

        TextView txtNameChild = (TextView) convertView.findViewById(R.id.nameText);
        TextView txtDescChild = (TextView) convertView.findViewById(R.id.descriptionText);
        TextView txtPriceChild = (TextView) convertView.findViewById(R.id.priceText);

        txtNameChild.setText(childName);
        txtDescChild.setText(childDescription);
        txtPriceChild.setText(childPrice);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition).name)
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        MenuEntry menuEntry = (MenuEntry) getGroup(groupPosition);
        String headerTitle = menuEntry.name;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.menu_group_layout, null);
        }

        TextView nameView = (TextView) convertView
                .findViewById(R.id.categoryName);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
