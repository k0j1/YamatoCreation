package com.yamatocreation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.yamatocreation.fragment.NavigationDrawerFragment;
import com.yamatocreation.fragment.WebFragment;
import com.yamatocreation.gcm.GCMPreference;
import com.yamatocreation.pref.PrefWrapper;

import static com.yamatocreation.gcm.CommonUtilities.SENDER_ID;
import static com.yamatocreation.gcm.CommonUtilities.TAG;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener
{
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public final String PROPERTY_REG_ID = "REG_ID";
	public final String PROPERTY_APP_VERSION = "APP_VERSION";

	public WebFragment mWeb;
	//public ResideMenu mResideMenu;
	//ResideMenuItem mResideItem[];
	private GoogleCloudMessaging gcm;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_main);

		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		// attach to current activity;
		/*
		mResideMenu = new ResideMenu(this);
		mResideMenu.setBackground(R.drawable.logo);
		mResideMenu.attachToActivity(this);

		List<ListItem> strItems = new ArrayList<ListItem>();
		strItems.add(new ListItem(android.R.drawable.ic_menu_share, getString(R.string.NAV_TITLE_SHARE)));
		strItems.add(new ListItem(android.R.drawable.ic_menu_info_details, getString(R.string.NAV_TITLE_ABOUT)));
		strItems.add(new ListItem(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.NAV_TITLE_FINISH)));
		mResideItem = new ResideMenuItem[3];

		for (int i = 0; i < strItems.size(); i++){
			ListItem sItem = strItems.get(i);
			mResideItem[i] = new ResideMenuItem(this, sItem.GetResourceID(), sItem.GetSectionText());
			mResideItem[i].setOnClickListener(this);
			mResideMenu.addMenuItem(mResideItem[i],  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
		}
		mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
		mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
		*/

		if (checkPlayServices())
		{
			// get the instance of GoogleCloudMessaging.
			gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());
			// initialize the Kii SDK!
			//Kii.initialize("2eb09ff2", "d755e37b3d1c644d7466f56f12cb544c", Kii.Site.JP);
			Kii.initialize("8603b07a", "2c3f356e786f93935bf1167bf536ad7b", Kii.Site.JP);

			// Login UFE
			//PrefWrapper prefs = PrefWrapper.getInstance(this);
			//KiiPushAppTask task = new KiiPushAppTask(KiiPushAppTask.MENU_ID.LOGIN, "LOGIN", this);
			//task.execute(prefs.getUsername(), prefs.getPassword());

			// if the id is saved in the preference, it skip the registration and just install push.
			String regId = GCMPreference.getRegistrationId(this.getApplicationContext());
			if (regId.isEmpty()) {
				registerGCM();
			}else{
				Log.v(TAG, String.format("regId is not empty. regID:%s", regId));
			}
		} else {
			Log.v(TAG, "No valid Google Play Services APK found.");
		}

		mWeb = WebFragment.newInstance(0, WebFragment.URL_INIT);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, mWeb).commit();
	}

	private void registerGCM() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params)
			{
				//KiiUser.Builder builder = KiiUser.builderWithName("user_123456");
				//builder.withEmail("user_123456@example.com");
				//builder.withPhone("+819012345678");
				//KiiUser user = builder.build();

				try {
					// call register and save registration ID to preference.
					//String regId = gcm.register(SENDER_ID);
					//GCMPreference.setRegistrationId(MainActivity.this.getApplicationContext(), regId);
					//return null;
					PrefWrapper prefs = PrefWrapper.getInstance(MainActivity.this);
					// call register
					String regId = gcm.register(SENDER_ID);
					// login
					KiiUser.logIn(prefs.getUsername(), prefs.getPassword());
					// install user device
					KiiUser.pushInstallation().install(regId);
					// if all succeeded, save registration ID to preference.
					GCMPreference.setRegistrationId(MainActivity.this.getApplicationContext(), regId);
					Log.v(TAG, String.format("KiiUser Login Success!! regId is not empty. regID:%s", regId));
					return null;
				} catch (Exception e) {
					// Error Handling
					e.printStackTrace();
					Log.v(TAG, "KiiUser Login Error!!");
					return null;
				}
			}
		}.execute();
	}

	private boolean checkPlayServices()
	{
		try {
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (resultCode != ConnectionResult.SUCCESS) {
				if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
					Log.i(TAG, "This device is supported GooglePlayServices");
				} else {
					Log.i(TAG, "This device is not supported GooglePlayServices");
					finish();
				}
				return false;
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		return true;
	}

/*	private void initGCM() {

		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			String regid = getRegistrationId(this);

			if (regid.isEmpty()) {
				registerInBackground();
			} else {
				Log.i(TAG, "registrationID = " + regid);
				try {
					boolean development = false;
					KiiUser.pushInstallation(development).install(regid);
				} catch (ConflictException e) {
					// Already installed.
				} catch (AppException e) {
					// Please check configuration on developer portal.
				} catch (IOException e) {
					// Network error.
				}
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						// gcmはインスタンス変数として保持し、毎回生成しないようにする。
						gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
					}
					String regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					//sendRegistrationIdToBackend();
					// このメソッドは任意のものでアプリケーションのサーバへレジストレーションIDを送信します。
					// regidを渡していないのはインスタンス変数だからですが、パラメータとして渡しもよいでしょう。
					// 必要ならレジストレーションID以外に、ユーザーを特定するようなキーを同時に送信します。

					// 取得したレジストレーションIDをプリファレンスへ保存し、毎回取得しないようにします。
					storeRegistrationId(MainActivity.this, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.i(TAG, msg + "\n");
			}
		}.execute(null, null, null);
	}

	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
	// プリファレンスを取得
	private SharedPreferences getGCMPreferences(Context context)
	{
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	// アプリのバージョン番号を取得
	private static int getAppVersion(Context context)
	{
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
*/
	@Override public void onClick(View v) {
		int number = 0;
		//if(v == mResideItem[0]) number = 1;
		//if(v == mResideItem[1]) number = 2;
		//if(v == mResideItem[2]) number = 3;
		PerformNavigationMenu(number);
	}

	@Override public void onNavigationDrawerItemSelected(int position) {
		int number = position + 1;
		PerformNavigationMenu(number);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//return mResideMenu.dispatchTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public void PerformNavigationMenu(int number)
	{
		switch (number) {
			case 1: // 共有
				IntentShare(mWeb.GetTitle(), mWeb.GetURL());
				break;
			case 2: // ABOUT
				WebFragment Web = WebFragment.newInstance(0, WebFragment.URL_ABOUT);
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction().add(R.id.container, Web).commit();
				break;
			case 3: // 終了
				finish();
				break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				//mTitle = getString(R.string.title_section1);
				break;
			case 2:
				//mTitle = getString(R.string.title_section2);
				break;
			case 3:
				//mTitle = getString(R.string.title_section3);
				break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.abs_layout);

		// 更新ボタン
		ImageButton btnUpdate = (ImageButton) this.findViewById(R.id.abs_update);
		btnUpdate.setBackgroundResource(R.drawable.bk_abs_btn);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mWeb.WebViewReload();
			}
		});
	}


	@Override public boolean onCreateOptionsMenu(Menu menu)
	{
		restoreActionBar();
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.abs_update) {
			mWeb.WebViewReload();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*******************************************
	 * void IntentShare()<br>
	 * 他のアプリで開く（ブラウザ起動）<br>
	 *
	 * @param	strTitle	タイトル
	 * @param	strURL		URL
	 * @return	none
	 *******************************************/
	public void IntentShare(String strTitle, String strURL)
	{
		//Uri uri = Uri.parse(strURL);
		//Intent i = new Intent(Intent.ACTION_VIEW,uri);
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_SUBJECT, strTitle);
		send.putExtra(Intent.EXTRA_TEXT, strURL);
		startActivity(send);
	}


}
