package retailworks.in.field.app;

/**
 * Created by Neiv on 10/18/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retailworks.in.field.R;
import retailworks.in.field.db.CallCycleTable;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.OutletTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;

public class StoreListFragment1
        extends Fragment
        implements DbSyntax, Constants, View.OnClickListener
{
    private static final String LOGC = Constants.APP_TAG + StoreListFragment1.class.getSimpleName();
    private static ListView storeLv = null;
    private int cycle = 0;
    private int cycleDays = 1;
    private View rootView = null;

    private void dispCallCycle(int day)
    {
        this.rootView.findViewById(R.id.store_lv).setVisibility(View.VISIBLE);
        this.rootView.findViewById(R.id.HolidayText).setVisibility(View.GONE);

        //day += cycle * cycleDays;

        // first, fetch all beats for the given cycle day
        String where = CallCycleTable.Columns.DAY + EQUAL + Q + day + Q;
        String[] cols = new String[] {CallCycleTable.Columns.BEAT};
        Cursor cursor = DbHelper.getInstance(getActivity()).query(CallCycleTable.CONTENT_URI, cols, where, null, null);

        if (cursor != null && cursor.moveToFirst()){

            // now create sql query to fetch all outlets with the beats == cycle day
            String sql = "";
            do {
                String beat = cursor.getString(cursor.getColumnIndexOrThrow(CallCycleTable.Columns.BEAT));
                sql += CallCycleTable.Columns.BEAT + EQUAL + Q + beat + Q;

                if(cursor.moveToNext())sql += OR;
                else break;

            } while (true);

            cursor.close();
            cursor = DbHelper.getInstance(getActivity()).query(OutletTable.CONTENT_URI,
                    null, sql, null, OutletTable.Columns.CODE + ASC);
        } else return;

        int rows = 0;
        if(cursor != null) {
            rows = cursor.getCount();
        }

        String[] code = new String[rows];
        String[] name = new String[rows];
        String[] address = new String[rows];
        Cursor vCursor = null;
        if (cursor != null && cursor.moveToFirst()){

            String sql = VisitTable.Columns.CYCLE + EQUAL + Q + this.cycle + Q + AND + LP;
            rows = 0;
            do {
                code[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.CODE));
                name[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.NAME));
                address[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.ADDRESS));

                sql += VisitTable.Columns.CODE + EQUAL + Q + code[rows] + Q;
                if (cursor.moveToNext()){
                    sql += OR;
                    rows++;
                } else {
                    sql += RP;
                    break;
                }
            } while (true);

            cols = new String[] { VisitTable.Columns.CODE, VisitTable.Columns.STATUS, VisitTable.Columns.START_TIME, VisitTable.Columns.END_TIME };
            vCursor = DbHelper.getInstance(getActivity()).query(
                    VisitTable.CONTENT_URI, cols, sql, null, VisitTable.Columns.CODE +  ASC);
        }

        if (vCursor == null) {
            return;
        }
        rows = vCursor.getCount();
        String[] start = new String[rows];
        String[] end = new String[rows];
        String[] status = new String[rows];
        if ((vCursor != null) && (vCursor.moveToFirst()))
        {
            rows = 0;
            do {
                status[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.STATUS));
                start[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.START_TIME));
                end[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.END_TIME));
                rows++;
            } while (vCursor.moveToNext());
        }
        /* ct, status, start, end, name, address, ocode, vcode */
        CustomAdapter sa = new CustomAdapter(getActivity(), status, start, end, name, address, code, code);
        storeLv.setAdapter(sa);
    }


    private void displayCallCycle(int day)
    {
        this.rootView.findViewById(R.id.store_lv).setVisibility(View.VISIBLE);
        this.rootView.findViewById(R.id.HolidayText).setVisibility(View.GONE);

        //day += cycle * cycleDays;

        // first, fetch all beats for the given cycle day
        String where = CallCycleTable.Columns.DAY + EQUAL + Q + day + Q;
        String[] cols = new String[] {CallCycleTable.Columns.BEAT};
        Cursor cursor = DbHelper.getInstance(getActivity()).query(CallCycleTable.CONTENT_URI, cols, where, null, null);
        
        if (cursor != null && cursor.moveToFirst()){

            // now create sql query to fetch all outlets with the beats == cycle day
            String sql = "";
            do {
                String beat = cursor.getString(cursor.getColumnIndexOrThrow(CallCycleTable.Columns.BEAT));
                sql += CallCycleTable.Columns.BEAT + EQUAL + Q + beat + Q;

                if(cursor.moveToNext())sql += OR;
                else break;

            } while (true);

            cursor.close();
            cursor = DbHelper.getInstance(getActivity()).query(OutletTable.CONTENT_URI,
                    null, sql, null, OutletTable.Columns.CODE + ASC);
        } else return;

        int rows = 0;
        if(cursor != null) {
            rows = cursor.getCount();
        }

        String[] code = new String[rows];
        String[] name = new String[rows];
        String[] address = new String[rows];
        Cursor vCursor = null;
        if (cursor != null && cursor.moveToFirst()){

            String sql = VisitTable.Columns.CYCLE + EQUAL + Q + this.cycle + Q + AND + LP;
            rows = 0;
            do {
                code[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.CODE));
                name[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.NAME));
                address[rows] = cursor.getString(cursor.getColumnIndexOrThrow(OutletTable.Columns.ADDRESS));

                sql += VisitTable.Columns.CODE + EQUAL + Q + code[rows] + Q;
                if (cursor.moveToNext()){
                    sql += OR;
                    rows++;
                } else {
                    sql += RP;
                    break;
                }
            } while (true);
        
            cols = new String[] { VisitTable.Columns.CODE, VisitTable.Columns.STATUS, VisitTable.Columns.START_TIME, VisitTable.Columns.END_TIME };
            vCursor = DbHelper.getInstance(getActivity()).query(
                    VisitTable.CONTENT_URI, cols, sql, null, VisitTable.Columns.CODE +  ASC);
        }
        
        if (vCursor == null) {
            return;
        }
        rows = vCursor.getCount();
        String[] start = new String[rows];
        String[] end = new String[rows];
        String[] status = new String[rows];
        if ((vCursor != null) && (vCursor.moveToFirst()))
        {
            rows = 0;
            do {
                status[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.STATUS));
                start[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.START_TIME));
                end[rows] = vCursor.getString(vCursor.getColumnIndexOrThrow(VisitTable.Columns.END_TIME));
                rows++;
            } while (vCursor.moveToNext());
        }
        /* ct, status, start, end, name, address, ocode, vcode */
        CustomAdapter sa = new CustomAdapter(getActivity(), status, start, end, name, address, code, code);
        storeLv.setAdapter(sa);
    }

    private int getCycleDay() {

        int day = 1;
        String[] cols =new String[] { CallCycleTable.Columns.START_DATE, CallCycleTable.Columns.DAY };
        Cursor cursor = DbHelper.getInstance(getActivity()).query(
                CallCycleTable.CONTENT_URI, cols, null, null, CallCycleTable.Columns.DAY  + ASC);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToLast()) {

            cycleDays = cursor.getInt(cursor.getColumnIndexOrThrow(CallCycleTable.Columns.DAY));
            String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(CallCycleTable.Columns.START_DATE));

            try {

                Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                Calendar cstart = Calendar.getInstance();
                cstart.setTime(startdate);
                //cstart.add(Calendar.DAY_OF_MONTH, 5);
                Calendar curr = Calendar.getInstance();
                //curr.add(Calendar.DAY_OF_MONTH, -1); // for testing
                Date cDate = curr.getTime();
                if (startdate.before(cDate)) {
                    long totalDays = (cDate.getTime() - startdate.getTime()) / 86400000L  + 1L /*including today*/;
                    totalDays -= totalDays / 6L;
                    totalDays = Utils.getWorkingDaysBetweenTwoDates(cstart.getTime(), curr.getTime());

                    if (totalDays > this.cycleDays) {
                        this.cycle = ((int)totalDays / cycleDays);
                        day = (int) totalDays % this.cycleDays;
                        if(day == 0){
                            cycle--;
                            day = cycleDays;
                        }
                    }
                }
            } catch (ParseException e) {
                e.getMessage();
            }
        }
        return day;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK) && requestCode == 0x1234) {
            int day = (Integer)getActivity().findViewById(R.id.cycledate).getTag();
            displayCallCycle(day);

            String msg = data.getStringExtra(OutletTable.Columns.NAME) + getString(R.string.store_updated) +
                    data.getStringExtra(VisitTable.Columns.STATUS);

            Utils.showSnackBar(null, msg, rootView);
        }
    }


    public void onClick(View view)
    {

        if (view.getId() == R.id.store_row) {
            String[] data = (String[]) view.getTag();
            Intent in = new Intent(view.getContext(), StoreVisitActivity.class);
            in.putExtra(OutletTable.Columns.CODE, data[0]);
            in.putExtra(VisitTable.Columns.STATUS, data[1]);
            in.putExtra(VisitTable.Columns.CYCLE, this.cycle);

            startActivityForResult(in, 0x1234);
        } else if ((view.getId() == R.id.prevDay) || (view.getId() == R.id.nxtDay)) {

            TextView dateView = (TextView) ((LinearLayout) view.getParent()).findViewById(R.id.cycledate);
            String dateStre = dateView.getText().toString();
            int day = (Integer) dateView.getTag();
            boolean display = true;
            int aDay = 1;

            if (view.getId() == R.id.prevDay) {
                if (day > 1) {
                    day--;
                    aDay *= -1;
                } else
                    display = false;
            } else if (view.getId() == R.id.nxtDay) {
                if (day < cycleDays) {
                    day++;
                } else {
                    display = false;
                }
            }

            if (!display) {
                Snackbar.make(rootView, R.string.cycle_day_limit_reached, Snackbar.LENGTH_LONG).show();
                return;
            }

            try {
                DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dFormat.parse(dateStre);

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_MONTH, aDay);

                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    this.rootView.findViewById(R.id.HolidayText).setVisibility(View.VISIBLE);
                    display = false;
                }

                dateView.setText(dFormat.format(c.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (display) {
                dateView.setTag(day);
                displayCallCycle(day);
                return;
            } else {
                this.rootView.findViewById(R.id.store_lv).setVisibility(View.GONE);
                this.rootView.findViewById(R.id.HolidayText).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_store_list, container, false);
        storeLv = (ListView)this.rootView.findViewById(R.id.store_lv);

        int day = getCycleDay();

        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            displayCallCycle(day);
        } else {
            rootView.findViewById(R.id.store_lv).setVisibility(View.GONE);
            rootView.findViewById(R.id.HolidayText).setVisibility(View.VISIBLE);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        TextView txt = (TextView)this.rootView.findViewById(R.id.cycledate);
        txt.setTag(day);
        txt.setText(date);

        rootView.findViewById(R.id.prevDay).setOnClickListener(this);
        rootView.findViewById(R.id.nxtDay).setOnClickListener(this);

        return this.rootView;
    }

    class CustomAdapter
            extends BaseAdapter
    {
        private Context ctx = null;
        private LayoutInflater inflater = null;
        private String[] addr,end,name,ocode,start,status,vcode;

        /**
         *
         * @param ct
         * @param status
         * @param start
         * @param end
         * @param name
         * @param address
         * @param ocode
         * @param vcode
         */
        CustomAdapter(Context ct, String[] status, String[] start, String[] end, String[] name, String[] address, String[] ocode, String[] vcode) {
            this.ctx    = ct;
            this.ocode  = ocode;
            this.vcode  = vcode;
            this.name   = name;
            this.addr   = address;
            this.status = status;
            this.start  = start;
            this.end    = end;

            this.inflater = ((LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        }

        public int getCount() {
            return this.ocode.length;
        }

        public Object getItem(int paramInt) {
            return Integer.valueOf(paramInt);
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int idx, View rowView, ViewGroup paramViewGroup) {

            if(rowView == null) {
                rowView = this.inflater.inflate(R.layout.row_storelist, null);
            }

            Holder hold = new Holder();

            hold.statusv = (ImageView)rowView.findViewById(R.id.shopStatus);
            hold.namev = (TextView)rowView.findViewById(R.id.shopName);
            hold.addrv = (TextView)rowView.findViewById(R.id.shopAddress);
            hold.inv = (TextView)rowView.findViewById(R.id.timeIn);
            hold.outv = (TextView)rowView.findViewById(R.id.timeOut);
            hold.inicon = (ImageView)rowView.findViewById(R.id.in_icon);
            hold.outicon = (ImageView)rowView.findViewById(R.id.out_icon);

            if(status.length == 0) return rootView;

            if (status[idx].equals(VisitTable.VisitStatus.UNVISITED)) {
                hold.statusv.setImageResource(R.drawable.ic_menu_help_holo_light);
            } else if (status[idx].equals(VisitTable.VisitStatus.OPEN)) {
                hold.statusv.setImageResource(R.drawable.btn_check_on_selected);
            } else if (status[idx].equals(VisitTable.VisitStatus.REJECTED)) {
                hold.statusv.setImageResource(R.drawable.ic_menu_blocked_user);
            } else if (status[idx].equals(VisitTable.VisitStatus.CLOSED)) {
                hold.statusv.setImageResource(R.drawable.ic_lock_idle_lock);
            } else if (status[idx].equals(VisitTable.VisitStatus.NOT_FOUND)) {
                hold.statusv.setImageResource(R.drawable.ic_dialog_map);
            } /*else // all cases like closed, rejected, not found, etc
                hold.statusv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
*/
            boolean time = false;
            if (start[idx] != null && !start[idx].equals("")) {
                hold.inv.setText(start[idx]);
                time = true;
                hold.inicon.setVisibility(View.VISIBLE);
            } else hold.inicon.setVisibility(View.GONE);

            if (end[idx] != null && !end[idx].equals("")) {
                hold.outv.setText(end[idx]);
                time = true;
                hold.outicon.setVisibility(View.VISIBLE);
            }else hold.outicon.setVisibility(View.GONE);

            if(!time)
                ((View)hold.outv.getParent().getParent()).setVisibility(View.GONE);

            hold.namev.setText(name[idx]);
            hold.addrv.setText(addr[idx]);

            rowView.setOnClickListener(StoreListFragment1.this);

            String[] data = new String[] {ocode[idx],status[idx]};
            rowView.setTag(data);

            return rowView;
        }

        class Holder {
            ImageView statusv;
            TextView namev;
            TextView addrv;
            TextView inv;
            TextView outv;
            ImageView inicon;
            ImageView outicon;
        }
    }


}
