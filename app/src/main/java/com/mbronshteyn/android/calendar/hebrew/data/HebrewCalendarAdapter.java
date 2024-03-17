package com.mbronshteyn.android.calendar.hebrew.data;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.SQLException;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.util.BackupDataUtils;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;
import com.mbronshteyn.calendar.hebrew.exceptions.HebrewDateException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HebrewCalendarAdapter {

	public static List<HebrewCalendarEvent> getEventsForYear(Context context, int year) {

		ArrayList<HebrewCalendarEvent> events = new ArrayList<HebrewCalendarEvent>();

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		// get month list for the year
		List<HebrewMonth> months = CalendarUtils.hebrewMonthsInYear(year);

		try {
			dataAdapter.open();

			// get events for each month
			for (HebrewMonth month : months) {

				List<HebrewCalendarEvent> monthEvents = dataAdapter.getEventsForTheMonth(month);

				for (HebrewCalendarEvent hEvent : monthEvents) {

					// add only valid Hebrew dates
					if (HebrewDate.hebrewDateValid(year, month, hEvent.getDay())) {
						// set Gregorian date
						hEvent.setGrDate(year, month, hEvent.getDay());
						events.add(hEvent);
					}

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HebrewDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}

		return applyEventRules(events, year);
	}

	/**
	 * insert new custom non-yahrzeit event
	 * 
	 * @param context
	 * @param event
	 */
	public static void insertCustomEvent(Context context, HebrewCalendarEvent event) {

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		try {

			dataAdapter.open();

			dataAdapter.insertCustomEvent(event);

			// advance start year to the next year
			event.setStart_year(event.getStart_year() + 1);

			// check Hebrew month rules
			if (event.getMonth().equals(HebrewMonth.ADAR)) {
				// insert Adar II date
				event.setNotIf(HebrewMonth.ADAR.toString() + "," + event.getDay());
				event.setMonth(HebrewMonth.ADAR_II);
				dataAdapter.insertCustomEvent(event);
			} else if (event.getMonth().equals(HebrewMonth.ADAR_I) || event.getMonth().equals(HebrewMonth.ADAR_II)) {

				if (event.getDay() == 30) {
					// insert Nisan 1 date
					event.setMonth(HebrewMonth.NISAN);
					event.setDay(1);
					event.setNotIf(HebrewMonth.ADAR_I.toString() + ",30");
					dataAdapter.insertCustomEvent(event);
				} else {
					// insert Adar date
					event.setNotIf(event.getMonth() + "," + event.getDay());
					event.setMonth(HebrewMonth.ADAR);
					dataAdapter.insertCustomEvent(event);
				}
			}

			// check CHESHVAN 30 rules
			if (event.getMonth().equals(HebrewMonth.CHESHVAN) && event.getDay() == 30) {
				// insert Adar II date
				event.setMonth(HebrewMonth.KISLEV);
				event.setDay(1);
				event.setNotIf(HebrewMonth.CHESHVAN.toString() + ",30");
				dataAdapter.insertCustomEvent(event);
			}

			// check KISLEV 30 rules
			if (event.getMonth().equals(HebrewMonth.KISLEV) && event.getDay() == 30) {
				// insert Adar II date
				event.setMonth(HebrewMonth.TEVETH);
				event.setDay(1);
				event.setNotIf(HebrewMonth.KISLEV.toString() + ",30");
				dataAdapter.insertCustomEvent(event);
			}

			BackupDataUtils backaupUtils = new BackupDataUtils(context);
			backaupUtils.requestBackup();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}

	}

	/**
	 * insert new custom yahrzeit event
	 * 
	 * @param context
	 * @param event
	 */
	public static void insertYahrzeitEvent(Context context, HebrewCalendarEvent event) {

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		try {

			dataAdapter.open();

			// insert original occurrence of the event
			dataAdapter.insertCustomEvent(event);

			// advance start year to the next year
			event.setStart_year(event.getStart_year() + 1);

			// check for non leap year ADAR
			if (event.getMonth().equals(HebrewMonth.ADAR)) {

				event.setNotIf(HebrewMonth.ADAR.toString() + "," + event.getDay());

				// insert Adar I date
				event.setMonth(HebrewMonth.ADAR_I);
				dataAdapter.insertCustomEvent(event);

				// insert Adar II date
				event.setMonth(HebrewMonth.ADAR_II);
				dataAdapter.insertCustomEvent(event);
			}
			// check for leap year ADAR I and ADAR II
			else if (event.getMonth().equals(HebrewMonth.ADAR_I) || event.getMonth().equals(HebrewMonth.ADAR_II)) {

				if (event.getDay() == 30) {
					// insert SHEVAT 30 that is first day of Rosh Hodesh Adar
					event.setMonth(HebrewMonth.SHEVAT);
					event.setDay(30);
					event.setNotIf(HebrewMonth.ADAR_I.toString() + ",30");
					dataAdapter.insertCustomEvent(event);
				} else {
					// insert Adar date
					event.setNotIf(event.getMonth() + "," + event.getDay());
					event.setMonth(HebrewMonth.ADAR);
					dataAdapter.insertCustomEvent(event);
				}
			}

			// check for CHESHVAN 30
			if (event.getMonth().equals(HebrewMonth.CHESHVAN) && event.getDay() == 30) {
				// insert CHESHVAN 29 date
				event.setDay(29);
				event.setNotIf(HebrewMonth.CHESHVAN.toString() + ",30");
				dataAdapter.insertCustomEvent(event);
			}
			// check for KISLEV 30
			if (event.getMonth().equals(HebrewMonth.KISLEV) && event.getDay() == 30) {
				// insert CHESHVAN 29 date
				event.setDay(29);
				event.setNotIf(HebrewMonth.KISLEV.toString() + ",30");
				dataAdapter.insertCustomEvent(event);
			}

			BackupDataUtils backaupUtils = new BackupDataUtils(context);
			backaupUtils.requestBackup();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}
	}

	/**
	 * Deletes custom event from the database
	 * 
	 * @param context
	 * @param id
	 */
	public static void deleteCustomEvent(Context context, int id) {

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		try {
			dataAdapter.open();
			dataAdapter.deleteCustomEvent(id);

			BackupDataUtils backaupUtils = new BackupDataUtils(context);
			backaupUtils.requestBackup();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}

	}

	/**
	 * Update custom event in the database
	 * 
	 * @param context
	 * @param id
	 * @param eventDescription
	 */
	public static void updateCustomEvent(Context context, HebrewCalendarEvent hEvent) {

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		try {
			dataAdapter.open();
			dataAdapter.updateCustomEvent(hEvent);

			BackupDataUtils backaupUtils = new BackupDataUtils(context);
			backaupUtils.requestBackup();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}

	}

	public static Map<String, List<HebrewCalendarEvent>> getAllEvents(Context context) {

		HebrewCalendarEvent omerEvent = null;

		HebrewCalendarDatabaseAdapter dataAdapter = new HebrewCalendarDatabaseAdapter(context);

		Map<String, List<HebrewCalendarEvent>> events = new HashMap<String, List<HebrewCalendarEvent>>();

		try {

			dataAdapter.open();
			List<HebrewCalendarEvent> allEvents = dataAdapter.getAllEvents();

			for (HebrewCalendarEvent event : allEvents) {

				// find and save omer event
				if (event.getEvent().equals(HebrewEvent.OMER)) {
					omerEvent = event;
				}

				String key = event.getMonth().toString() + "," + event.getDay();
				List<HebrewCalendarEvent> localEvents = events.get(key);

				if (localEvents == null) {
					localEvents = new ArrayList<HebrewCalendarEvent>();
					events.put(key, localEvents);
				}
				localEvents.add(event);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dataAdapter.close();
		}

		events = addOmerEvents(omerEvent, events, (String) context.getText(R.string.days));

		return events;
	}

	private static Map<String, List<HebrewCalendarEvent>> addOmerEvents(HebrewCalendarEvent omerEvent, Map<String, List<HebrewCalendarEvent>> events,
			String days) {

		HebrewDate nexthDate = null;
		try {
			nexthDate = new HebrewDate(5771, omerEvent.getMonth(), omerEvent.getDay());
		} catch (HebrewDateException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 48; i++) {

			// create new event
			HebrewCalendarEvent newOmerEvent = new HebrewCalendarEvent();

			newOmerEvent.setEvent(omerEvent.getEvent());
			newOmerEvent.setImage(omerEvent.getImage());
			newOmerEvent.setNotIf(omerEvent.getNotIf());
			newOmerEvent.setOnlyIf(omerEvent.getOnlyIf());
			newOmerEvent.setStart_year(omerEvent.getStart_year());
			newOmerEvent.setDescription(omerEvent.getEvent().getEventName() + " " + (i + 2) + " " + days);

			nexthDate = DateConverter.getNextHebrewDate(nexthDate);

			newOmerEvent.setDay(nexthDate.getDay());
			newOmerEvent.setMonth(nexthDate.getHebrewMonth());

			// insert new event
			String omerKey = newOmerEvent.getMonth() + "," + newOmerEvent.getDay();
			List<HebrewCalendarEvent> omerEvents = events.get(omerKey);
			if (omerEvents == null) {
				omerEvents = new ArrayList<HebrewCalendarEvent>();
				events.put(omerKey, omerEvents);
			}
			omerEvents.add(newOmerEvent);
		}

		return events;
	}

	public static List<HebrewCalendarEvent> applyEventRules(List<HebrewCalendarEvent> events, int hYear) {

		// make copy of the event list
		List<HebrewCalendarEvent> validEvents = new ArrayList<HebrewCalendarEvent>(events);
		Collections.copy(validEvents, events);

		for (HebrewCalendarEvent hEvent : events) {

			// check for only if rule (keep event only if "only if" rule applies
			String key = hEvent.getOnlyIf();
			if (key != null && !"".equals(key)) {

				String[] hMonthDay = key.split(",");
				if (!HebrewDate.hebrewDateValid(hYear, HebrewMonth.valueOf(hMonthDay[0]), Integer.parseInt(hMonthDay[1]))) {
					validEvents.remove(hEvent);
				}
			}

			// check for not if rule (keep event only if "not if" rule applies
			key = hEvent.getNotIf();
			if (key != null && !"".equals(key)) {

				String[] hMonDay = key.split(",");
				if (HebrewDate.hebrewDateValid(hYear, HebrewMonth.valueOf(hMonDay[0]), Integer.parseInt(hMonDay[1]))) {
					validEvents.remove(hEvent);
				}
			}

			// check for starting hebrew year
			if (hEvent.getStart_year() > hYear) {
				validEvents.remove(hEvent);
			}
		}

		return validEvents;
	}

	public static boolean hasShabbos(List<HebrewCalendarEvent> events) {

		boolean hasShabbos = false;

		for (HebrewCalendarEvent hebrewCalendarEvent : events) {
			if (hebrewCalendarEvent.isShabbos()) {
				hasShabbos = true;
				break;
			}
		}

		return hasShabbos;
	}

	public static List<HebrewCalendarEvent> generateDefaultEvents(Context context) {

		ArrayList<HebrewCalendarEvent> hebrewEvents = new ArrayList<HebrewCalendarEvent>();

		XmlResourceParser parser = context.getResources().getXml(R.xml.hebrew_events);

		HebrewEvent event = null;
		HebrewMonth month = null;
		int day = 0;
		String image = null;
		boolean shabbos = false;
		int start_year = 0;
		String onlyIf = "";
		String notIf = "";
		String description = "";

		HebrewCalendarEvent calEvent = null;

		try {

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {

					String name = parser.getName();

					if (name != null && "event".equals(name)) {

						event = HebrewEvent.valueOf(parser.getAttributeValue(null, "name"));
						month = HebrewMonth.valueOf(parser.getAttributeValue(null, "month"));
						day = Integer.parseInt(parser.getAttributeValue(null, "day"));
						image = parser.getAttributeValue(null, "image");
						// TODO fix boolean
						shabbos = parser.getAttributeBooleanValue(null, "shabbos", false);
						start_year = Integer.parseInt(parser.getAttributeValue(null, "start_year"));
						onlyIf = parser.getAttributeValue(null, "onlyif");
						notIf = parser.getAttributeValue(null, "notif");
						description = parser.getAttributeValue(null, "description");

						int descNumb = context.getResources().getIdentifier(description, "string", context.getPackageName());
						String eventDesc = context.getString(descNumb);

						calEvent = new HebrewCalendarEvent(event, month, day, image, shabbos, start_year, onlyIf, notIf, eventDesc);
						hebrewEvents.add(calEvent);
					}
				}

				eventType = parser.next();

			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			parser.close();
		}

		return hebrewEvents;
	}

}
