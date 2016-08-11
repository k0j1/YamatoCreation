package com.yamatocreation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.DirectPushMessage;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.PushMessageBundleHelper;
import com.kii.cloud.storage.PushToAppMessage;
import com.kii.cloud.storage.PushToUserMessage;
import com.kii.cloud.storage.ReceivedMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by k0j1 on 2015/09/20.
 */
public class KiiPushBroadcastReceiver extends BroadcastReceiver
{
	private static final String TAG = "KiiPushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String gcmMessageType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcmMessageType)) {
			Bundle extras = intent.getExtras();
			ReceivedMessage message = PushMessageBundleHelper.parse(extras);
			KiiUser sender = message.getSender();
			PushMessageBundleHelper.MessageType type = message.pushMessageType();
			switch (type) {
				case PUSH_TO_APP:
					PushToAppMessage appMsg = (PushToAppMessage)message;
					Log.i(TAG, "Received PUSH_TO_APP");
					break;
				case PUSH_TO_USER:
					PushToUserMessage userMsg = (PushToUserMessage)message;
					Log.i(TAG, "Received PUSH_TO_USER");
					break;
				case DIRECT_PUSH:
					DirectPushMessage directMsg = (DirectPushMessage)message;
					Log.i(TAG, "Received DIRECT_PUSH");
					break;
			}
		}
		if (gcmMessageType != null) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(gcmMessageType)) {
				messageReceived(context, intent);
				Log.e(TAG, "Error occurred while gcm messge sending.");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(gcmMessageType)) {
				Log.i(TAG, "Received deleted messages notification");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(gcmMessageType)) {
				Log.i(TAG, "Received Message!");
				messageReceived(context, intent);
			}
			setResultCode(Activity.RESULT_OK);
		} else {
			Log.e(TAG, "Unknown message type.");
		}
	}

	//@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void messageReceived(Context context, Intent intent) {
		fileLog(TAG, "Received message :" + intent.getExtras().toString());
		fileLog(TAG, "Time: " + System.currentTimeMillis());

		// 通知作成
		NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder nNotify = new Notification.Builder(context);
		Bundle extras = intent.getExtras();

		// メッセージ
		String msg = intent.getStringExtra("msg");
		if(msg.isEmpty()) msg = "Received message!";

		// タイムスタンプがあればメッセージに追加
		String strTimeStamp = intent.getStringExtra("when");
		if(null != strTimeStamp)
		{
			long timestamp = Long.parseLong(strTimeStamp);
			if (0 < timestamp) {
				Date date = new Date(timestamp);
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
				msg += String.format("\n%s", df.format(date));
			}
		}
		Log.i(TAG, msg);

		// ロゴ
		Bitmap bmpLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

		// 通知の設定
		nNotify.setSmallIcon(R.mipmap.ic_launcher);
		nNotify.setLargeIcon(bmpLogo);
		nNotify.setTicker(msg);
		nNotify.setContentTitle(context.getString(R.string.app_name));
		nNotify.setContentText(msg);
		nNotify.setNumber(1);
		nNotify.setWhen(System.currentTimeMillis());
		nNotify.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
		nNotify.setAutoCancel(true);
		//n.contentIntent = ;

		Intent i = new Intent(context.getApplicationContext(), ShowPushMessageActivity.class);
		i.putExtras(extras);
		i.setAction(Context.ACTIVITY_SERVICE);

		PendingIntent pend = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		nNotify.setContentIntent(pend);
		//n.setLatestEventInfo(context.getApplicationContext(), context.getString(R.string.app_name), msg, pend);

		// BigTextStyle を適用
		if(null != strTimeStamp)
		{
			// BigPictureStyle
			Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle(nNotify);
			bigPictureStyle.bigPicture(bmpLogo);
			bigPictureStyle.setBigContentTitle(context.getString(R.string.app_name));
			bigPictureStyle.setSummaryText(msg);

			nManager.cancelAll();
			nManager.notify(1, bigPictureStyle.build());
			Log.i(TAG, "Notification Big!");
		}
		else if(msg.length() >= 20)
		{
			Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(nNotify);
			bigTextStyle.bigText(msg);
			bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
			bigTextStyle.setSummaryText(msg);

			nManager.cancelAll();
			nManager.notify(1, bigTextStyle.build());
			Log.i(TAG, "Notification Big!");
		}
		else
		{
			nManager.cancelAll();
			nManager.notify(1, nNotify.build());
			Log.i(TAG, "Notification Small!");
		}
	}

	private void fileLog(String tag, String message) {
		Log.i(tag, message);
		final String path = "pushlog.txt";
		File f = new File(Environment.getExternalStorageDirectory(), path);

		BufferedWriter bw = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(tag + " : " + message);
			bw.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}