// Source File Name:   CalendarUtils.java

package com.mbronshteyn.calendar.hebrew;

import com.kosherjava.zmanim.AstronomicalCalendar;
import com.kosherjava.zmanim.ZmanimCalendar;
import com.kosherjava.zmanim.util.GeoLocation;
import com.mbronshteyn.calendar.hebrew.data.GeoInfo;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;
import com.mbronshteyn.calendar.hebrew.exceptions.HebrewDateException;

import org.joda.time.DateTimeZone;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;

// Referenced classes of package com.mbronshteyn.calendar.hebrew:
//            HebrewDate

/**
 * @author misha
 */
public class CalendarUtils {

	public static final int GREG_BASE_YEAR = 1760;
	public static final int GRDEFAULT_START_YEAR = 1770;
	public static final int GEDEFAULT_END_YEAR = 2240;
	public static final int HEBR_BASE_YEAR = 5530;
	public static final int HEBR_LAST_YEAR = 6000;
	public static final int BASE_MOLAD_DAY = 3560;
	public static final int BASE_MOLAD_HOUR = 21;
	public static final int BASE_MOLAD_PART = 549;

	public static final int TISHREI = 1;
	public static final int CHESHVAN = 2;
	public static final int KISLEV = 3;
	public static final int TEVETH = 4;
	public static final int SHEVAT = 5;
	public static final int ADAR = 6;
	public static final int ADAR_I = 7;
	public static final int ADAR_II = 8;
	public static final int NISAN = 9;
	public static final int IYAR = 10;
	public static final int SIVAN = 11;
	public static final int TAMMUZ = 12;
	public static final int AV = 13;
	public static final int ELUL = 14;

	/**
	 * @uml.property name="hebrewMonths"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	public static final HebrewMonth hebrewMonths[] = HebrewMonth.values();
	/**
	 * @uml.property name="hebrewEvents"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	public static final HebrewEvent hebrewEvents[] = HebrewEvent.values();

	public static final int JANUARY = 1;
	public static final int FEBRUARY = 2;
	public static final int MARCH = 3;
	public static final int APRIL = 4;
	public static final int MAY = 5;
	public static final int JUNE = 6;
	public static final int JULY = 7;
	public static final int AUGUST = 8;
	public static final int SEPTEMBER = 9;
	public static final int OCTOBER = 10;
	public static final int NOVEMBER = 11;
	public static final int DECEMBER = 12;

	public static boolean hebrLeapYear(int year) {

		boolean leap = false;
		int rem = year % 19;

		if (rem == 0 || rem == 3 || rem == 6 || rem == 8 || rem == 11 || rem == 14 || rem == 17) {
			leap = true;
		}

		return leap;
	}

	public static boolean gregLeapYear(int year) {

		boolean leap = false;

		if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
			leap = true;
		}

		return leap;
	}

	public static int daysInGregorianYear(int year) {
		int days = 365;
		if (gregLeapYear(year))
			days = 366;
		return days;
	}

	public static int absoluteFromGgregorian(int month, int day, int year) {

		int greg_month_starts[] = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };
		int year_diffs = year - GREG_BASE_YEAR;
		int absolute_day = 365 * year_diffs;

		absolute_day += year_diffs / 4;
		absolute_day -= year / 100 - 17;
		absolute_day += year / 400 - 4;
		absolute_day += greg_month_starts[month - 1];
		absolute_day += day - 1;

		if (!gregLeapYear(year) && month <= 2) {
			absolute_day++;
		}

		return absolute_day;
	}

	public static int absoluteFromHebrew(int month, int day, int year) {

		int absolute_day = hebrewFindRosh(year);

		for (int hmonth = 1; hmonth < month; hmonth = hebrewNextMonth(hmonth, year)) {
			absolute_day += hebrewDaysInMonth(hmonth, year);
		}

		absolute_day += day - 1;
		return absolute_day;
	}

	public static Date gregorianFromAbsolute(int absolute_day) {

		int rough_year = absolute_day / 366;
		int days = 365 * rough_year;
		days += rough_year / 4;
		int year = rough_year + GREG_BASE_YEAR;
		if (!gregLeapYear(year))
			days++;
		days -= year / 100 - 17;
		days += year / 400 - 4;
		days = absolute_day - days;

		for (int ydays = daysInGregorianYear(year); days >= ydays; ydays = daysInGregorianYear(++year)) {
			days -= ydays;
		}

		int month = 1;
		for (int mdays = 0; days >= mdays;) {
			mdays = gregorianDaysInMonth(month, year);
			days -= mdays;
			month++;
		}

		int day = days + 1;
		Calendar cal = new GregorianCalendar(year, month - 1, day);
		return cal.getTime();
	}

	public static HebrewDate hebrewFromAbsolute(int absolute_day) {

		int days = absolute_day - BASE_MOLAD_DAY;
		int cycles = days / 6950;
		days -= cycles * 6950;
		int delta = 0;
		HebrewDate hdate = null;

		for (int loop = 1; days >= 0; loop++) {
			delta++;
			days -= hebrLeapYear(loop) ? 358 : 388;
		}

		int year = (HEBR_BASE_YEAR + 19 * cycles + delta) - 1;
		int year_date = hebrewFindRosh(year);
		days = absolute_day;

		for (int next_year_date = hebrewFindRosh(year + 1); days >= next_year_date; next_year_date = hebrewFindRosh(year + 1)) {
			year++;
			year_date = next_year_date;
		}

		days = absolute_day - year_date;
		int month = 1;

		for (delta = hebrewDaysInMonth(month, year); days >= delta; delta = hebrewDaysInMonth(month, year)) {
			days -= delta;
			month = hebrewNextMonth(month, year);
		}

		int day = days + 1;

		try {
			hdate = new HebrewDate(year, month, day);
		} catch (HebrewDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hdate;
	}

	public static int hebrewFindRosh(int hebrew_year) {

		int cycles = (hebrew_year - HEBR_BASE_YEAR) / 19;
		int cycle_year = hebrew_year % 19;
		if (cycle_year == 0)
			cycle_year = 19;
		int extra_months = 0;
		for (int year = 1; year < cycle_year; year++)
			extra_months += hebrLeapYear(year) ? 13 : 12;

		int molad_day = BASE_MOLAD_DAY + 6939 * cycles + 29 * extra_months;
		int molad_hour = BASE_MOLAD_HOUR + 16 * cycles + 12 * extra_months;
		int molad_part = BASE_MOLAD_PART + 595 * cycles + 793 * extra_months;
		molad_hour += molad_part / 1080;
		molad_part %= 1080;
		molad_day += molad_hour / 24;
		molad_hour %= 24;
		int weekday = (molad_day + 2) % 7;
		if (weekday == 0 || weekday == 3 || weekday == 5)
			molad_day++;
		else if (molad_hour >= 18) {
			if (weekday == 6 || weekday == 2 || weekday == 4)
				molad_day += 2;
			else
				molad_day++;
		} else if (!hebrLeapYear(hebrew_year) && weekday == 2 && (molad_hour > 9 || molad_hour == 9 && molad_part >= 204))
			molad_day += 2;
		else if (hebrLeapYear(hebrew_year - 1) && !hebrLeapYear(hebrew_year) && weekday == 1 && (molad_hour > 15 || molad_hour == 15 && molad_part >= 589))
			molad_day++;

		return molad_day;
	}

	public static HebrewMonth hebrewNextMonth(HebrewMonth hMonth, int year) {

		int month = getMonthNumber(hMonth);
		int nextMonth = hebrewNextMonth(month, year);

		return hebrewMonths[nextMonth - 1];

	}

	public static int hebrewNextMonth(int month, int year) {

		int nextMonth = 0;

		switch (month) {
		default:
			break;

		case TISHREI: // '\001'
			nextMonth = CHESHVAN;
			break;

		case CHESHVAN: // '\002'
			nextMonth = KISLEV;
			break;

		case KISLEV: // '\003'
			nextMonth = TEVETH;
			break;

		case TEVETH: // '\004'
			nextMonth = SHEVAT;
			break;

		case SHEVAT: // '\005'
			if (hebrLeapYear(year))
				nextMonth = ADAR_I;
			else
				nextMonth = ADAR;
			break;

		case ADAR: // '\006'
			nextMonth = NISAN;
			break;

		case ADAR_I: // '\007'
			nextMonth = ADAR_II;
			break;

		case ADAR_II: // '\b'
			nextMonth = NISAN;
			break;

		case NISAN: // '\t'
			nextMonth = IYAR;
			break;

		case IYAR: // '\n'
			nextMonth = SIVAN;
			break;

		case SIVAN: // '\013'
			nextMonth = TAMMUZ;
			break;

		case TAMMUZ: // '\f'
			nextMonth = AV;
			break;

		case AV: // '\r'
			nextMonth = ELUL;
			break;

		case ELUL: // '\016'
			nextMonth = TISHREI;
			break;
		}

		return nextMonth;
	}

	public static int hebrewDaysInMonth(HebrewMonth hMonth, int year) {

		int month = getMonthNumber(hMonth);

		return hebrewDaysInMonth(month, year);
	}

	public static int hebrewDaysInMonth(int month, int year) {

		int numberOfDays = 0;
		int hebrew_year_length = hebrewFindRosh(year + 1) - hebrewFindRosh(year);

		switch (month) {
		default:
			break;

		case TISHREI: // '\001'
			numberOfDays = 30;
			break;

		case SHEVAT: // '\005'
			numberOfDays = 30;
			break;

		case ADAR_I: // '\007'
			numberOfDays = 30;
			break;

		case NISAN: // '\t'
			numberOfDays = 30;
			break;

		case SIVAN: // '\013'
			numberOfDays = 30;
			break;

		case AV: // '\r'
			numberOfDays = 30;
			break;

		case TEVETH: // '\004'
			numberOfDays = 29;
			break;

		case ADAR_II: // '\b'
			numberOfDays = 29;
			break;

		case IYAR: // '\n'
			numberOfDays = 29;
			break;

		case TAMMUZ: // '\f'
			numberOfDays = 29;
			break;

		case ELUL: // '\016'
			numberOfDays = 29;
			break;

		case ADAR: // '\006'
			if (hebrew_year_length > 360)
				numberOfDays = 30;
			else
				numberOfDays = 29;
			break;

		case CHESHVAN: // '\002'
			if (hebrew_year_length == 355 || hebrew_year_length == 385)
				numberOfDays = 30;
			else
				numberOfDays = 29;
			break;

		case KISLEV: // '\003'
			if (hebrew_year_length == 353 || hebrew_year_length == 383)
				numberOfDays = 29;
			else
				numberOfDays = 30;
			break;
		}
		return numberOfDays;
	}

	public static int gregorianDaysInMonth(int month, int year) {
		int numberOfDays = 0;
		switch (month) {
		default:
			break;

		case JANUARY: // '\001'
			numberOfDays = 31;
			break;

		case MARCH: // '\003'
			numberOfDays = 31;
			break;

		case MAY: // '\005'
			numberOfDays = 31;
			break;

		case AUGUST: // '\b'
			numberOfDays = 31;
			break;

		case OCTOBER: // '\n'
			numberOfDays = 31;
			break;

		case DECEMBER: // '\f'
			numberOfDays = 31;
			break;

		case APRIL: // '\004'
			numberOfDays = 30;
			break;

		case JUNE: // '\006'
			numberOfDays = 30;
			break;

		case JULY: // '\007'
			numberOfDays = 31;
			break;

		case SEPTEMBER: // '\t'
			numberOfDays = 30;
			break;

		case NOVEMBER: // '\013'
			numberOfDays = 30;
			break;

		case FEBRUARY: // '\002'
			if (gregLeapYear(year))
				numberOfDays = 29;
			else
				numberOfDays = 28;
			break;
		}
		return numberOfDays;
	}

	public static List<HebrewMonth> hebrewMonthsInYear(int year) {

		ArrayList<HebrewMonth> months = new ArrayList<HebrewMonth>();

		for (int nextMonth = 1; nextMonth <= 14;) {
			HebrewMonth monthName = hebrewMonths[nextMonth - 1];
			months.add(monthName);
			if (nextMonth != 14)
				nextMonth = hebrewNextMonth(nextMonth, year);
			else
				nextMonth++;
		}

		return months;
	}

	public static int getMonthNumber(HebrewMonth monthName) {

		int month = 0;

		for (int i = 0; i < CalendarUtils.hebrewMonths.length && month == 0; i++) {
			if (CalendarUtils.hebrewMonths[i].equals(monthName)) {
				month = i + 1;
			}
		}

		return month;
	}

	public static Date getShabbosStart(String locationName, Date time, GeoInfo geoInfo, DateTimeZone dynaZone) {

		Date shabbosStart = null;
		double elevation = 0;

		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(time);

		SimpleTimeZone timeZone = new SimpleTimeZone(dynaZone.getOffset(dateCal.getTimeInMillis()), geoInfo.getTimeZone());

		// create the location object
		GeoLocation location = new GeoLocation(locationName, geoInfo.getLatitude(), geoInfo.getLongitude(), elevation, timeZone);
		// create the ZmanimCalendar
		ZmanimCalendar zc = new ZmanimCalendar(location);
		zc.setCalendar(dateCal);
		shabbosStart = zc.getSunset();

		if(shabbosStart !=null){
			Calendar cals = Calendar.getInstance(timeZone);// time zone
			cals.setTime(shabbosStart);
			cals.add(Calendar.MINUTE, -geoInfo.getBeforeSunset());
			shabbosStart = cals.getTime();
			LocalDateTime ldt = LocalDateTime.ofInstant(shabbosStart.toInstant(),ZoneId.systemDefault());
		}

		return shabbosStart;
	}
	public static Date getShabbosEnd(String locationName, Date time, GeoInfo geoInfo, DateTimeZone dynaZone) {

		Date shabbosEnd = null;
		double elevation = 0;

		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(time);

		SimpleTimeZone timeZone = new SimpleTimeZone(dynaZone.getOffset(dateCal.getTimeInMillis()), geoInfo.getTimeZone());

		// create the location object
		GeoLocation location = new GeoLocation(locationName, geoInfo.getLatitude(), geoInfo.getLongitude(), elevation, timeZone);
		// create the ZmanimCalendar
		ZmanimCalendar zc = new ZmanimCalendar(location);
		zc.setCalendar(dateCal);
		shabbosEnd = zc.getTzais();

		if(shabbosEnd ==  null){
			AstronomicalCalendar ac = new AstronomicalCalendar(location);
			ac.setCalendar(dateCal);
			shabbosEnd = ac.getSolarMidnight();
		}

		return shabbosEnd;
	}
}
