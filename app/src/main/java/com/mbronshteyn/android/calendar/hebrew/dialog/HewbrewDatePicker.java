package com.mbronshteyn.android.calendar.hebrew.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.fragment.DateConversionFragment;
import com.mbronshteyn.android.custom.ui.NumberPicker;
import com.mbronshteyn.android.custom.ui.NumberPicker.OnChangedListener;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

/**
 * @author misha
 */
public class HewbrewDatePicker extends AlertDialog implements OnClickListener {

	/**
	 * @uml.property name="hDate"
	 * @uml.associationEnd
	 */
	private HebrewDate hDate;
	/**
	 * @uml.property name="yearPicker"
	 * @uml.associationEnd
	 */
	private NumberPicker yearPicker;
	/**
	 * @uml.property name="monthPicker"
	 * @uml.associationEnd
	 */
	private NumberPicker monthPicker;
	/**
	 * @uml.property name="dayPicker"
	 * @uml.associationEnd
	 */
	private NumberPicker dayPicker;
	private List<HebrewMonth> monthList;
	private Resources resources;
	/**
	 * @uml.property name="mCallBack"
	 * @uml.associationEnd
	 */
	private final OnDateSetListener mCallBack;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateSet(int hYyear, HebrewMonth hMonth, int hDay);

	}

	public HewbrewDatePicker(Context context, OnDateSetListener callBack, final HebrewDate hDate) {
		super(context);
		// TODO Auto-generated constructor stub

		this.hDate = hDate;
		this.mCallBack = callBack;

		resources = context.getResources();

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.hebrew_datepicker, null);
		setView(view);
		setTitle(context.getText(R.string.hDatePicker_title));

		setButton(context.getText(R.string.hDatePicker_Set_Button), this);
		setButton2(context.getText(R.string.hDatePicker_Cancel_Button), (OnClickListener) null);

		yearPicker = (NumberPicker) view.findViewById(R.id.hyear);
		monthPicker = (NumberPicker) view.findViewById(R.id.hmonth);
		dayPicker = (NumberPicker) view.findViewById(R.id.hday);

		TextView mText = (TextView) monthPicker.findViewById(R.id.timepicker_input);
		// mText.setTextSize(resources.getDimension(R.dimen.hMonth));

		TextView dText = (TextView) dayPicker.findViewById(R.id.timepicker_input);
		// dText.setTextSize(resources.getDimension(R.dimen.hDay));

		TextView yText = (TextView) yearPicker.findViewById(R.id.timepicker_input);
		// yText.setTextSize(resources.getDimension(R.dimen.hYear));

		yearPicker.setOnChangeListener(new OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				initMonthPicker(newVal);
				initDayPicker(hDate.getHebrewMonth(), newVal);
			}
		});

		monthPicker.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		monthPicker.setOnChangeListener(new OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				HebrewMonth hm = monthList.get(newVal - 1);
				initDayPicker(hm, yearPicker.getCurrent());
			}
		});

		setDatePicker();
	}

	public void updateDate(HebrewDate hDate) {

		this.hDate = hDate;
		setDatePicker();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("Pick Hebrew Date");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mCallBack != null) {
			yearPicker.clearFocus();
			monthPicker.clearFocus();
			dayPicker.clearFocus();
			mCallBack.onDateSet(yearPicker.getCurrent(), monthList.get(monthPicker.getCurrent() - 1), dayPicker.getCurrent());
		}

	}

	private void initYearPicker(int hYear) {

		yearPicker.setRange(CalendarUtils.HEBR_BASE_YEAR, CalendarUtils.HEBR_LAST_YEAR);
		yearPicker.setCurrent(hYear);

	}

	private void initMonthPicker(int hYear) {

		monthList = CalendarUtils.hebrewMonthsInYear(hYear);

		String[] hMonths = new String[monthList.size()];

		// get month strings
		for (int i = 0; i < hMonths.length; i++) {
			hMonths[i] = monthList.get(i).getMonthName();
		}

		monthPicker.setRange(1, hMonths.length, hMonths);
	}

	private void initDayPicker(HebrewMonth hm, int year) {

		int days = CalendarUtils.hebrewDaysInMonth(hm, year);

		dayPicker.setRange(1, days);
		dayPicker.setCurrent(1);
	}

	private void setDatePicker() {

		HebrewMonth hm = null;

		initYearPicker(this.hDate.getYear());
		initMonthPicker(this.hDate.getYear());

		// set current month
		for (int i = 0; i < monthList.size(); i++) {
			if (monthList.get(i).equals(hDate.getHebrewMonth())) {
				monthPicker.setCurrent(i + 1);
				hm = hDate.getHebrewMonth();
			}
		}

		initDayPicker(hm, this.hDate.getYear());
		dayPicker.setCurrent(this.hDate.getDay());
	}

	/**
	 * @uml.property name="dateConversionActivity"
	 * @uml.associationEnd inverse=
	 *                     "hewbrewDatePicker:com.mbronshteyn.android.calendar.hebrew.activity.DateConversionActivity"
	 */
	private DateConversionFragment dateConversionActivity;

	/**
	 * Getter of the property <tt>dateConversionActivity</tt>
	 * 
	 * @return Returns the dateConversionActivity.
	 * @uml.property name="dateConversionActivity"
	 */
	public DateConversionFragment getDateConversionActivity() {
		return dateConversionActivity;
	}

	/**
	 * Setter of the property <tt>dateConversionActivity</tt>
	 * 
	 * @param dateConversionActivity
	 *            The dateConversionActivity to set.
	 * @uml.property name="dateConversionActivity"
	 */
	public void setDateConversionActivity(DateConversionFragment dateConversionActivity) {
		this.dateConversionActivity = dateConversionActivity;
	}
}
