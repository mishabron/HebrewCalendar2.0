package com.mbronshteyn.calendar.hebrew.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TestFormat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DateFormat candlesTime = new SimpleDateFormat("HH:mm");

		Calendar calendar = new GregorianCalendar();

		calendar.set(2017, 3, 10);

		Date date = calendar.getTime();

		System.out.println(date);

	}

}
