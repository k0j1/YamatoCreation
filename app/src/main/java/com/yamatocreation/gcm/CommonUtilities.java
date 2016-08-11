package com.yamatocreation.gcm;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities
{
	// 端末のレジストレーションキーを登録・解除するサーバアプリのURLを指定します。
	static final String SERVER_URL = "http://www.notice.co.jp/gcm";

	// 前回作成したプロジェクトのProject Numberを指定します。
	static public final String SENDER_ID = "558290044960";

	/**
	 * Tag used on log messages.
	 */
	static public final String TAG = "GCM"; //

	/**
	 * Intent used to display a message in the screen.
	 */
	static final String DISPLAY_MESSAGE_ACTION =
			"com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by
	 * the UI and the background service.
	 *
	 * @param context application's context.
	 * @param message message to be displayed.
	 */
	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}