package com.yamatocreation.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yamatocreation.R;
import com.yamatocreation.SendMessageActivity;
import com.yamatocreation.adapter.ListAdapter;
import com.yamatocreation.item.ListItem;
import com.yamatocreation.view.DrawerArrowDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable mDrawerArrowDrawable;

	private DrawerLayout mDrawerLayout;
	private RelativeLayout mLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	private float offset;
	private boolean flipped;

	// ロゴのタップ回数
	private int m_nTapLogoCount = 0;

	public NavigationDrawerFragment() {
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		//selectItem(mCurrentSelectedPosition);
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mLayout = (RelativeLayout) inflater.inflate(R.layout.frag_nav_drawer, container, false);

		ImageView imgLogo = (ImageView) mLayout.findViewById(R.id.nav_logo);
		imgLogo.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				m_nTapLogoCount++;
				Log.v("NAV", String.format("m_nTapLogoCount = %d", m_nTapLogoCount));
				if(7 <= m_nTapLogoCount)
				{
					m_nTapLogoCount = 0;
					Intent i = new Intent(getActivity(), SendMessageActivity.class);
					startActivity(i);
				}
			}
		});

		mDrawerListView = (ListView)mLayout.findViewById(R.id.nav_list);
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});
		List<ListItem> strItems = new ArrayList<ListItem>();
		strItems.add(new ListItem(android.R.drawable.ic_menu_share, getString(R.string.NAV_TITLE_SHARE)));
		strItems.add(new ListItem(android.R.drawable.ic_menu_info_details, getString(R.string.NAV_TITLE_ABOUT)));
		strItems.add(new ListItem(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.NAV_TITLE_FINISH)));

		ListAdapter adapter = new ListAdapter(getActivity(), strItems);
		mDrawerListView.setAdapter(adapter);
		//mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		return mLayout;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 * @param fragmentId   The android:id of this fragment in its activity's layout.
	 * @param drawerLayout The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(),                    /* host Activity */
				mDrawerLayout,                    /* DrawerLayout object */
				R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
				R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
			@Override
			public void onDrawerClosed(View drawerView)
			{
				m_nTapLogoCount = 0;

				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				m_nTapLogoCount = 0;

				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				m_nTapLogoCount = 0;
				offset = slideOffset;

				// Sometimes slideOffset ends up so close to but not quite 1 or 0.
				if (slideOffset >= .995) {
					flipped = true;
					mDrawerArrowDrawable.setFlip(flipped);
				} else if (slideOffset <= .005) {
					flipped = false;
					mDrawerArrowDrawable.setFlip(flipped);
				}

				mDrawerArrowDrawable.setParameter(offset);
			}		};
		mDrawerToggle.setHomeAsUpIndicator(mDrawerArrowDrawable);

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			//mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
		mDrawerArrowDrawable = new DrawerArrowDrawable(activity.getResources());
		mDrawerArrowDrawable.setStrokeColor(Color.LTGRAY);
	}

	@Override public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			//inflater.inflate(R.menu.main, menu);
			//showGlobalContextActionBar();
		}

		// ドロワーメニューボタン
		ImageButton btnMenu = (ImageButton) getActivity().findViewById(R.id.abs_menu);
		btnMenu.setBackgroundResource(R.drawable.bk_abs_btn);
		btnMenu.setImageDrawable(mDrawerArrowDrawable);
		btnMenu.setScaleType(ImageView.ScaleType.FIT_CENTER);
		btnMenu.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				if (mDrawerLayout != null && !isDrawerOpen())
				{
					mDrawerLayout.openDrawer(mFragmentContainerView);
				}
				else if (mDrawerLayout != null && isDrawerOpen())
				{
					mDrawerLayout.closeDrawer(mFragmentContainerView);
				}
			}
		});

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.list_layout) {
			Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowTitleEnabled(true);
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//actionBar.setTitle(R.string.app_name);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.abs_layout);
		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
