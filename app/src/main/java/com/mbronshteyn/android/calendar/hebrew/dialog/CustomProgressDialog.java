package com.mbronshteyn.android.calendar.hebrew.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog {

	private ProgressDialog m_ProgressDialog = null;
	private boolean isDialogShowing = false;
	private Context context;

	public CustomProgressDialog(Context context) {
		this.context = context;
	}

	public boolean isDialogShowing() {
		return isDialogShowing;
	}

	public void setDialogShowing(boolean isDialogShowing) {
		this.isDialogShowing = isDialogShowing;
	}

	public void setProgressDialog(int style) {

		if (m_ProgressDialog == null) {
			m_ProgressDialog = new ProgressDialog(context);
			m_ProgressDialog.setTitle("Please wait...");
			m_ProgressDialog.setProgressStyle(style);
		}
	}

	public void showProgressDialog() {
		isDialogShowing = true;
		if (m_ProgressDialog != null && !m_ProgressDialog.isShowing()) {
			m_ProgressDialog.show();
		}
	}

	public void showProgressDialog(String message, int style) {
		isDialogShowing = true;

		if (m_ProgressDialog != null && !m_ProgressDialog.isShowing()) {
			m_ProgressDialog.show();
		} else if (m_ProgressDialog == null) {
			setProgressDialog(style);
			m_ProgressDialog.show();
		}
		m_ProgressDialog.setMessage(message);
	}

	public void dismissProgressDialog() {
		if (m_ProgressDialog != null && m_ProgressDialog.isShowing()) {
			m_ProgressDialog.dismiss();
		}
	}

	public void killProgressDialog() {
		dismissProgressDialog();
		isDialogShowing = false;
		m_ProgressDialog = null;
	}

}
