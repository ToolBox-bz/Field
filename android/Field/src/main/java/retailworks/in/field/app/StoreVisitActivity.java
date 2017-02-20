package retailworks.in.field.app;

/**
 * Created by Neivon 10/17/2015.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retailworks.in.field.R;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.InventoryTable;
import retailworks.in.field.db.OrderTable;
import retailworks.in.field.db.OutletTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.ICallBack;
import retailworks.in.field.utils.SuperActivity;
import retailworks.in.field.utils.Utils;

public class StoreVisitActivity extends SuperActivity implements
        StoreVisitFragment.OnFragmentInteractionListener,
        StoreCheckinFragment.OnCheckinFragmentListener,
        ICallBack, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOGC = Constants.APP_TAG
            + StoreVisitActivity.class.getSimpleName();

    public boolean visited = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Context ctx = null;
    private String vDate = null;
    private ImageView imageView = null;

    private ContentValues inventoryValues = new ContentValues();
    private ContentValues orderValues = new ContentValues();
    private ContentValues storeValues = new ContentValues();
    private ContentValues visitValues = new ContentValues();
    private String outletStatus = null;
    private Bitmap storeBitmap = null;
    private boolean started = false;

    GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;

    private void dispatchTakePictureIntent()
    {
        Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (localIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(localIntent, 1);
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);

        visited = isOutletVisited();
        setActionBarAndViewPager();

        ctx = this;
        populateOutletInfo();

        TextView pageTitle = (TextView)getRootView().findViewById(R.id.pageTitle);
        pageTitle.setVisibility(View.VISIBLE);
        pageTitle.setText(storeValues.getAsString(OutletTable.Columns.NAME));

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void setActionBarAndViewPager()
    {
        Toolbar toolbar = setActionToolBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getRootView(), R.string.abort_visit, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                //Utils.killActivityDialog(StoreVisitActivity.this,
                //      R.string.abort_visit);
            }
        });

        // Create the adapter that will return a fragment for each of the
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(this.mSectionsPagerAdapter);

        startEndButton = (FloatingActionButton) findViewById(R.id.fab);
        startEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                if (outletStatus.equals(VisitTable.VisitStatus.NOT_FOUND)) {

                    String lat = storeValues.getAsString(OutletTable.Columns.LATITUDE);
                    String lng = storeValues.getAsString(OutletTable.Columns.LONGITUDE);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + lat + " , " + lng)));

                    submitData();

                } else if (storeBitmap == null) {

                    dispatchTakePictureIntent();
                } else
                */
                    submitData();
            }
        });

        setTabLayout();

        mViewPager.setPadding(0, 40, 0, 0);
    }

    private void setTabLayout(){

        TabLayout tabLayout = (TabLayout) findViewById(R.id.HomeTabLayout);
        tabLayout.setOnTabSelectedListener(this);

        if(tabLayout.getTabCount() == 0) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.info));
            if (!visited) tabLayout.addTab(tabLayout.newTab().setText(R.string.checkin));
        }
        if(started) tabLayout.addTab(tabLayout.newTab().setText(R.string.orders));


        if(mViewPager != null)
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        if(visited)
            super.onBackPressed();
        else
        Utils.killActivityDialog(this, R.string.abort_visit,
                getRootView());
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        return super.onOptionsItemSelected(paramMenuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.visit_menu, menu);
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);

        int position = tab.getPosition();
        setFloatingButton(position);
    }

    @Override
    public void onNewOrderInventory(ContentValues orders, ContentValues inventory) {
        orderValues = orders;
        inventoryValues = inventory;
    }

    @Override
    public void onCheckin(int position) {}

    @Override
    public void workDone(boolean result) {

        if(result){

            Intent in = new Intent();

            in.putExtra(OutletTable.Columns.NAME, storeValues.getAsString(OutletTable.Columns.NAME));
            in.putExtra(VisitTable.Columns.STATUS, visitValues.getAsString(VisitTable.Columns.STATUS));

            setResult(Activity.RESULT_OK, in);

            finish();
        } else {
            setResult(Activity.RESULT_CANCELED);
            Utils.showSnackBar(null, R.string.remote_sync_failed, getRootView());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class SectionsPagerAdapter
            extends SuperActivity.SectionsPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return visited ? 1:started ? 3:2;
        }

        public Fragment getItem(int paramInt) {
            switch (paramInt)
            {
                default:
                    return null;
                case 0:
                    ContentValues cv = new ContentValues(storeValues);
                    cv.putAll(visitValues);
                    return new StoreInfoFragment().getInstance(cv);
                case 1:
                    return new StoreCheckinFragment().newInstance(storeValues);
                case 2:
                    return new StoreVisitFragment().newInstance(storeValues);

                /*case 3:
                    return new VisitorListFragment();*/
            }
        }

        public CharSequence getPageTitle(int paramInt)
        {
            Locale l = Locale.getDefault();
            switch (paramInt)
            {
                default:
                    return null;
                case 0:
                    return getString(R.string.info).toUpperCase(l);
                case 1:
                    return getString(R.string.checkin).toUpperCase(l);
                case 2:
                    return getString(R.string.orders).toUpperCase(l);
            }
        }
    }

    private boolean isOutletVisited() {

        String code = getIntent().getStringExtra(VisitTable.Columns.CODE);
        int cycle = getIntent().getIntExtra(VisitTable.Columns.CYCLE, 0);
        String vstatus = getIntent().getStringExtra(VisitTable.Columns.STATUS);

        if (!vstatus.equals(VisitTable.VisitStatus.UNVISITED)){
            Utils.showSnackBar(null, R.string.already_visited, getRootView());
            //Snackbar.make(getRootView(), R.string.already_visited,Snackbar.LENGTH_INDEFINITE).show();
            return true;
        }

        if(vstatus.equals(VisitTable.VisitStatus.UNVISITED)) return false;

        String where = VisitTable.Columns.CYCLE + EQUAL + Q + cycle + Q + AND +
                VisitTable.Columns.CODE + EQUAL + Q + code + Q;

        String[] cols = new String[] { VisitTable.Columns.STATUS };
        Cursor cursor = DbHelper.getInstance(this).query(VisitTable.CONTENT_URI, cols, where, null, null);

        if (cursor != null && cursor.moveToFirst()){
            String status = cursor.getString(cursor.getColumnIndexOrThrow(VisitTable.Columns.STATUS));
            if (!status.equals(VisitTable.VisitStatus.UNVISITED)){
                Snackbar.make(getRootView(), R.string.already_visited,Snackbar.LENGTH_INDEFINITE).show();
                return true;
            }
        }
        return false;
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        if ((paramInt1 == 1) && (paramInt2 == -1))
        {
            this.storeBitmap = ((Bitmap)paramIntent.getExtras().get("data"));
            //this.storeBitmap = Utils.rotateBitmap(storeBitmap);
            this.imageView = ((ImageView)findViewById(R.id.picthumb));
            this.imageView.setImageBitmap(this.storeBitmap);
            this.imageView.setVisibility(View.VISIBLE);

            setFloatingButton(1);

            startEndButton.animate().translationXBy(-400);
        }
    }

    void populateOutletInfo()
    {
        String vStatus = getIntent().getStringExtra(VisitTable.Columns.STATUS);
        String code = getIntent().getStringExtra(VisitTable.Columns.CODE);
        this.vDate = getIntent().getStringExtra(VisitTable.Columns.DATE);

        String[] cols = OutletTable.getColumnNames();
        String where = OutletTable.Columns.CODE + EQUAL + Q + code + Q;
        Cursor c = DbHelper.getInstance(this).query(OutletTable.CONTENT_URI, cols, where, null, null);

        if (c != null && c.moveToFirst()) {
            try {
                int idx = 0;
                for (String str : cols) {
                    this.storeValues.put(str, c.getString(idx++));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                c.close();
            }
        }

        this.visitValues.put(VisitTable.Columns.EMPCODE, Utils.getEmpCode(ctx));
        this.visitValues.put(VisitTable.Columns.TYPE, "sales");
        this.visitValues.put(VisitTable.Columns.CODE, this.storeValues.getAsString(VisitTable.Columns.CODE));
        this.visitValues.put(VisitTable.Columns.DATE, vDate);
        this.visitValues.put(VisitTable.Columns.STATUS, vStatus);
    }


    private void setFloatingButton(int tab){

        switch(tab){
            case 0:
                startEndButton.hide();
                break;
            case 2:
            case 1: {
                if(outletStatus != null) {
                    startEndButton.show();

                    int id;
                    if(outletStatus.equals(VisitTable.VisitStatus.NOT_FOUND)) {

//                        Utils.showSnackBar(null, R.string.locate_suggest, getRootView());
                        id = android.R.drawable.ic_dialog_map;

                    } else {

       //                 if(storeBitmap == null) {
         //                   Utils.showSnackBar(null,R.string.snapshot_suggest, getRootView());
           //                 id = android.R.drawable.ic_menu_camera;
             //           }else{
                            if(!started && outletStatus.equals(VisitTable.VisitStatus.OPEN)){
    //                            Utils.showSnackBar(null,R.string.visit_start_suggest, getRootView());
                                id = android.R.drawable.ic_media_play;

                            }else{
      //                          Utils.showSnackBar(null,R.string.save_suggest, getRootView());
                                id = android.R.drawable.ic_menu_save;
                            }
     //                   }
                    }

                    startEndButton.setImageDrawable(this.getResources().getDrawable(id));
                }else {

        //            Utils.showSnackBar(null, R.string.checking_suggest, getRootView());
                    //startEndButton.hide();
                }
            }
        }
    }


    private void submitData() {

        Calendar c = Calendar.getInstance();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        this.visitValues.put(VisitTable.Columns.DATE, dateStr);

        String time = new SimpleDateFormat("HH:mm").format(c.getTime());

        this.visitValues.put(VisitTable.Columns.STATUS, outletStatus);

        if(outletStatus.equals(VisitTable.VisitStatus.OPEN)) {

            // check if we are starting or ending visit!
            if (! started) {
                this.visitValues.put(VisitTable.Columns.START_TIME, time);
                startEndButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_save));
                startEndButton.animate().translationXBy(-400);

                started = true;
                mSectionsPagerAdapter.notifyDataSetChanged();
                setTabLayout();
                this.mViewPager.setCurrentItem(2);
                return;
            }
        }

        visitValues.put(VisitTable.Columns.END_TIME, time);
        storeValuesInDb();
    }


    public void storeValuesInDb() {

        final Snackbar sb = Utils.showSnackBar(null, R.string.syncing_with_server, getRootView());

        String where = VisitTable.Columns.DATE + EQUAL + Q + vDate + Q + AND +
                VisitTable.Columns.CODE + EQUAL + Q + visitValues.get(VisitTable.Columns.CODE) + Q;

        // fetch visit id
        String[] cols = new String[]{VisitTable.Columns._ID};
        Cursor c = DbHelper.getInstance(ctx).query(VisitTable.CONTENT_URI, cols, where, null, null);
        if ((c != null) && (c.moveToFirst())) {

            long visit_id = c.getLong(c.getColumnIndexOrThrow(VisitTable.Columns._ID));

            this.orderValues.put(OrderTable.Columns.VISIT_ID, visit_id);
            this.orderValues.put(OrderTable.Columns.EMPCODE, Utils.getEmpCode(ctx));
            this.orderValues.put(OrderTable.Columns.CODE, this.visitValues.getAsString(VisitTable.Columns.CODE));
            this.orderValues.put(OrderTable.Columns.DATE, this.visitValues.getAsString(VisitTable.Columns.DATE));
            long order_id = DbHelper.getInstance(ctx).insert(OrderTable.CONTENT_URI, this.orderValues);

            this.inventoryValues.put(InventoryTable.Columns.VISIT_ID, visit_id);
            this.inventoryValues.put(InventoryTable.Columns.EMPCODE, Utils.getEmpCode(ctx));
            this.inventoryValues.put(InventoryTable.Columns.CODE, this.visitValues.getAsString(VisitTable.Columns.CODE));
            this.inventoryValues.put(InventoryTable.Columns.DATE, this.visitValues.getAsString(VisitTable.Columns.DATE));
            long inventory_id = DbHelper.getInstance(ctx).insert(InventoryTable.CONTENT_URI, this.inventoryValues);

            visitValues.put(VisitTable.Columns.ORDERS_ID, order_id);
            visitValues.put(VisitTable.Columns.INVENTORY_ID, inventory_id);

            if (mLastLocation != null) {
                visitValues.put(VisitTable.Columns.LATITUDE, mLastLocation.getLatitude());
                visitValues.put(VisitTable.Columns.LONGITUDE, mLastLocation.getLongitude());
            }

            where = VisitTable.Columns._ID + EQUAL + Q + visit_id + Q;
            DbHelper.getInstance(ctx).update(VisitTable.CONTENT_URI, visitValues, where, null);

            Utils.sendRemoteSyncReq(ctx, INSERT, VisitTable.TABLE_NAME, visitValues, this, false);

            if (this.outletStatus.equals(VisitTable.VisitStatus.OPEN)) {

                Utils.sendRemoteSyncReq(ctx,INSERT, OrderTable.TABLE_NAME, orderValues, this, false);

                ICallBack cb = new ICallBack() {
                    @Override
                    public void workDone(boolean result) {
                        sb.dismiss();
                    }
                };
                Utils.sendRemoteSyncReq(ctx, INSERT, InventoryTable.TABLE_NAME, inventoryValues, this, false);
            }

            Intent in = new Intent();
            in.putExtra(OutletTable.Columns.NAME, storeValues.getAsString(OutletTable.Columns.NAME));
            in.putExtra(VisitTable.Columns.STATUS, storeValues.getAsString(VisitTable.Columns.STATUS));
            setResult(Activity.RESULT_OK, in);
        } else
            setResult(Activity.RESULT_CANCELED);

        //finish();
    }

    // called from xml
    public void onRadioButtonClicked(View radioBtn)
    {
        if (visited) return;
        startEndButton.setVisibility(View.VISIBLE);
        startEndButton.show();

        switch (radioBtn.getId()){
            case R.id.radio_open:
                this.outletStatus = VisitTable.VisitStatus.OPEN;
                break;
            case R.id.radio_closed:
                started = false;
                this.outletStatus = VisitTable.VisitStatus.CLOSED;
                break;
            case R.id.radio_notfnd:
                started = false;
                this.outletStatus = VisitTable.VisitStatus.NOT_FOUND;
                break;
            case R.id.radio_rejected:
                started = false;
                this.outletStatus = VisitTable.VisitStatus.REJECTED;
                break;
        }
        setFloatingButton(1);
    }

}