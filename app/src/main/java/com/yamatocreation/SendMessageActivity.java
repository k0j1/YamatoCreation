package com.yamatocreation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiGroup;
import com.kii.cloud.storage.KiiPushMessage;
import com.kii.cloud.storage.KiiPushSubscription;
import com.kii.cloud.storage.KiiTopic;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.GroupOperationException;
import com.kii.cloud.storage.exception.app.AppException;
import com.yamatocreation.pref.PrefWrapper;

import java.io.IOException;

/**
 * Created by k0j1 on 2015/10/02.
 */
public class SendMessageActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_send_message);

		Button btnSend = (Button)findViewById(R.id.send_msg_btn);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				EditText Msg = (EditText) findViewById(R.id.send_msg_edit);
				SendMessage(Msg.getText().toString());
			}
		});
	}

	public void SendMessage(final String strMsg)
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override protected String doInBackground(Void... params)
			{
				try {
					// ログイン
					PrefWrapper prefs = PrefWrapper.getInstance(SendMessageActivity.this);
					KiiUser.logIn(prefs.getUsername(), prefs.getPassword());
					// グループの作成
					String groupName = "SampleGroup";
					KiiGroup group = Kii.group(groupName);
					group.save();
					// トピックの作成
					String topicName = "SampleGroupTopic";
					KiiTopic topic = group.topic(topicName);
					boolean exists = topic.exists();
					if (!exists) {
						topic.save();
					}
					// トピックの購読
					KiiUser user = KiiUser.getCurrentUser();
					KiiPushSubscription sub = user.pushSubscription();
					if (!sub.isSubscribed(topic)) {
						sub.subscribe(topic);
					}
					// メッセージ送信
					group.refresh();
					// Build a push message.
					KiiPushMessage.Data data = new KiiPushMessage.Data();
					data.put("msg", strMsg);
					data.put("int", 1);
					data.put("bool", false);
					data.put("double", 1.12);
					KiiPushMessage message = KiiPushMessage.buildWith(data).build();
					// Send the push message.
					topic.sendMessage(message);
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				catch (GroupOperationException goe) {
					goe.printStackTrace();
				}
				catch (AppException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
}
