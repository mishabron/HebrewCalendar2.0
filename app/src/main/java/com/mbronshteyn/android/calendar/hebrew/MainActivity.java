package com.mbronshteyn.android.calendar.hebrew;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mbronshteyn.android.calendar.hebrew.fragment.DateConversionFragment;
import com.mbronshteyn.android.calendar.hebrew.fragment.HebrewCalendarFragment;
import com.mbronshteyn.android.calendar.hebrew.fragment.HebrewEventsFragment;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.android.calendar.hebrew.util.RequestPermissionHandler;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

import java.lang.reflect.Method;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private String[] navMenuTitles;
	private MainActivity context;
	private HebrewEventsFragment hebrewEventsFragment;
	private HebrewCalendarFragment hebrewCalendarFragment;
	private DateConversionFragment dateConversionFragment;

	private RequestPermissionHandler mRequestPermissionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		mRequestPermissionHandler = new RequestPermissionHandler();
		mRequestPermissionHandler.requestPermission(this, new String[] { Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
				Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 123,
				new RequestPermissionHandler.RequestPermissionListener() {
					@Override
					public void onSuccess() {

						// init Hebrew month names
						HebrewMonth[] months = CalendarUtils.hebrewMonths;
						HebrewCalendarUtils.initMonthNames(context, months);

						// init Hebrew event names
						HebrewEvent[] events = CalendarUtils.hebrewEvents;
						HebrewCalendarUtils.initEventNames(context, events);

						mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
						getTitle();

						// Set up the drawer.
						mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

						// load slide menu items
						navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

						hebrewEventsFragment = new HebrewEventsFragment();
						hebrewEventsFragment.setArguments(getIntent().getExtras());
						hebrewEventsFragment.setHasOptionsMenu(true);

						hebrewCalendarFragment = new HebrewCalendarFragment();
						hebrewCalendarFragment.setArguments(getIntent().getExtras());
						hebrewCalendarFragment.setHasOptionsMenu(true);

						dateConversionFragment = new DateConversionFragment();
						dateConversionFragment.setArguments(getIntent().getExtras());
						dateConversionFragment.setHasOptionsMenu(true);

						// set first fragment
						getFragmentManager().beginTransaction().add(R.id.container, hebrewEventsFragment).commit();
						getActionBar().setTitle(navMenuTitles[0]);
					}

					@Override
					public void onFailed() {
						Toast.makeText(MainActivity.this, "request permission failed", Toast.LENGTH_SHORT).show();
						System.exit(1);
					}
				});
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		getActionBar().setTitle(navMenuTitles[position]);

		Fragment fragment = null;

		switch (position) {
		case 0:
			fragment = hebrewEventsFragment;
			break;
		case 1:
			fragment = hebrewCalendarFragment;
			break;
		case 2:
			fragment = dateConversionFragment;
			break;
		}

		// Commit the transaction
		if (fragment != null && fragment.isAdded()) {
			transaction.show(fragment);
		} else {
			transaction.addToBackStack(null);
			transaction.replace(R.id.container, fragment);
		}
		transaction.commit();
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// enable visible icons in action bar
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
