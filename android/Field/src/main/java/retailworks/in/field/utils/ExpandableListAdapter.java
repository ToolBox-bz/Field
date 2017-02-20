package retailworks.in.field.utils;

/**
 * Created by Rohit on 26-10-2015.
 */

import android.graphics.Bitmap;
import android.text.Html;
import android.widget.BaseExpandableListAdapter;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import retailworks.in.field.R;
import retailworks.in.field.db.ProductTable;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private HashMap<Integer, HashMap<String, String>> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, HashMap<Integer, HashMap<String, String>> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        HashMap<String, String> group = (HashMap<String, String>) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.product_details, null);
        }

        TextView namev = (TextView) convertView.findViewById(R.id.ProductDetailName);
        namev.setText(R.string.description);

        TextView descv = (TextView) convertView.findViewById(R.id.ProductDetailDesc);
        descv.setText(Html.fromHtml(group.get(ProductTable.Columns.DESCRIPTION)));

/*
        TextView typev = (TextView) convertView.findViewById(R.id.ProductDetailType);
        typev.setText(group.get(ProductTable.Columns.TYPE));

        Bitmap bitmap = Utils.fetchBitmapFromStorage(convertView.getContext(),
                group.get(ProductTable.Columns.CODE));
        ImageView picv = (ImageView) convertView.findViewById(R.id.ProductDetailImage);
        picv.setImageBitmap(bitmap);
*/

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        HashMap<String, String> group = (HashMap<String, String>) getGroup(groupPosition);
        Holder hold = new Holder();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_products, null);
        }

        hold.name = (TextView) convertView.findViewById(R.id.ProductName);
        hold.name.setText(group.get(ProductTable.Columns.NAME));

        hold.type = (TextView) convertView.findViewById(R.id.ProductType);
        hold.type.setText(group.get(ProductTable.Columns.TYPE));

        Bitmap bitmap = Utils.fetchBitmapFromStorage(convertView.getContext(),
                group.get(ProductTable.Columns.CODE));
        hold.icon = (ImageView) convertView.findViewById(R.id.ProductPic);
        hold.icon.setImageBitmap(bitmap);

        hold.indicator = (ImageView) convertView.findViewById(R.id.indicater);
        if (isExpanded) {
            hold.indicator.setImageResource(R.drawable.expander_ic_maximized);
        } else {
            hold.indicator.setImageResource(R.drawable.expander_ic_minimized);
        }

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

    class Holder{
        ImageView icon;
        TextView name;
        TextView type;
        ImageView indicator;
    }
}
