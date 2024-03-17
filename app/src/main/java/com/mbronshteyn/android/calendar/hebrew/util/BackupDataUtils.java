package com.mbronshteyn.android.calendar.hebrew.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.Context;
import android.content.DialogInterface;

import com.mbronshteyn.android.calendar.hebrew.R;

public class BackupDataUtils {
	
	private Context context;
	private Activity activity;
	private AlertDialog alertDialog;
	
	public  BackupDataUtils(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
		
        alertDialog = new AlertDialog.Builder(this.activity).create();
        alertDialog.setTitle(R.string.dataRestore_title);
        alertDialog.setIcon(R.drawable.calendar);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            return;
        } }); 			
	}
	
	
	public BackupDataUtils(Context context) {
		this.context = context;
	}


	public void requestBackup(){
		
		BackupManager backupManager = new BackupManager(context);
		
		backupManager.dataChanged();
	}
	
	public void requestRestore(){
		
		BackupManager backupManager = new BackupManager(context);
		
		try {
			backupManager.requestRestore(new RestoreObserver() {
				@Override
				public void restoreFinished(int error) {

					if (error == 0){
						alertDialog.setMessage(context.getText(R.string.dataRestore_success));					
					}
					else{
						alertDialog.setMessage(context.getText(R.string.dataRestore_failed));						
					}
					alertDialog.show();
				}						
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
