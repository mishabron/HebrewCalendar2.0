// Source File Name:   HebrewDate.java

package com.mbronshteyn.calendar.hebrew;

import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;
import com.mbronshteyn.calendar.hebrew.exceptions.HebrewDateException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Referenced classes of package com.mbronshteyn.calendar.hebrew:
//            CalendarUtils

/**
 * @author misha
 */
public class HebrewDate {

	/**
	 * @uml.property name="day"
	 */
	private int day;
	/**
	 * @uml.property name="month"
	 */
	private int month;
	/**
	 * @uml.property name="year"
	 */
	private int year;
	/**
	 * @uml.property name="monthName"
	 * @uml.associationEnd
	 */
	private HebrewMonth monthName;
	/**
	 * @uml.property name="hebrewEvent"
	 */
	private String hebrewEvent;
	/**
	 * @uml.property name="events"
	 */
	private List<HebrewEvent> events;

	private boolean yomTov = false;

	private boolean beforeYomTov = false;

	private boolean beforeShabbos = false;

	private Date shabbosStart;

	private Date shabbosEnd;
	private boolean dayAftrShabbos = false;
	private boolean dayAfterYomTov = false;;

	public HebrewDate() {
	}

	public HebrewDate(int year, int month, int day) throws HebrewDateException {

		// validate hebrew date
		int maxdays = CalendarUtils.hebrewDaysInMonth(month, year);
		if (day > maxdays) {
			throw new HebrewDateException();
		}

		this.day = day;
		this.month = month;
		this.year = year;
		this.monthName = CalendarUtils.hebrewMonths[month - 1];
	}

	public HebrewDate(int year, HebrewMonth monthName, int day) throws HebrewDateException {

		if (!hebrewDateValid(year, monthName, day)) {
			throw new HebrewDateException();
		}

		this.day = day;
		this.month = CalendarUtils.getMonthNumber(monthName);
		this.year = year;
		this.monthName = monthName;
	}

	/**
	 * @return
	 * @uml.property name="day"
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param day
	 * @uml.property name="day"
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return
	 * @uml.property name="month"
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month
	 * @uml.property name="month"
	 */
	public void setMonth(int month) {
		this.month = month;
		this.monthName = CalendarUtils.hebrewMonths[month - 1];
	}

	public HebrewMonth getHebrewMonth() {
		return monthName;
	}

	public void setHebrewMonth(HebrewMonth monthName) {
		this.monthName = monthName;
		this.month = CalendarUtils.getMonthNumber(monthName);
	}

	/**
	 * @return
	 * @uml.property name="year"
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year
	 * @uml.property name="year"
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return
	 * @uml.property name="hebrewEvent"
	 */
	public String getHebrewEvent() {
		return hebrewEvent;
	}

	/**
	 * @param hebrewEvent
	 * @uml.property name="hebrewEvent"
	 */
	public void setHebrewEvent(String hebrewEvent) {
		this.hebrewEvent = hebrewEvent;
	}

	/**
	 * @return
	 * @uml.property name="events"
	 */
	public List<HebrewEvent> getEvents() {
		return events;
	}

	/**
	 * @param events
	 * @uml.property name="events"
	 */
	public void setEvents(List<HebrewEvent> events) {
		this.events = events;
	}

	public void addEvent(HebrewEvent event) {
		events.add(event);
	}

	public HebrewMonth getMonthName() {
		return monthName;
	}

	public void setMonthName(HebrewMonth monthName) {
		this.monthName = monthName;
	}

	public boolean isYomTov() {
		return yomTov;
	}

	public void setYomTov(boolean dayOff) {
		this.yomTov = dayOff;
	}

	public boolean isBeforeYomTov() {

		return beforeYomTov;
	}

	public void setBeforeYomTov(boolean beforeDayOff) {
		this.beforeYomTov = beforeDayOff;
	}

	public Date getShabbosStart() {
		return shabbosStart;
	}

	public void setShabbosStart(Date shabbosStart) {
		this.shabbosStart = shabbosStart;
	}

	public Date getShabbosEnd() {
		return shabbosEnd;
	}

	public void setShabbosEnd(Date shabbosEnd) {
		this.shabbosEnd = shabbosEnd;
	}

	public boolean isBeforeShabbos() {
		return beforeShabbos;
	}

	public void setBeforeShabbos(boolean beforeShabbos) {
		this.beforeShabbos = beforeShabbos;
	}

	public static boolean hebrewDateValid(int year, HebrewMonth monthName, int day) {

		boolean retValue = true;
		// validate hebrew month in year
		List<HebrewMonth> hMonths = CalendarUtils.hebrewMonthsInYear(year);
		if (!hMonths.contains(monthName)) {
			retValue = false;
		}

		// validate hebrew day in the month
		int maxdays = CalendarUtils.hebrewDaysInMonth(CalendarUtils.getMonthNumber(monthName), year);
		if (day > maxdays) {
			retValue = false;
		}

		return retValue;
	}

	public boolean isShabbos() {

		boolean isShabbos = false;

		Date grDate = DateConverter.getGregorianDate(this);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(grDate);

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			isShabbos = true;
		}

		return isShabbos;
	}
	public void setDayAfterShabbos(boolean dayAftrShabbos) {

		this.dayAftrShabbos = dayAftrShabbos;
	}

	public void setDayAfterYomTov(boolean dayAfterYomTov) {
		this.dayAfterYomTov = dayAfterYomTov;
	}

	public boolean isDayAfterYomTov() {
		return dayAfterYomTov;
	}

	public boolean isDayAfterShabbos() {
		return dayAftrShabbos;
	}
}
