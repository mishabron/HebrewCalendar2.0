package com.mbronshteyn.android.calendar.hebrew.activity.drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.dialog.CustomProgressDialog;
import com.mbronshteyn.android.calendar.hebrew.util.drive.GoogleDriveFileHolder;

public class DataBackupActivity extends BaseDriveActivity {

	private AccountPicker tmo;
	private Context context;
	private static final String TAG = "DataBackupActivity";;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.showProgressDialog(context.getText(R.string.backupMessage).toString(), ProgressDialog.STYLE_SPINNER);
	}

	public void processFile()  {

		Uri fileUri = Uri.fromFile(new java.io.File(Environment.getDataDirectory().getPath()
				+ "/data/com.mbronshteyn.android.calendar.hebrew/databases/hebrewCalendar.db"));

		final java.io.File fileContent = new java.io.File(fileUri.getPath());

		uploadFile(fileContent,null);

	}

	private void uploadFile(final java.io.File localFile, @Nullable final String folderId) {

		if (mDriveServiceHelper == null) {
			return;
		}
		mDriveServiceHelper.uploadFile(localFile, "/", folderId)
				.addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
					@Override
					public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
						showMessage(context.getText(R.string.backupSuccessMessage).toString() + googleDriveFileHolder.getName());
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						showMessage(context.getText(R.string.backupErrorMessage).toString());
					}
				});

		mProgressDialog.killProgressDialog();
		finish();
	}
}
