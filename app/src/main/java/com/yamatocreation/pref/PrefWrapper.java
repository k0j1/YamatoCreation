package com.yamatocreation.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PrefWrapper {

	private SharedPreferences pref;

	//private static final String USERNAME_DEF = "k0j123t@gmail.com";
	//private static final String PASS_DEF = "s1110137";
	//private static final String USERNAME_DEF = "YamatoCreation";
	//private static final String PASS_DEF = "999998";
	private static final String USERNAME_DEF = "yamatocreation";
	private static final String PASS_DEF = "1111";

	private static PrefWrapper INSTANCE = null;
	private PrefWrapper(SharedPreferences pref) {
		this.pref = pref;
	}

	private static final String USERNAMEKEY = "username";
	private static final String PASSWORDKEY = "password";

	public static synchronized PrefWrapper getInstance(Context ctx) {
		if (INSTANCE == null) {
			SharedPreferences sp = ctx.getSharedPreferences("Setting",
					Context.MODE_PRIVATE);
			INSTANCE = new PrefWrapper(sp);
		}
		return INSTANCE;
	}

	public String getUsername() {
		return pref.getString(USERNAMEKEY, USERNAME_DEF);
	}

	public String getPassword() {
		return pref.getString(PASSWORDKEY, PASS_DEF);
	}

	public void setUsername(String username) {
		if (TextUtils.isEmpty(username)) {
			pref.edit().remove(USERNAMEKEY).commit();
		} else {
			pref.edit().putString(USERNAMEKEY, username).commit();
		}
	}

	public void setPassword(String password) {
		if (TextUtils.isEmpty(password)) {
			pref.edit().remove(PASSWORDKEY).commit();
		} else {
			pref.edit().putString(PASSWORDKEY, password).commit();
		}
	}

}