/*
Copyright (c) 2011-2013, Intel Corporation

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of Intel Corporation nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package retailworks.in.field.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import retailworks.in.field.R;
import retailworks.in.field.db.DbSyntax;


/***
 * Because all of our Activities need to bind to the service, this abstract class z
 * encapsulates the grunt work of binding to the RemoteService. Subclasses
 * must implement the onServiceConnected / onServiceDisconnected methods.
 * <p>
 * This class automatically handles the bind and unbind on pause/resume. The subclass is
 * responsible for calling doStartService() and doStopService(). doStartService() should be 
 * called in the 'entry point' activities for the application. doStopService() should be
 * called when the application is done and the service may exit.
 * <p>
 * There is nothing c3 specific here.
 */
public abstract class SuperActivity extends AppCompatActivity implements
		Constants, DbSyntax, TabLayout.OnTabSelectedListener
{
	private static final String LOGC = Constants.APP_TAG + "abstract";

	private BroadcastReceiver broadcastReceiver = null;
	private CoordinatorLayout rootView = null;
	protected FloatingActionButton startEndButton = null;


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link FragmentStatePagerAdapter}.
	 */
	protected SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	protected ViewPager mViewPager;

	protected void registerKillBroadcast(){

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("CLOSE_ALL");
		this.broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		registerReceiver(this.broadcastReceiver, intentFilter);
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {

		boolean bool = false;
		switch (paramMenuItem.getItemId()) {

			default:
				bool = super.onOptionsItemSelected(paramMenuItem);
				break;
			case R.id.action_exit:
				Utils.killActivityDialog(this, R.string.logout_msg, getRootView());
				bool = true;
				break;
		}
		return bool;
	}

	public CoordinatorLayout getRootView() {

		if(rootView == null)
			rootView = (CoordinatorLayout) findViewById(R.id.main_content);

		return rootView;
	}

	protected Toolbar setActionToolBar(){

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();

		String name = getString(R.string.logged_in_as) + DbSyntax.SPACE + Utils.getUserName(this);
		toolbar.setTitle(R.string.app_name);
		toolbar.setTitleTextAppearance(this, R.style.AppTitleText);
		toolbar.setSubtitle(name);
		toolbar.setSubtitleTextAppearance(this, R.style.SubTitleText);
		actionBar.setDisplayHomeAsUpEnabled(true);

		registerKillBroadcast();

		return toolbar;
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(this.broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {

		int position = tab.getPosition();
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(position);
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {}

	public abstract class SectionsPagerAdapter
			extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public abstract int getCount();

		public abstract Fragment getItem(int paramInt);

		public abstract CharSequence getPageTitle(int paramInt);
	}
}
