package com.yamatocreation.gcm;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.yamatocreation.gcm.CommonUtilities.SERVER_URL;
import static com.yamatocreation.gcm.CommonUtilities.TAG;

public class ServerUtilities
{
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	public static boolean register(final Context context, final String regId)
	{
		Random random = new Random();

		Log.i(TAG, "registering device (regId = " + regId + ")");

		// サーバアプリの登録URLを設定する。
		String serverUrl = CommonUtilities.SERVER_URL + "/register.php";

		// 端末のレジストレーションIDをパラメータとして付加する。
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		// 空き時間間隔（exponential backoff：繰り返すごとに空き時間を大きくして、送信タイミングを分散する）を持って、リトライします。
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				//displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));

				// サーバアプリにポストします。
				post(serverUrl, params);

				// サーバアプリに登録したことをGCMに登録します。
				//GCMRegistrar.setRegisteredOnServer(context, true);

				// 登録メッセージを表示します。
				//String message = context.getString(R.string.server_registered);
				//CommonUtilities.displayMessage(context, message);

				// 成功
				return true;

			} catch (IOException e) {
				Log.e(TAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// 完了前に、アクティビティが終了
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// 待ち時間を増加
				backoff *= 2;
			}
		}
		// エラーメッセージを表示する。
		//String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
		//CommonUtilities.displayMessage(context, message);
		return false;
	}

	public static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");

		// 端末のレジストレーションIDをパラメータとして付加する。
		String serverUrl = SERVER_URL + "/unregister.php";

		// 端末のレジストレーションIDをパラメータとして付加する。
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);

		try {

			// サーバアプリにポストします。
			post(serverUrl, params);

			// サーバアプリから解除したことをGCMに登録します。
			//GCMRegistrar.setRegisteredOnServer(context, false);

			// 解除メッセージを表示します。
			//String message = context.getString(R.string.server_unregistered);
			//CommonUtilities.displayMessage(context, message);

		} catch (IOException e) {
			// この時点では、GCMからは解除できているが、サーバアプリからは解除されていないが、再度、解除を実行する必要はない。
			// サーバがメッセージ送信するとき、その端末は未登録エラーとなり、その端末の解除が実行される。
			//String message = context.getString(R.string.server_unregister_error, e.getMessage());
			//CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 *
	 * @throws IOException propagated from POST.
	 */
	private static void post(String endpoint, Map<String, String> params)
			throws IOException {
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Map.Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
					.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

}