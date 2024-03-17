package com.mbronshteyn.android.calendar.hebrew.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

public class HebrewCalendarUtils {

	public static Map<String, Drawable> icons = null;

	public static void initEventNames(Context context, HebrewEvent[] hebrewEvents) {

		for (int i = 0; i < hebrewEvents.length; i++) {
			HebrewEvent event = hebrewEvents[i];
			event.setEventName(getEventName(context, event));
		}
	}

	private static String getEventName(Context context, HebrewEvent event) {

		String eventName = "";

		switch (event) {
		case ROSH_HASHANA:
			eventName = (String) context.getText(R.string.ROSH_HASHANA);
			break;
		case YOM_KIPPUR:
			eventName = (String) context.getText(R.string.YOM_KIPPUR);
			break;
		case SUKKOT:
			eventName = (String) context.getText(R.string.SUKKOT);
			break;
		case SHEMINI_ATZERET:
			eventName = (String) context.getText(R.string.SHEMINI_ATZERET);
			break;
		case SIMCHA_TORAH:
			eventName = (String) context.getText(R.string.SIMCHA_TORAH);
			break;
		case CHANUKAH:
			eventName = (String) context.getText(R.string.CHANUKAH);
			break;
		case TUBSHEVAT:
			eventName = (String) context.getText(R.string.TUBSHEVAT);
			break;
		case PURIM:
			eventName = (String) context.getText(R.string.PURIM);
			break;
		case PESACH:
			eventName = (String) context.getText(R.string.PESACH);
			break;
		case OMER:
			eventName = (String) context.getText(R.string.OMER);
			break;
		case LAG_BAOMER:
			eventName = (String) context.getText(R.string.LAG_BAOMER);
			break;
		case SHAVUOT:
			eventName = (String) context.getText(R.string.SHAVUOT);
			break;
		case TISHA_BAV:
			eventName = (String) context.getText(R.string.TISHA_BAV);
			break;
		case YAHRZEIT:
			eventName = (String) context.getText(R.string.YAHRZEIT);
			break;
		case SHABATH:
			eventName = (String) context.getText(R.string.SHABATH);
			break;
		case BIRTHDAY:
			eventName = (String) context.getText(R.string.BIRTHDAY);
			break;
		case ANNIVERSARY:
			eventName = (String) context.getText(R.string.ANNIVERSARY);
			break;
		case OTHER:
			eventName = (String) context.getText(R.string.OTHER);
			break;
		case ROSH_HODESH:
			eventName = (String) context.getText(R.string.ROSH_HODESH);
			break;
		case CANDLE_LIGHTING:
			eventName = (String) context.getText(R.string.CANDLELIGHTING_TIME);
			break;
		case SHABBOS_END:
			eventName = (String) context.getText(R.string.SHABBOS_YOMTOV_END);
			break;
		case TAMUZ_17:
			eventName = (String) context.getText(R.string.TAMUZ_17);
			break;
		case TEVETH_10:
			eventName = (String) context.getText(R.string.TEVETH_10);
			break;
		}

		return eventName;
	}

	public static void initMonthNames(Context context, HebrewMonth[] months) {

		for (int i = 0; i < months.length; i++) {
			HebrewMonth month = months[i];
			month.setMonthName(getMonthName(context, month));
		}
	}

	private static String getMonthName(Context context, HebrewMonth month) {

		String monthName = "";

		switch (month) {
		case TISHREI:
			monthName = (String) context.getText(R.string.TISHREI);
			break;
		case CHESHVAN:
			monthName = (String) context.getText(R.string.CHESHVAN);
			break;
		case KISLEV:
			monthName = (String) context.getText(R.string.KISLEV);
			break;
		case TEVETH:
			monthName = (String) context.getText(R.string.TEVETH);
			break;
		case SHEVAT:
			monthName = (String) context.getText(R.string.SHEVAT);
			break;
		case ADAR:
			monthName = (String) context.getText(R.string.ADAR);
			break;
		case ADAR_I:
			monthName = (String) context.getText(R.string.ADAR_I);
			break;
		case ADAR_II:
			monthName = (String) context.getText(R.string.ADAR_II);
			break;
		case NISAN:
			monthName = (String) context.getText(R.string.NISAN);
			break;
		case IYAR:
			monthName = (String) context.getText(R.string.IYAR);
			break;
		case SIVAN:
			monthName = (String) context.getText(R.string.SIVAN);
			break;
		case TAMMUZ:
			monthName = (String) context.getText(R.string.TAMMUZ);
			break;
		case AV:
			monthName = (String) context.getText(R.string.AV);
			break;
		case ELUL:
			monthName = (String) context.getText(R.string.ELUL);
			break;
		}

		return monthName;
	}

	public static Map<Integer, String> getLocalCalendars(ContentResolver cr) {

		Map<Integer, String> myCalendars = new HashMap<Integer, String>();

		String calendarIdColumn = "_id";
		String calendarNameColumn = "name";

		if (Integer.parseInt(Build.VERSION.SDK) > 13) {
			calendarNameColumn = "calendar_displayName";
		}

		String[] projection = new String[] { calendarIdColumn, calendarNameColumn };

		String selection = "selected=1";
		String path = "calendars";

		Cursor managedCursor = getCalendarManagedCursor(cr, projection, selection, path);

		if (managedCursor != null && managedCursor.moveToFirst()) {
			String calName;
			int calId;
			int nameColumn = managedCursor.getColumnIndex(calendarNameColumn);
			int idColumn = managedCursor.getColumnIndex(calendarIdColumn);
			do {
				calName = managedCursor.getString(nameColumn);
				calId = managedCursor.getInt(idColumn);
				myCalendars.put(calId, calName);
			} while (managedCursor.moveToNext());
		}

		return myCalendars;
	}

	public static void updateCalendar(ContentResolver cr, Context context, int calId, ArrayList<HebrewCalendarEvent> events, int reminderMinutes) {

		Calendar calendar = new GregorianCalendar();

		String calendarBase = getCalendarUriBase(cr);

		for (HebrewCalendarEvent hEvent : events) {

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (hEvent.isSelected()) {

				String title = hEvent.getDescription();
				String description = getMonthName(context, hEvent.getMonth()) + " " + hEvent.getDay();

				// process omer
				if (hEvent.getEvent().equals(HebrewEvent.OMER)) {

					calendar.setTime(hEvent.getGrDate());

					for (int j = 1; j < 50; j++) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						String day = (String) context.getText(R.string.day);
						if (j > 1) {
							day = (String) context.getText(R.string.days);
						}

						title = hEvent.getEvent().getEventName() + " " + j + " " + day;

						insertIvent(calendarBase, cr, calId, title, description, calendar.getTime(), reminderMinutes);

						// roll to the next day
						calendar.add(Calendar.DAY_OF_YEAR, 1);
						HebrewDate newhDate = DateConverter.getHebrewDate(calendar.getTime());
						description = getMonthName(context, newhDate.getHebrewMonth()) + " " + newhDate.getDay();
					}
				} else {
					insertIvent(calendarBase, cr, calId, title, description, hEvent.getGrDate(), reminderMinutes);
				}
			}
		}
	}

	private static void insertIvent(String calendarBase, ContentResolver cr, int calId, String title, String description, Date date, int reminderMinutes) {

		// this is a all day event
		// hours, minutes and seconds should be set to 0

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

		long startTime = calendar.getTimeInMillis();

		calendar.add(Calendar.HOUR_OF_DAY, 24);

		long endTime = calendar.getTimeInMillis();

		Uri uri = Uri.parse(calendarBase + "events");

		ContentValues calendarEvent = new ContentValues();

		calendarEvent.put("calendar_id", calId);
		calendarEvent.put("title", title);
		calendarEvent.put("description", description);
		calendarEvent.put("dtstart", startTime);
		calendarEvent.put("dtend", endTime);
		calendarEvent.put("allDay", 1);
		calendarEvent.put("hasAlarm", 1);
		calendarEvent.put("eventTimezone", "UTC");

		Uri newEvent = cr.insert(uri, calendarEvent);

		// create reminder
		if (reminderMinutes != 0) {
			long id = Long.parseLong(newEvent.getLastPathSegment());

			ContentValues values = new ContentValues();
			values.put("event_id", id);
			values.put("method", 1);
			values.put("minutes", reminderMinutes);

			uri = Uri.parse(calendarBase + "reminders");

			cr.insert(uri, values);
		}

	}

	public static Map<String, Drawable> loadIcons(Resources resources) {

		Map<String, Drawable> iconList = new HashMap<String, Drawable>();

		iconList.put("shofar", resources.getDrawable(R.drawable.shofar));
		iconList.put("scales", resources.getDrawable(R.drawable.scales));
		iconList.put("sukkot", resources.getDrawable(R.drawable.sukkot));
		iconList.put("torah", resources.getDrawable(R.drawable.torah));
		iconList.put("one", resources.getDrawable(R.drawable.one));
		iconList.put("two", resources.getDrawable(R.drawable.two));
		iconList.put("three", resources.getDrawable(R.drawable.three));
		iconList.put("four", resources.getDrawable(R.drawable.four));
		iconList.put("five", resources.getDrawable(R.drawable.five));
		iconList.put("six", resources.getDrawable(R.drawable.six));
		iconList.put("seven", resources.getDrawable(R.drawable.seven));
		iconList.put("eight", resources.getDrawable(R.drawable.eight));
		iconList.put("fruits", resources.getDrawable(R.drawable.fruits));
		iconList.put("purim", resources.getDrawable(R.drawable.purim));
		iconList.put("matzah", resources.getDrawable(R.drawable.matzah));
		iconList.put("omer", resources.getDrawable(R.drawable.omer));
		iconList.put("lagbaomer", resources.getDrawable(R.drawable.lagbaomer));
		iconList.put("luchos", resources.getDrawable(R.drawable.luchos));
		iconList.put("temple", resources.getDrawable(R.drawable.temple));
		iconList.put("yahrzeit", resources.getDrawable(R.drawable.yahrzeit));
		iconList.put("anniversary", resources.getDrawable(R.drawable.univesary));
		iconList.put("birthday", resources.getDrawable(R.drawable.birthday));
		iconList.put("other", resources.getDrawable(R.drawable.calendar));
		iconList.put("moon", resources.getDrawable(R.drawable.moon));
		iconList.put("shabbos", resources.getDrawable(R.drawable.candlesticks));
		iconList.put("havdalah", resources.getDrawable(R.drawable.havdalah));
		iconList.put("havdalahset", resources.getDrawable(R.drawable.havdalahset));
		iconList.put("shabbos_theme", resources.getDrawable(R.drawable.shabbos_theme));
		iconList.put("havdalah_theme", resources.getDrawable(R.drawable.havdalah_theme));

		return iconList;
	}

	/**
	 * @param projection
	 * @param selection
	 * @param path
	 * @return
	 */
	private static Cursor getCalendarManagedCursor(ContentResolver cr, String[] projection, String selection, String path) {

		Uri calendars = Uri.parse("content://calendar/" + path);

		Cursor managedCursor = null;
		try {
			managedCursor = cr.query(calendars, projection, selection, null, null);
		} catch (IllegalArgumentException e) {
			// eat
		}

		if (managedCursor == null) {
			// try again
			calendars = Uri.parse("content://com.android.calendar/" + path);
			try {
				managedCursor = cr.query(calendars, projection, null, null, null);
			} catch (IllegalArgumentException e) {
				// eat
			}
		}

		return managedCursor;
	}

	/*
	 * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
	 */
	private static String getCalendarUriBase(ContentResolver cr) {

		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;

		try {
			managedCursor = cr.query(calendars, null, null, null, null);
		} catch (Exception e) {
			// eat
		}

		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = cr.query(calendars, null, null, null, null);
			} catch (Exception e) {
				// eat
			}

			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}

		}

		return calendarUriBase;
	}

}
