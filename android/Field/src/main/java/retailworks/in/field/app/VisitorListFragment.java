package retailworks.in.field.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import retailworks.in.field.R;
import retailworks.in.field.db.AttendanceTable;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.VisitorTable;
import retailworks.in.field.utils.Utils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnVisitorListFragmentInteractionListener}
 * interface.
 */
public class VisitorListFragment extends Fragment
implements DbSyntax{

    private OnVisitorListFragmentInteractionListener mListener;
    private View rootView;

    // TODO: Rename and change types of parameters
    public static VisitorListFragment newInstance(String param1, String param2) {
        VisitorListFragment fragment = new VisitorListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VisitorListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        ArrayAdapter sa = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, (String[]) null);
        // TODO: Change Adapter to display your content
        setListAdapter(sa);
*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.field_listview,
                container, false);

        showAttendanceDetails();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnVisitorListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnVisitorListFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAddVisitor(String id);
    }


    private void showAttendanceDetails()
    {
        // fetch all
        Cursor c = DbHelper.getInstance(getContext()).query(VisitorTable.CONTENT_URI,null,null,null,null);

        if (c != null && c.moveToFirst()) {

            String userName = Utils.getUserName(getContext());
            String type = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns.TYPE));
            String date = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns.DATE));
            String days = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns._ID));

            String[] cols = new String[] { VisitorTable.Columns.COMPANY, VisitorTable.Columns.COMPANY, VisitorTable.Columns.DATE };
            int[] vals = { R.id.GenImg, R.id.GenHeading, R.id.GenValue };
            //SimpleListAdapter sa = new SimpleListAdapter(getContext(), cols, vals);
            SimpleCursorAdapter sca = new SimpleCursorAdapter(getContext(), R.layout.row_general,c,cols,vals);
            ListView lv = (ListView)rootView.findViewById(R.id.field_lv);
            lv.setAdapter(sca);
        }
    }
}
