package retailworks.in.field.app;

/**
 * Created by Neiv on 10/19/2015.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import retailworks.in.field.R;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.ProductTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;

public class StoreVisitFragment
        extends Fragment
        implements SimpleCursorAdapter.ViewBinder
{
    private static final String LOGC = Constants.APP_TAG + StoreVisitFragment.class.getSimpleName();
    private static SimpleCursorAdapter adapter = null;
    private static ListView visitList;
    private static ContentValues storeValues = null;
    private boolean firstVisit = false;
    private ContentValues inventoryValues = new ContentValues();
    private OnFragmentInteractionListener mListener;
    private ContentValues orderValues = new ContentValues();
    private boolean storeOpen = false;


    public static StoreVisitFragment newInstance(ContentValues values)
    {
        setStoreValues(values);
        return new StoreVisitFragment();
    }

    public static void setStoreValues(ContentValues values){
        storeValues = values;
    }

    public void onAttach(Activity paramActivity)
    {
        super.onAttach(paramActivity);
        try
        {
            this.mListener = ((OnFragmentInteractionListener)paramActivity);
            return;
        }
        catch (ClassCastException localClassCastException)
        {
            throw new ClassCastException(paramActivity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_store_visit, container, false);

        firstVisit = true;
        String[] from = new String[]{ProductTable.Columns._ID, ProductTable.Columns.NAME,
                ProductTable.Columns.TYPE, ProductTable.Columns.CODE};
        int[] to = new int[] { R.id.ProductPic, R.id.ProductName, R.id.OrdersEntry, R.id.InventoryEntry};
        Cursor c= DbHelper.getInstance(getActivity()).query(ProductTable.CONTENT_URI, from, null, null, null);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.row_visit, c, from, to, 0);
        visitList = (ListView)rootView.findViewById(R.id.visit_lv);
        if (adapter != null) {

            adapter.setViewBinder(this);
            visitList.setAdapter(adapter);
        }

        String str = storeValues.getAsString(VisitTable.Columns.STATUS);
        if ((str != null) && (str.equals(VisitTable.VisitStatus.OPEN))) {
            this.storeOpen = true;
        } else this.storeOpen = false;

        return rootView;
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    public boolean setViewValue(final View paramView, Cursor paramCursor, int paramInt)
    {
        if (paramView.getId() == R.id.ProductName){
            LinearLayout localObject = (LinearLayout)paramView.getParent().getParent();
            String name =  paramCursor.getString(paramCursor.getColumnIndexOrThrow(ProductTable.Columns.NAME)) +
                    " - " + paramCursor.getString(paramCursor.getColumnIndexOrThrow(ProductTable.Columns.TYPE));
            ((TextView)paramView).setText(name);
        } else if (paramView.getId() == R.id.OrdersEntry){

            String code = paramCursor.getString(paramCursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));
            final String order = this.orderValues.getAsString(code);
            if (order != null) {
                ((EditText)paramView).setText(order);
            }
            paramView.setTag(code);
            paramView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event){
                    String orders = ((EditText)v).getText().toString();
                    orderValues.put(v.getTag().toString(), orders);

                    if (mListener != null) {
                        mListener.onNewOrderInventory(orderValues, inventoryValues);
                    }
                    return false;
                }
            });
        } else if (paramView.getId() == R.id.InventoryEntry){

            String code = paramCursor.getString(paramCursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));
            final String order = this.inventoryValues.getAsString(code);
            if (order != null) {
                ((EditText)paramView).setText(order);
            }
            paramView.setTag(code);
            paramView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event){
                    String orders = ((EditText)v).getText().toString();
                    inventoryValues.put(v.getTag().toString(), orders);

                    if (mListener != null) {
                        mListener.onNewOrderInventory(orderValues, inventoryValues);
                    }
                    return false;
                }
            });
        } else if (paramView.getId() == R.id.LastOrderText){
            if (this.firstVisit) {
                paramView.setVisibility(View.GONE);
            }
        }else if (paramView.getId() == R.id.LastInventoryText){
            if (this.firstVisit) {
                paramView.setVisibility(View.GONE);
            }
        }else if (paramView.getId() == R.id.ProductPic){
            String url = paramCursor.getString(paramCursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));
            ((ImageView)paramView).setImageBitmap(Utils.fetchBitmapFromStorage(getActivity(), url));
        }
        return true;
    }

    public static abstract interface OnFragmentInteractionListener{
        public abstract void onNewOrderInventory(ContentValues orders, ContentValues inventory);
    }
}