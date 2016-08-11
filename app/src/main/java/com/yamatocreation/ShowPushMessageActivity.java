package com.yamatocreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Iterator;

/**
 * Created by k0j1 on 2015/09/20.
 */
public class ShowPushMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Show Content
		setContentView(R.layout.showview);
		// Get intent bundle
		final Bundle b = getIntent().getExtras();
		// Expand
		TableLayout table = (TableLayout) findViewById(R.id.showview);
		setTextToTable(b, this, table);

		// アプリ起動
		final Button btnApp = (Button) findViewById(R.id.open_app);
		btnApp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
			}
		});
		// Parse Message
		final Button button = (Button) findViewById(R.id.parse);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						PushMessageParseActivity.class);
				i.putExtras(b);
				startActivity(i);
			}
		});
	}

	public static void setTextToTable(Bundle bundle, Activity activity,
	                                  TableLayout table) {
		Iterator<String> keys = bundle.keySet().iterator();
		String hkey;
		String hvalue;

		while (keys.hasNext()) {
			hkey = keys.next();
			hvalue = bundle.getString(hkey);

			android.widget.TableRow row = new android.widget.TableRow(activity);
			row.setLayoutParams(new TableLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			row.setGravity(Gravity.CENTER);
			table.addView(row);

			TextView tvkey = new TextView(activity);
			tvkey.setText(hkey + "  ");
			row.addView(tvkey);

			TextView tvvalue = new TextView(activity);
			tvvalue.setText(hvalue);
			row.addView(tvvalue);
		}
	}

}