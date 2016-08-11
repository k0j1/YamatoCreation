package com.yamatocreation.none;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by k0j1 on 2015/09/20.
 */
public class ProgressDialogFragment extends DialogFragment
{

	public static final String TAG = "ProgressDialogFragment";
	public static ProgressDialogFragment newInstance() {
		return new ProgressDialogFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog d = new ProgressDialog(getActivity());
		d.setIndeterminate(true);
		return d;
	}

}
