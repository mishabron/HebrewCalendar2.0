package com.mbronshteyn.android.calendar.hebrew.activity.drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.drive.model.File;
import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.dialog.CustomProgressDialog;

public class DataRestoreActivity extends BaseDriveActivity {

	private static final String TAG = "DataRestoreActivity";;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		mProgressDialog = new CustomProgressDialog(this);
		mProgressDialog.showProgressDialog(this.getText(R.string.restoreMessage).toString(), ProgressDialog.STYLE_SPINNER);
	}

	@Override
	public void processFile() {

		if (mDriveServiceHelper != null) {
			Log.d(TAG, "Querying for files.");

			mDriveServiceHelper.queryFiles()
					.addOnSuccessListener(fileList -> {

						File file = fileList.getFiles().get(0);
						String fileId = file.getId();

						Uri fileUri = Uri.fromFile(new java.io.File(Environment.getDataDirectory().getPath()
								+ "/data/com.mbronshteyn.android.calendar.hebrew/databases/hebrewCalendar.db"));
						final java.io.File outFileName = new java.io.File(fileUri.getPath());

						downloadFile(outFileName,fileId);
					})
					.addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
		}

	}

	public void downloadFile(java.io.File outFileName, String fileId) {

		if (mDriveServiceHelper == null) {
			return;
		}
		mDriveServiceHelper.downloadFile(outFileName, fileId)
				.addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						showMessage(context.getText(R.string.restoreSuccessMessage).toString());
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						showMessage(context.getText(R.string.restoreErrorMessage).toString());
					}
				});
		mProgressDialog.killProgressDialog();
		finish();
	}

}
