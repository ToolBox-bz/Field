package retailworks.in.field.app;

/**
 * Created by Neiv on 10/17/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import retailworks.in.field.R;
import retailworks.in.field.db.AttendanceTable;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.ICallBack;
import retailworks.in.field.utils.SimpleListAdapter;
import retailworks.in.field.utils.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AttendanceFragment
        extends Fragment
        implements Constants, DbSyntax, ICallBack, AbsListView.OnItemClickListener
{
    private static final String LOGC = APP_TAG + AttendanceFragment.class.getSimpleName();
    private static String attendanceType = AttendanceTable.ATTENDANCE.AWOL;
    private static OnAttendenceMarkedListener mListener;
    private static View rootView = null;
    private static Snackbar sb = null;
    private boolean attendanceMarked = false;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleListAdapter mAdapter;

    public static boolean isAttendanceMarked(Context ct)
    {
        boolean result = false;
        String sql = SELECT + AttendanceTable.Columns.DATE + FROM + AttendanceTable.TABLE_NAME +
        ORDER_BY + AttendanceTable.Columns._ID + DESC + LIMIT + 1;
        Cursor c = DbHelper.getInstance(ct).rawQuery(sql);

        if (c != null && c.moveToFirst()) {
            try {
                String date = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns.DATE));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                int d = dateFormat.parse(date).getDate();
                int cd = Calendar.getInstance().get(Calendar.DATE);

                if (cd == d) {
                    result = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void showAttendanceDetails()
    {
        String sql = SELECT + ALL + FROM + AttendanceTable.TABLE_NAME +
                ORDER_BY + AttendanceTable.Columns._ID + DESC + LIMIT + 1;
        Cursor c = DbHelper.getInstance(getContext()).rawQuery(sql);
        if (c != null && c.moveToFirst()) {

            String userName = Utils.getUserName(getContext());
            String type = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns.TYPE));
            String date = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns.DATE));
            String days = c.getString(c.getColumnIndexOrThrow(AttendanceTable.Columns._ID));

            String[] cols = new String[] { "Name", "Attendance Type", "Date", "Total Attendance" };
            String[] vals = new String[] { userName, type, date, days };
            SimpleListAdapter sa = new SimpleListAdapter(getContext(), cols, vals);

            ListView lv = (ListView)rootView.findViewById(R.id.field_lv);
            lv.setAdapter(sa);
            lv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void workDone(boolean result) {

        if(result) {
            mListener.onAttendanceMarked(result);
            sb.dismiss();

            showAttendanceDetails();
        } else {
            Utils.showSnackBar(null, R.string.attendance_sync_failed, rootView);
        }
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnAttendenceMarkedListener)activity;
            return;
        }
        catch (ClassCastException localClassCastException)
        {
            throw new ClassCastException(activity.toString() + " must implement OnAttendenceMarkedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] Heading = new String[]{
                getString(R.string.present_msg),
                getString(R.string.absent_msg),
                getString(R.string.meeting_msg),
                getString(R.string.training_msg),
                getString(R.string.travel_msg),
                getString(R.string.admin_msg)
        };

        int[] imgs = {
                R.drawable.ic_person_white_48dp,
                R.drawable.ic_hotel_white_48dp,
                R.drawable.ic_people_white_48dp,
                R.drawable.ic_school_white_48dp,
                R.drawable.ic_flight_white_48dp,
                R.drawable.ic_menu_paste_holo_light
        };

        mAdapter = new SimpleListAdapter(getContext(), Heading, imgs);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.field_listview,
                container, false);

        this.attendanceMarked = isAttendanceMarked(getActivity());
        if (this.attendanceMarked) {
            showAttendanceDetails();
        }
        else {
            // Set the adapter
            mListView = (AbsListView) rootView.findViewById(R.id.field_lv);
            mListView.setAdapter(mAdapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);
        }

        return rootView;
    }


    private void showConfirmationDialog(final Context ct) {

        AlertDialog.Builder localBuilder = new AlertDialog.Builder(ct);
        localBuilder.setMessage(ct.getString(R.string.attendance_alert1) +
                AttendanceFragment.attendanceType + ct.getString(R.string.attendance_alert2));
        localBuilder.setTitle(AttendanceFragment.attendanceType);

        localBuilder.setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int postion) {

                dialog.cancel();

                Calendar cal = Calendar.getInstance();
                String date = new SimpleDateFormat("yyyy-MM-dd HH-mm").format(cal.getTime());

                sb = Snackbar.make(rootView, R.string.syncing_with_server, Snackbar.LENGTH_INDEFINITE);
                sb.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sb.dismiss();
                    }
                });
                sb.show();

                ContentValues cv = new ContentValues();
                cv.put(AttendanceTable.Columns.EMPCODE, Utils.getEmpCode(ct));
                cv.put(AttendanceTable.Columns.DATE, date);
                cv.put(AttendanceTable.Columns.TYPE, attendanceType);

                Utils.sendRemoteSyncReq(ct,INSERT,AttendanceTable.TABLE_NAME,cv, AttendanceFragment.this, true);

            }
                      });

        localBuilder.setNegativeButton(android.R.string.cancel, null);
        localBuilder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        attendanceType = (String) view.getTag();
        Context ctx = view.getContext();

        showConfirmationDialog(ctx);
    }

    public static abstract interface OnAttendenceMarkedListener {

        public abstract void onAttendanceMarked(boolean paramBoolean);
    }
}