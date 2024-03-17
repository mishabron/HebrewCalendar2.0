package com.mbronshteyn.android.calendar.hebrew.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class CandleTimeFormatter {

	private Context context;
	private String pattern;

	public CandleTimeFormatter(Context context, String pattern) {

		this.context = context;
		this.pattern = pattern;
	}

	public String format(Date date) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		String timeZone = preferences.getString("timeZone", TimeZone.getDefault().getID());

		DateTimeZone dynaZone = DateTimeZone.forID(timeZone);

		SimpleTimeZone simpleTimeZone = new SimpleTimeZone(dynaZone.getOffset(date.getTime()), timeZone);

		DateFormat candlesTime = new SimpleDateFormat(pattern);
		candlesTime.setTimeZone(simpleTimeZone);

		return candlesTime.format(date.getTime());

	}

}
