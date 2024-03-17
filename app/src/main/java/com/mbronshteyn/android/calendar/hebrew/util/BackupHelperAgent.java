package com.mbronshteyn.android.calendar.hebrew.util;

import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarDatabaseAdapter;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class BackupHelperAgent extends BackupAgentHelper {

	static final String DATA = "databse";

	/* (non-Javadoc)
	 * @see android.app.backup.BackupAgent#onCreate()
	 */
	@Override
	public void onCreate() {

		FileBackupHelper hosts = new FileBackupHelper(this,"../databases/" + HebrewCalendarDatabaseAdapter.DATABASE_NAME);
		addHelper(DATA, hosts);
		
	}
	
}
