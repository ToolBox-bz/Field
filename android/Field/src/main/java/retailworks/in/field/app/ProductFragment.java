package retailworks.in.field.app;

/**
 * Created by Neiv on 10/19/2015.
 */
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import java.util.HashMap;

import retailworks.in.field.R;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.ProductTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.ExpandableListAdapter;
import retailworks.in.field.utils.Utils;

public class ProductFragment
        extends Fragment
        implements Constants, ViewBinder, OnClickListener
{
    private static final String LOGC = APP_TAG+ ProductFragment.class.getSimpleName();
    private static SimpleCursorAdapter adapter = null;
    private static ListView libraryLV = null;
    public static String[] products = null;

    public static String[] getProducts() {
        return products;
    }

    public void onClick(View view)
    {
        String where = ProductTable.Columns.CODE + DbSyntax.EQUAL + 
                DbSyntax.Q + view.getTag() + DbSyntax.Q;
        String[] cols = new String[] { ProductTable.Columns.DESCRIPTION, ProductTable.Columns.NAME,
                ProductTable.Columns.TYPE, ProductTable.Columns.PIC_PATH, ProductTable.Columns.CODE };
        Cursor pCursor = DbHelper.getInstance(getActivity()).query(ProductTable.CONTENT_URI, cols, where, null, "_id DESC");
        if ((pCursor == null) || (!pCursor.moveToFirst())) {
            return;
        }
        String name = pCursor.getString(pCursor.getColumnIndexOrThrow(ProductTable.Columns.NAME));
        String type = pCursor.getString(pCursor.getColumnIndexOrThrow(ProductTable.Columns.TYPE));
        String desc = pCursor.getBlob(pCursor.getColumnIndexOrThrow(ProductTable.Columns.DESCRIPTION)).toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        HashMap<Integer, HashMap<String, String>> items = getProductsMap();

        ExpandableListAdapter adapter = new ExpandableListAdapter(getContext(), items);


        final ExpandableListView expLv = (ExpandableListView)rootView.findViewById(R.id.exp_lv);
        expLv.setAdapter(adapter);

        expLv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousItem )
                    expLv.collapseGroup(previousItem );
                previousItem = groupPosition;
            }
        });

        return rootView;
    }

    private HashMap<Integer, HashMap<String, String>> getProductsMap(){

        Cursor cursor = DbHelper.getInstance(getActivity()).query(
                ProductTable.CONTENT_URI, null, null, null, "_id DESC");

        if ((cursor == null) || (!cursor.moveToFirst())) {
            return null;
        }

        int count = cursor.getCount();
        products = new String[count];
        HashMap<Integer, HashMap<String, String>> items = new HashMap<>();

        int cnt = 0;
        do{

            products[cnt] = cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));

            HashMap<String, String> item = new HashMap<>();

            item.put(cursor.getColumnName(cursor.getColumnIndexOrThrow(ProductTable.Columns.NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.Columns.NAME)));

            item.put(cursor.getColumnName(cursor.getColumnIndexOrThrow(ProductTable.Columns.TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.Columns.TYPE)));

            item.put(cursor.getColumnName(cursor.getColumnIndexOrThrow(ProductTable.Columns.CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.Columns.CODE)));

            item.put(cursor.getColumnName(cursor.getColumnIndexOrThrow(ProductTable.Columns.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.Columns.DESCRIPTION)));

            items.put(cnt++, item);
        } while(cursor.moveToNext());

        // this is just an old code
        if(!cursor.moveToFirst()){

            String[] cols = new String[] { ProductTable.Columns._ID, ProductTable.Columns.NAME,
                    ProductTable.Columns.TYPE, ProductTable.Columns.CODE };
            int[] to = new int[] { R.id.ProductName, R.id.ProductPic, R.id.ProductType, R.id.ProductName };
            adapter = new SimpleCursorAdapter(getActivity(), R.layout.row_products, cursor, cols, to, 0);

            adapter.setViewBinder(this);

            //libraryLV = (ListView)rootView.findViewById(R.id.library_lv);
            libraryLV.setAdapter(adapter);
        }

        return items;
    }

    public boolean setViewValue(View view, Cursor c, int paramInt) {

        if (view.getId() == R.id.ProductName) {
            ((TextView)view).setText(c.getString(c.getColumnIndexOrThrow(ProductTable.Columns.NAME)));
        }else if (view.getId() == R.id.ProductType){
            ((TextView)view).setText(c.getString(c.getColumnIndexOrThrow(ProductTable.Columns.TYPE)));
        } else if (view.getId() == R.id.ProductPic) {
            String code = c.getString(c.getColumnIndexOrThrow(ProductTable.Columns.CODE));

            RelativeLayout rowView = (RelativeLayout)view.getParent().getParent();
            rowView.setOnClickListener(this);
            rowView.setTag(c);

            Bitmap bitmap = Utils.fetchBitmapFromStorage(getActivity(), code);
            ((ImageView)view).setImageBitmap(bitmap);
        }
        return true;
    }
}
