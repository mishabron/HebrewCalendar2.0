package com.mbronshteyn.android.calendar.hebrew.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.dialog.AddCustomEventDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.GRDatePicker;
import com.mbronshteyn.android.calendar.hebrew.dialog.HewbrewDatePicker;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.android.calendar.hebrew.util.MenuHelper;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

/**
 * @author misha
 */
public class DateConversionFragment extends Fragment {

	private TextView bToday;
	private Button hbPickDate;
	private Button grPickDate;
	private Button thisYearHb;
	private Button addDialog;

	private Date grDate;
	/**
	 * @uml.property name="hDate"
	 * @uml.associationEnd
	 */
	private HebrewDate hDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Calendar calendar;

	private static final int DATE_DIALOG_ID = 0;
	private static final int HDATE_DIALOG_ID = 1;
	private static final int ADDEVENT_DIALOG_ID = 2;

	private HewbrewDatePicker hewbrewDatePicker;
	private MenuHelper menuHelper;

	private SimpleDateFormat df = new SimpleDateFormat("EEE, MMMM d, yyyy");

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity().getBaseContext();

		// load icons
		if (HebrewCalendarUtils.icons == null) {
			HebrewCalendarUtils.icons = HebrewCalendarUtils.loadIcons(getResources());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.date_converter, container, false);

		// capture all View elements
		grPickDate = (Button) view.findViewById(R.id.ButtonGr);
		hbPickDate = (Button) view.findViewById(R.id.ButtonHb);
		bToday = (Button) view.findViewById(R.id.ButtonToday);
		thisYearHb = (Button) view.findViewById(R.id.ButtonThisYearHb);
		addDialog = (Button) view.findViewById(R.id.addEvent_button);

		// add a click listener to the button
		addDialog.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(ADDEVENT_DIALOG_ID);
			}
		});

		// add a click listener to the button
		grPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		// add a click listener to the button
		hbPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(HDATE_DIALOG_ID);
			}
		});

		// add a click listener to the button
		bToday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setToday();
				updateDisplay();
			}
		});

		// add a click listener to the buttons
		thisYearHb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setThisHbYear();
				updateDisplay();
			}
		});

		// get today dates
		setToday();

		updateDisplay();

		return view;
	}

	private void updateDisplay() {

		grPickDate.setText(df.format(grDate));

		StringBuilder sb = new StringBuilder();
		String monthName = hDate.getHebrewMonth().getMonthName();

		sb.append(monthName).append(" ").append(hDate.getDay()).append(", ").append(hDate.getYear());

		hbPickDate.setText(sb.toString());

	}

	// the callback received when the user "sets" the date in the dialog
	/**
	 * @uml.property name="mDateSetListener"
	 * @uml.associationEnd
	 */
	private GRDatePicker.OnDateSetListener mDateSetListener = new GRDatePicker.OnDateSetListener() {

		public void onDateSet(int year, int monthOfYear, int dayOfMonth, String dayNight) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			calendar.set(mYear, mMonth, mDay);
			grDate = calendar.getTime();
			hDate = DateConverter.getHebrewDate(grDate);

			if (context.getText(R.string.nightTime).equals(dayNight)) {
				hDate = DateConverter.getNextHebrewDate(hDate);
			}

			updateDisplay();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	/**
	 * @uml.property name="hDateSetListener"
	 * @uml.associationEnd
	 */
	private HewbrewDatePicker.OnDateSetListener hDateSetListener = new HewbrewDatePicker.OnDateSetListener() {

		public void onDateSet(int year, HebrewMonth monthOfYear, int dayOfMonth) {

			hDate.setYear(year);
			hDate.setHebrewMonth(monthOfYear);
			hDate.setDay(dayOfMonth);

			grDate = DateConverter.getGregorianDate(hDate);

			calendar.setTime(grDate);

			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);

			updateDisplay();
		}
	};

	protected void showDialog(int dialogId) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(String.valueOf(dialogId));

		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		switch (dialogId) {
		case ADDEVENT_DIALOG_ID:
			Date calendarDate = DateConverter.getGregorianDate(hDate);
			AddCustomEventDialog addDialog = new AddCustomEventDialog(getActivity());
			addDialog.updateDate(hDate, calendarDate);
			addDialog.show();
			break;
		case HDATE_DIALOG_ID:
			HewbrewDatePicker hDateDialog = new HewbrewDatePicker(getActivity(), hDateSetListener, hDate);
			hDateDialog.show();
			break;
		case DATE_DIALOG_ID:
			GRDatePicker grDialog = new GRDatePicker(getActivity(), mDateSetListener, mYear, mMonth, mDay);
			grDialog.show();
			break;
		}

	}

	private void setToday() {

		// get today dates
		calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);

		grDate = calendar.getTime();
		hDate = DateConverter.getHebrewDate(grDate);
	}

	private void setThisHbYear() {

		calendar = Calendar.getInstance();
		int thisGrYear = calendar.get(Calendar.YEAR);

		hDate = DateConverter.getHebrewDateForGrYear(thisGrYear, hDate);
		grDate = DateConverter.getGregorianDate(hDate);

	}

	/**
	 * Getter of the property <tt>hewbrewDatePicker</tt>
	 * 
	 * @return Returns the hewbrewDatePicker.
	 * @uml.property name="hewbrewDatePicker"
	 */
	public HewbrewDatePicker getHewbrewDatePicker() {
		return hewbrewDatePicker;
	}

	/**
	 * Setter of the property <tt>hewbrewDatePicker</tt>
	 * 
	 * @param hewbrewDatePicker
	 *            The hewbrewDatePicker to set.
	 * @uml.property name="hewbrewDatePicker"
	 */
	public void setHewbrewDatePicker(HewbrewDatePicker hewbrewDatePicker) {
		this.hewbrewDatePicker = hewbrewDatePicker;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menuHelper = new MenuHelper(getActivity());
		MenuHelper.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menuHelper.onOptionsItemSelected(item);

		return false;
	}
}
