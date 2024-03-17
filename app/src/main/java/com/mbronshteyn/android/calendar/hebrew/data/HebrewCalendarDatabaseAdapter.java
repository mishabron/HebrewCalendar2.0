package com.mbronshteyn.android.calendar.hebrew.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

import java.util.ArrayList;
import java.util.List;

/**
 * @author misha
 */
public class HebrewCalendarDatabaseAdapter {

	public static final String DATABASE_NAME = "hebrewCalendar.db";
	public static final String DEFAULT_EVENTS_TABLE = "hebrewEvents";
	public static final String CUSTOM_EVENTS_TABLE = "customEvents";
	public static final int DATABASE_VERSION = 54;

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";

	// The name and column index of each column in your database.
	public static final String KEY_NAME = "name";
	public static final String KEY_MONTH = "month";
	public static final String KEY_DAY = "day";
	public static final String KEY_IMAGE = "image";
	public static final String SHABBOS = "shabbos";
	public static final String START_YEAR = "start_year";
	public static final String ONLY_IF = "only_if";
	public static final String NOT_IF = "not_if";
	public static final String DESCRIPTION = "description";

	private static final String DEFAULT_CREATE = "create table if not exists " + DEFAULT_EVENTS_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null, " + KEY_MONTH + " text not null, " + KEY_DAY + " integer not null, " + KEY_IMAGE + " text not null, " + SHABBOS
			+ " integer, " + START_YEAR + " integer, " + ONLY_IF + " text, " + NOT_IF + " text, " + DESCRIPTION + " text" + ");";

	private static final String CUSTOM_CREATE = "create table if not exists " + CUSTOM_EVENTS_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null, " + KEY_MONTH + " text not null, " + KEY_DAY + " integer not null, " + KEY_IMAGE + " text not null, " + SHABBOS
			+ " integer, " + START_YEAR + " integer, " + ONLY_IF + " text, " + NOT_IF + " text, " + DESCRIPTION + " text" + ");";

	private SQLiteDatabase db;
	private static Context context;
	/**
	 * @uml.property name="dbHelper"
	 * @uml.associationEnd
	 */
	private myDbHelper dbHelper;

	public HebrewCalendarDatabaseAdapter(Context context) {
		HebrewCalendarDatabaseAdapter.context = context;
		dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		dbHelper.getWritableDatabase();
	}

	public HebrewCalendarDatabaseAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	private static long insertEvent(SQLiteDatabase db, String tableName, HebrewCalendarEvent hebrewEvent) {

		ContentValues values = new ContentValues();

		values.put(KEY_NAME, hebrewEvent.getEvent().toString());
		values.put(KEY_MONTH, hebrewEvent.getMonth().toString());
		values.put(KEY_DAY, hebrewEvent.getDay());
		values.put(KEY_IMAGE, hebrewEvent.getImage());
		values.put(SHABBOS, hebrewEvent.isShabbos());
		values.put(START_YEAR, hebrewEvent.getStart_year());
		values.put(ONLY_IF, hebrewEvent.getOnlyIf());
		values.put(NOT_IF, hebrewEvent.getNotIf());
		values.put(DESCRIPTION, hebrewEvent.getDescription());

		long index = 0;
		try {
			index = db.insert(tableName, null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return index;
	}

	private static void loadEvents(SQLiteDatabase db) {

		List<HebrewCalendarEvent> events = HebrewCalendarAdapter.generateDefaultEvents(context);

		for (HebrewCalendarEvent hEvent : events) {
			insertEvent(db, DEFAULT_EVENTS_TABLE, hEvent);
		}

	}

	public void insertCustomEvent(HebrewCalendarEvent event) {
		insertEvent(db, CUSTOM_EVENTS_TABLE, event);
	}

	public void deleteCustomEvent(int id) {
		db.delete(CUSTOM_EVENTS_TABLE, "_id=?", new String[] { Long.toString(id) });
	}

	public void updateCustomEvent(HebrewCalendarEvent hEvent) {

		ContentValues updateEvent = new ContentValues();

		updateEvent.put(DESCRIPTION, hEvent.getDescription());
		updateEvent.put(KEY_MONTH, hEvent.getMonth().toString());
		updateEvent.put(KEY_DAY, hEvent.getDay());
		updateEvent.put(START_YEAR, hEvent.getStart_year());

		int id = hEvent.getId();

		db.update(CUSTOM_EVENTS_TABLE, updateEvent, "_id=?", new String[] { Long.toString(id) });

	}

	public List<HebrewCalendarEvent> getEventsForTheMonth(HebrewMonth month) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String defaultSql = getSelectSQL(DEFAULT_EVENTS_TABLE, 0) + " WHERE " + KEY_MONTH + " = '" + month.toString() + "'";
		String customSql = getSelectSQL(CUSTOM_EVENTS_TABLE, 1) + " WHERE " + KEY_MONTH + " = '" + month.toString() + "'";
		String sql = qb.buildUnionQuery(new String[] { defaultSql, customSql }, KEY_DAY, null);

		List<HebrewCalendarEvent> events = getEventsFromDatabase(sql);

		return events;
	}

	public List<HebrewCalendarEvent> getAllEvents() {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String defaultSql = getSelectSQL(DEFAULT_EVENTS_TABLE, 0);
		String customSql = getSelectSQL(CUSTOM_EVENTS_TABLE, 1);
		String sql = qb.buildUnionQuery(new String[] { defaultSql, customSql }, KEY_DAY, null);

		List<HebrewCalendarEvent> events = getEventsFromDatabase(sql);

		return events;
	}

	private List<HebrewCalendarEvent> getEventsFromDatabase(String sql) {

		ArrayList<HebrewCalendarEvent> events = new ArrayList<HebrewCalendarEvent>();

		Cursor cur = null;

		cur = db.rawQuery(sql, null);

		cur.moveToFirst();
		try {
			for (int i = 0; i < cur.getCount(); i++) {

				HebrewCalendarEvent hEvent = new HebrewCalendarEvent();

				hEvent.setId(cur.getInt(0));

				String eventType = cur.getString(1);
				HebrewEvent event = HebrewEvent.valueOf(eventType);

				hEvent.setEvent(event);
				hEvent.setMonth(HebrewMonth.valueOf(cur.getString(2)));
				hEvent.setDay(cur.getInt(3));
				hEvent.setImage(cur.getString(4));
				hEvent.setShabbos(cur.getInt(5) == 0 ? false : true);
				hEvent.setStart_year(cur.getInt(6));
				hEvent.setOnlyIf(cur.getString(7));
				hEvent.setNotIf(cur.getString(8));
				hEvent.setDescription(cur.getString(9));
				hEvent.setCustom(cur.getInt(10) == 0 ? false : true);

				events.add(hEvent);

				cur.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cur.close();
		}
		return events;
	}

	private String getSelectSQL(String tableName, int extra) {

		String sql = "SELECT _id, " + KEY_NAME + "," + KEY_MONTH + "," + KEY_DAY + "," + KEY_IMAGE + "," + SHABBOS + "," + START_YEAR + "," + ONLY_IF + ","
				+ NOT_IF + "," + DESCRIPTION + "," + extra + " FROM " + tableName;

		return sql;
	}

	private static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DEFAULT_CREATE);
				db.execSQL(CUSTOM_CREATE);
				loadEvents(db);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int _oldVersion, int _newVersion) {

			// The simplest case is to drop the old table and create a new one.
			db.execSQL("DROP TABLE IF EXISTS " + DEFAULT_EVENTS_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " + CUSTOM_EVENTS_TABLE);
			// Create a new one.
			onCreate(db);
		}
	}

}
