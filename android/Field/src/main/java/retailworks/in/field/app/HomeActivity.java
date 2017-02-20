package retailworks.in.field.app;

/**
 * Created by Neiv on 10/17/2015.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import retailworks.in.field.R;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.SuperActivity;
import retailworks.in.field.utils.Utils;
import java.util.Locale;

public class HomeActivity
        extends SuperActivity implements
        AttendanceFragment.OnAttendenceMarkedListener,
        AddVisitorFragment.OnFragmentInteractionListener{
    public static boolean attendance = false;
    private static final String LOGC = Constants.APP_TAG
            + HomeActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);

        attendance = AttendanceFragment.isAttendanceMarked(getBaseContext());
        setActionBarAndViewPager();

        startEndButton = (FloatingActionButton) findViewById(R.id.fab);
        startEndButton.setImageResource(android.R.drawable.ic_input_add);
        startEndButton.setTag(android.R.drawable.ic_input_add);
        startEndButton.hide();

        startEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer id = (Integer) startEndButton.getTag();

                if( id.longValue() == android.R.drawable.ic_input_add) {
                    startEndButton.setImageResource(android.R.drawable.ic_menu_save);
                    startEndButton.setTag(android.R.drawable.ic_menu_save);
                } else {

                    startEndButton.setImageResource(android.R.drawable.ic_input_add);
                    startEndButton.setTag(android.R.drawable.ic_input_add);
                }
            }
        });
    }

    private void setActionBarAndViewPager()
    {
        Toolbar toolbar = setActionToolBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.killActivityDialog(HomeActivity.this, R.string.logout_msg, getRootView());
            }
        });

        // Create the adapter that will return a fragment for each of the
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);


        mViewPager.setAdapter(this.mSectionsPagerAdapter);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        setTabs();
    }

    private void setTabs(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.HomeTabLayout);
        tabLayout.removeAllTabs();
        if(attendance) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.attendance));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.call_cycle));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.products));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.visitor));
        } else
            tabLayout.addTab(tabLayout.newTab().setText(R.string.day_plan));

        tabLayout.setOnTabSelectedListener(this);

        if(mViewPager != null)
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    @Override
    public void onAttendanceMarked(boolean marked)
    {
        attendance = marked;
        this.mSectionsPagerAdapter.notifyDataSetChanged();
        setTabs();
    }

    public void onBackPressed() {

        Utils.killActivityDialog(this, R.string.logout_msg, getRootView());
    }

    public void onClick(View paramView) {

    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        boolean bool = true;
        switch (paramMenuItem.getItemId())
        {
            default:
                bool = super.onOptionsItemSelected(paramMenuItem);
                break;
            case R.id.action_exit:
                Utils.killActivityDialog(this, R.string.logout_msg, getRootView());
                return bool;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int idx = tab.getPosition();
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(idx);

        if(idx == 3) startEndButton.show();
        else startEndButton.hide();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SectionsPagerAdapter
            extends FragmentStatePagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getCount() {
            return attendance? 3:1;
        }

        public Fragment getItem(int paramInt) {
            switch (paramInt)
            {
                default:
                    return null;
                case 0:
                    return new AttendanceFragment();
                case 1:
                    return new StoreListFragment();
                case 2:
                    return new ProductFragment();
                case 3:
                    return new VisitorListFragment();
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
                    return getString(R.string.attendance).toUpperCase(l);
                case 1:
                    return getString(R.string.call_cycle).toUpperCase(l);
                case 2:
                    return getString(R.string.products).toUpperCase(l);
                case 3:
                    return getString(R.string.visitor).toUpperCase(l);
            }
        }
    }
}
