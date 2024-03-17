package com.mbronshteyn.android.calendar.hebrew.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.activity.drive.DataBackupActivity;
import com.mbronshteyn.android.calendar.hebrew.activity.drive.DataRestoreActivity;
import com.mbronshteyn.android.calendar.hebrew.activity.preference.SettingsActivity;

public class MenuHelper {

	private static Activity activity;

	public MenuHelper(Activity activity) {
		MenuHelper.activity = activity;
	}

	public static void onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.mainQuit:
			activity.finish();
			break;
		case R.id.backup:
			// backaupUtils = new BackupDataUtils(context, activity);
			// backaupUtils.requestBackup();
			final Intent intent = new Intent(activity, DataBackupActivity.class);
			activity.startActivity(intent);
			break;
		case R.id.restore:
			// backaupUtils = new BackupDataUtils(context, activity);
			// backaupUtils.requestRestore();
			new AlertDialog.Builder(activity).setTitle(R.string.restoreWarning).setMessage(R.string.restoreConfirm).setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {
							final Intent intent = new Intent(activity, DataRestoreActivity.class);
							activity.startActivity(intent);
						}
					}).setNegativeButton(android.R.string.no, null).show();
			break;
		case R.id.action_settings:
			activity.startActivity(new Intent(activity, SettingsActivity.class));
			break;
		}
		return false;
	}
}
