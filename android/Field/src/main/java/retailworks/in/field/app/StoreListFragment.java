package retailworks.in.field.app;

/**
 * Created by Neiv on 10/18/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import retailworks.in.field.R;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.OutletTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StoreListFragment
        extends Fragment
        implements DbSyntax, Constants, View.OnClickListener, SimpleCursorAdapter.ViewBinder {
    private static final String LOGC = Constants.APP_TAG + StoreListFragment.class.getSimpleName();
    private ListView storeLv = null;
    private Calendar cDate = null;
    private String cStrDate = null;

    private View rootView = null;
    private TextView dateView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // init views
        rootView = inflater.inflate(R.layout.fragment_store_list, container, false);
        storeLv = (ListView)this.rootView.findViewById(R.id.store_lv);

        // init date
        cDate = Calendar.getInstance();
        cStrDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate.getTime());

        TextView txt = (TextView)this.rootView.findViewById(R.id.cycledate);
        txt.setText(cStrDate);

        rootView.findViewById(R.id.prevDay).setOnClickListener(this);
        rootView.findViewById(R.id.nxtDay).setOnClickListener(this);

        dateView = (TextView) rootView.findViewById(R.id.cycledate);

        return this.rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        displayCallCycle();
    }

    private void displayCallCycle()
    {
        if (cDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

            // return if its a holiday
            this.rootView.findViewById(R.id.store_lv).setVisibility(View.GONE);
            this.rootView.findViewById(R.id.HolidayText).setVisibility(View.VISIBLE);
            return;

        } else {
            this.rootView.findViewById(R.id.store_lv).setVisibility(View.VISIBLE);
            this.rootView.findViewById(R.id.HolidayText).setVisibility(View.GONE);
        }

        // query visit table for "date"
        String sql = VisitTable.Columns.DATE + EQUAL + Q + cStrDate + Q;
        String[] cols = new String[] {
                VisitTable.Columns._ID, VisitTable.Columns.CODE, VisitTable.Columns.NAME, VisitTable.Columns.ADDRESS,
                VisitTable.Columns.STATUS, VisitTable.Columns.START_TIME, VisitTable.Columns.END_TIME };
        int[] to = {R.id.shopStatus, R.id.shopName, R.id.shopAddress, R.id.timeIn, R.id.timeOut,
                R.id.in_icon, R.id.out_icon};

        Cursor vCursor = DbHelper.getInstance(getActivity()).query(
                VisitTable.CONTENT_URI, cols, sql, null, VisitTable.Columns.CODE + ASC);

        /* ct, status, start, end, name, address, ocode, vcode */
        SimpleCursorAdapter ca = new SimpleCursorAdapter(getContext(),R.layout.row_storelist, vCursor, cols, to);
        ca.setViewBinder(this);
        storeLv.setAdapter(ca);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK) && requestCode == 0x1234) {

            displayCallCycle();

            String msg = data.getStringExtra(OutletTable.Columns.NAME) + getString(R.string.store_updated) +
                    data.getStringExtra(VisitTable.Columns.STATUS);

            Utils.showSnackBar(null, msg, rootView);
        }
    }


    public void onClick(View view)
    {
        if (view.getId() == R.id.store_row) {

            Intent in = new Intent(view.getContext(), StoreVisitActivity.class);
            in.putExtra(VisitTable.Columns.STATUS, (String) view.getTag(R.id.shopStatus));
            in.putExtra(VisitTable.Columns.CODE, (String) view.getTag(R.id.shopName));
            in.putExtra(VisitTable.Columns.DATE, cStrDate);

            startActivityForResult(in, 0x1234);

        } else if ((view.getId() == R.id.prevDay) || (view.getId() == R.id.nxtDay)) {

            if (view.getId() == R.id.prevDay) {
                cDate.add(Calendar.DAY_OF_MONTH, -1);
            } else if (view.getId() == R.id.nxtDay) {
                cDate.add(Calendar.DAY_OF_MONTH, +1);
            }
            cStrDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate.getTime());

            //    Snackbar.make(rootView, R.string.cycle_day_limit_reached, Snackbar.LENGTH_LONG).show();
            dateView.setText(cStrDate);

            // display new call cycle
            displayCallCycle();
        }
    }


    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

        if(view.getId() == R.id.shopStatus){
            String status = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.STATUS));
            ImageView statusv = (ImageView) view;

            if (status.equals(VisitTable.VisitStatus.UNVISITED)) {
                statusv.setImageResource(R.drawable.ic_menu_help_holo_light);
            } else if (status.equals(VisitTable.VisitStatus.OPEN)) {
                statusv.setImageResource(R.drawable.btn_check_on_selected);
            } else if (status.equals(VisitTable.VisitStatus.REJECTED)) {
                statusv.setImageResource(R.drawable.ic_menu_blocked_user);
            } else if (status.equals(VisitTable.VisitStatus.CLOSED)) {
                statusv.setImageResource(R.drawable.ic_lock_idle_lock);
            } else if (status.equals(VisitTable.VisitStatus.NOT_FOUND)) {
                statusv.setImageResource(R.drawable.ic_dialog_map);
            }

            // set on click listener
            LinearLayout row = (LinearLayout) view.getParent();
            row.setOnClickListener(this);

            String code = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.CODE));
            row.setTag(R.id.shopStatus, status);
            row.setTag(R.id.shopName, code);

        } else if(view.getId() == R.id.timeIn) {

            LinearLayout timeLayout = (LinearLayout)view.getParent().getParent();
            String inTime = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.START_TIME));
            TextView timeInView = (TextView) view;

            if(!Utils.isEmpty(inTime)) {
                timeInView.setText(inTime);
                timeLayout.setVisibility(View.VISIBLE);
            }
        } else if(view.getId() == R.id.timeOut) {

            LinearLayout timeLayout = (LinearLayout)view.getParent().getParent();
            String outTime = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.END_TIME));
            TextView timeOutView = (TextView) view;

            if(!Utils.isEmpty(outTime)) {
                timeOutView.setText(outTime);
                timeLayout.setVisibility(View.VISIBLE);
            }

        } else if(view.getId() == R.id.shopName) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.NAME));
            TextView nameView = (TextView) view;

            nameView.setText(name);
        } else if(view.getId() == R.id.shopAddress) {
            String address = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.ADDRESS));
            TextView addrView = (TextView) view;

            addrView.setText(address);
        }

        return true;
    }



}
