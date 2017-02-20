package retailworks.in.field.app;

/**
 * Created by Neiv on 10/19/2015.
 */

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import retailworks.in.field.R;
import retailworks.in.field.db.OutletTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.SimpleListAdapter;

public class StoreInfoFragment
        extends Fragment
{
    private static SimpleListAdapter adapter = null;
    private static ContentValues storeValues = null;
    private static ListView visitorLV = null;
    private OnFragmentInteractionListener mListener;

    public StoreInfoFragment getInstance(ContentValues cv)
    {
        storeValues = cv;
        return new StoreInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_store_info, container, false);

        String type = storeValues.getAsString(OutletTable.Columns.CLASSIFICATION) +
                "-" + storeValues.getAsString(OutletTable.Columns.GRADE) +
                "-" + storeValues.getAsString(OutletTable.Columns.TYPE);

        String contact = storeValues.getAsString(OutletTable.Columns.CONTACT) + " " +
                storeValues.getAsString(OutletTable.Columns.PHONE);
        String status = storeValues.getAsString(OutletTable.Columns.STATUS);
        String address = storeValues.getAsString(OutletTable.Columns.ADDRESS);
        String beat = storeValues.getAsString(OutletTable.Columns.BEAT);
        String visit = storeValues.getAsString(VisitTable.Columns.STATUS);

        String[] headings = new String[] {
                "Outlet Description", "Contact Person", "Outlet Status",
                "Visit Status", "Beat", "Outlet Address" };
        String[] values = new String[] {
                type, contact, status, visit, beat, address };

        adapter = new SimpleListAdapter(getActivity(), headings, values);

        visitorLV = (ListView)rootView.findViewById(R.id.info_lv);
        visitorLV.setAdapter(adapter);

       return rootView;
    }

    public void onDetach()
    {
        super.onDetach();
        this.mListener = null;
    }

    public static abstract interface OnFragmentInteractionListener
    {
        public abstract void onFragmentInteraction(Uri paramUri);
    }
}
