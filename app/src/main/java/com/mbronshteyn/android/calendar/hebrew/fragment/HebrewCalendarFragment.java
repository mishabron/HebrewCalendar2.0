package com.mbronshteyn.android.calendar.hebrew.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarAdapter;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.android.calendar.hebrew.dialog.AddCustomEventDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.CalendarDayDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateEventDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateEventDialog.OnEventUpdateListener;
import com.mbronshteyn.android.calendar.hebrew.util.CandleTimeFormatter;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.android.calendar.hebrew.util.MenuHelper;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.GeoInfo;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class HebrewCalendarFragment extends Fragment {

	private Context context;

	private TableLayout calendarTable;
	private View[] dayCells = new View[42];
	private static Calendar calendar = Calendar.getInstance();
	private static Date today = new Date();
	private Map<String, List<HebrewCalendarEvent>> events;

	private final int EMPTY_CELL = R.drawable.empty_calendaritem_gradient;
	private final int SHABBOS_CELL = R.drawable.shabbos_cell;
	private final int CALENDAR_CELL = R.drawable.calendar_cell;

	private static final int CELL_DIALOG_ID = 0;
	private static final int ADDEVENT_DIALOG_ID = 1;
	private static final int EDITEVENT_DIALOG_ID = 2;

	private HebrewDate hDate;
	private HebrewCalendarEvent hEvent;

	private SimpleDateFormat titleFormat = new SimpleDateFormat("MMMM yyyy");
	private SimpleDateFormat cellTitleFormat = new SimpleDateFormat("EEE, MMM d yyyy");
	private CandleTimeFormatter candlesTime;

	private final int flingSpeed = 1000;

	private MenuHelper menuHelper;

	private String location;

	private CalendarDayDialog.OnButtonClickListener addEventListener = new CalendarDayDialog.OnButtonClickListener() {

		@Override
		public void onAddClick() {
			showDialog(ADDEVENT_DIALOG_ID);
		}

		@Override
		public void onEditClick(HebrewCalendarEvent dayEvent) {
			hEvent = dayEvent;
			showDialog(EDITEVENT_DIALOG_ID);

		}
	};

	AddCustomEventDialog.OnButtonClickListener saveEventListener = new AddCustomEventDialog.OnButtonClickListener() {

		@Override
		public void onClick() {
			refreshEvents();
		}

	};

	private OnEventUpdateListener callBack = new OnEventUpdateListener() {

		@Override
		public void onUpdate() {
			refreshEvents();
		}

	};

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (velocityX >= flingSpeed) {
				calendar.add(Calendar.MONTH, -1);
				updateDisplay();
			}
			if (velocityX <= -flingSpeed) {
				calendar.add(Calendar.MONTH, 1);
				updateDisplay();
			}
			if (velocityY >= flingSpeed) {
				calendar.add(Calendar.MONTH, -1);
				updateDisplay();
			}
			if (velocityY <= -flingSpeed) {
				calendar.add(Calendar.MONTH, 1);
				updateDisplay();
			}

			return false;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity().getBaseContext();

		candlesTime = new CandleTimeFormatter(context, "HH:mm");

		// load events
		events = HebrewCalendarAdapter.getAllEvents(context);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		// load icons
		if (HebrewCalendarUtils.icons == null) {
			HebrewCalendarUtils.icons = HebrewCalendarUtils.loadIcons(getResources());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.hebrew_calendar, container, false);

		// define calendar buttons
		Button prevYear = (Button) view.findViewById(R.id.prevYear);
		Button prevMonth = (Button) view.findViewById(R.id.prevMonth);
		Button todayDay = (Button) view.findViewById(R.id.today);
		Button nextMonth = (Button) view.findViewById(R.id.nextMonth);
		Button nextYear = (Button) view.findViewById(R.id.nextYear);

		// add a click listener to the button
		prevYear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calendar.add(Calendar.YEAR, -1);
				updateDisplay();
			}
		});

		// add a click listener to the button
		prevMonth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calendar.add(Calendar.MONTH, -1);
				updateDisplay();
			}
		});

		// add a click listener to the button
		todayDay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calendar = Calendar.getInstance();
				today = new Date();
				updateDisplay();
			}
		});

		// add a click listener to the button
		nextMonth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calendar.add(Calendar.MONTH, 1);
				updateDisplay();
			}
		});

		// add a click listener to the button
		nextYear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calendar.add(Calendar.YEAR, 1);
				updateDisplay();
			}
		});

		calendarTable = (TableLayout) view.findViewById(R.id.calendar);

		int index = 0;
		for (int row = 0; row < 6; row++) {

			View calrow = inflater.inflate(R.layout.calendar_row, calendarTable, false);
			TableRow trow = (TableRow) calrow.findViewById(R.id.trow);

			for (int column = 0; column < 7; column++) {

				try {
					final View dayCell = inflater.inflate(R.layout.calendar_cell, trow, false);
					dayCell.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							TextView grDay = (TextView) v.findViewById(R.id.gDay);
							if (!grDay.getText().equals("")) {
								hDate = (HebrewDate) dayCell.getTag();
								showDialog(CELL_DIALOG_ID);
							}
						}
					});

					dayCells[index] = dayCell;
					index++;

					trow.addView(dayCell);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			calendarTable.addView(trow);
		}

		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		updateDisplay();
	}

	public void updateDisplay() {

		// get saved GEO values
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		location = preferences.getString("address_location", "");

		GeoInfo geoInfo = new GeoInfo();
		geoInfo.setLatitude(Double.parseDouble("".equals(preferences.getString("latitude", "0")) ? "0" : preferences.getString("latitude", "0")));
		geoInfo.setLongitude(Double.parseDouble("".equals(preferences.getString("longitude", "0")) ? "0" : preferences.getString("longitude", "0")));
		geoInfo.setTimeZone(preferences.getString("timeZone", TimeZone.getDefault().getID()));
		geoInfo.setAddressName(preferences.getString("addressNameKey", ""));
		geoInfo.setDstOffset(preferences.getInt("dstOffset", 0));
		geoInfo.setRawOffset(preferences.getInt("rawOffset", 0));
		geoInfo.setBeforeSunset(Integer.parseInt(preferences.getString("beforeSunset", "0")));
		geoInfo.setAfterSunset(Integer.parseInt(preferences.getString("afterSunset", "0")));

		DateTimeZone dynaZone = DateTimeZone.forID(geoInfo.getTimeZone());

		// clear cells
		clearAllCells();

		calendar.set(Calendar.DATE, 1);

		int firstDay = calendar.get(Calendar.DAY_OF_WEEK);
		int lastDay = calendar.getActualMaximum(Calendar.DATE);

		TextView title = (TextView) getView().findViewById(R.id.calTitle);
		title.setText(titleFormat.format(calendar.getTime()));

		for (int i = 0; i < lastDay; i++) {

			View dayCell = dayCells[firstDay + i - 1];
			dayCell.setFocusable(true);
			dayCell.setFocusableInTouchMode(true);

			TextView grDay = (TextView) dayCell.findViewById(R.id.gDay);
			TextView hMonth = (TextView) dayCell.findViewById(R.id.hMonth);
			TextView hDay = (TextView) dayCell.findViewById(R.id.hDay);
			ImageView img = (ImageView) dayCell.findViewById(R.id.eventicon);
			ImageView candlesImg = (ImageView) dayCell.findViewById(R.id.candleicon);
			ImageView havdalahImg = (ImageView) dayCell.findViewById(R.id.havdalah);
			TextView startTime = (TextView) dayCell.findViewById(R.id.startTime);
			TextView endTime = (TextView) dayCell.findViewById(R.id.endTime);

			startTime.setVisibility(View.INVISIBLE);
			endTime.setVisibility(View.INVISIBLE);

			// set gr date
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			grDay.setText(Integer.toString(day));

			// set hebrew date
			HebrewDate cellHDate = DateConverter.getHebrewDate(calendar.getTime());
			hMonth.setText(cellHDate.getHebrewMonth().getMonthName());
			hDay.setText(Integer.toString(cellHDate.getDay()));

			String eventKey = cellHDate.getHebrewMonth().toString() + "," + Integer.toString(cellHDate.getDay());

			// save Hebrew date in the cell
			dayCell.setTag(cellHDate);

			// set event icon and Shabbos background
			if (events.get(eventKey) != null && !HebrewCalendarAdapter.applyEventRules(events.get(eventKey), cellHDate.getYear()).isEmpty()) {
				img.setVisibility(View.VISIBLE);
				if (HebrewCalendarAdapter.hasShabbos(events.get(eventKey))) {
					cellHDate.setYomTov(true);
				}
			} else if (cellHDate.getDay() == 30 || cellHDate.getDay() == 1) {
				img.setVisibility(View.VISIBLE);
			} else {
				img.setVisibility(View.INVISIBLE);
			}

			// check for Erev Shabbos/Yom Tov
			// get day next Hebrew Date
			HebrewDate nextHDate = DateConverter.getNextHebrewDate(cellHDate);
			String nextDayEventKey = nextHDate.getHebrewMonth().toString() + "," + Integer.toString(nextHDate.getDay());
			if (events.get(nextDayEventKey) != null && HebrewCalendarAdapter.hasShabbos(events.get(nextDayEventKey))) {
				cellHDate.setBeforeYomTov(true);
			}
			HebrewDate previousHDate = DateConverter.getPreviousHebrewDate(cellHDate);
			String previousDayEventKey = previousHDate.getHebrewMonth().toString() + "," + Integer.toString(previousHDate.getDay());
			if (events.get(previousDayEventKey) != null && HebrewCalendarAdapter.hasShabbos(events.get(previousDayEventKey))) {
				cellHDate.setDayAfterYomTov(true);
			}

			if (nextHDate.isShabbos()) {
				cellHDate.setBeforeShabbos(true);
			}
			if (previousHDate.isShabbos()) {
				cellHDate.setDayAfterShabbos(true);
			}

			// set proper cell attributes
			// for candle lighting times
			if (cellHDate.isYomTov() || cellHDate.isShabbos()) {
				dayCell.setBackgroundResource(SHABBOS_CELL);
			} else {
				dayCell.setBackgroundResource(CALENDAR_CELL);
			}

			candlesImg.setVisibility(View.INVISIBLE);
			havdalahImg.setVisibility(View.INVISIBLE);
			startTime.setVisibility(View.INVISIBLE);
			endTime.setVisibility(View.INVISIBLE);

			if (geoInfo.getLatitude() != 0 && geoInfo.getLongitude() != 0) {

				//first day of yom tov in middle of wa eek
				if (!cellHDate.isYomTov() && cellHDate.isBeforeYomTov() && !cellHDate.isShabbos()) {
					activateSgabbos(candlesImg, startTime, "shabbos");
					cellHDate.setShabbosStart(CalendarUtils.getShabbosStart(location, calendar.getTime(), geoInfo, dynaZone));
					if(cellHDate.getShabbosStart() !=null) {
						startTime.setText(candlesTime.format(cellHDate.getShabbosStart()));
					}
				//shabbos
				} else if (cellHDate.isBeforeShabbos()) {
					activateSgabbos(candlesImg, startTime, "shabbos");
					cellHDate.setShabbosStart(CalendarUtils.getShabbosStart(location, calendar.getTime(), geoInfo, dynaZone));
					if(cellHDate.getShabbosStart() !=null) {
						startTime.setText(candlesTime.format(cellHDate.getShabbosStart()));
					}
				//second day of yom tov in middle of wa eek
				} else if (cellHDate.isYomTov() && cellHDate.isBeforeYomTov() && !cellHDate.isBeforeShabbos()) {
					Date shabbosEnd = CalendarUtils.getShabbosEnd(location, calendar.getTime(), geoInfo, dynaZone);
					if(shabbosEnd != null && !isNextDay(candlesTime.format(shabbosEnd))) {
						activateSgabbos(candlesImg, startTime, "shabbos");
						cellHDate.setShabbosEnd(shabbosEnd);
						if (cellHDate.getShabbosEnd() != null) {
							startTime.setText(candlesTime.format(cellHDate.getShabbosEnd()));
						}
					}
				} else if (cellHDate.isYomTov() && cellHDate.isDayAfterYomTov() && !cellHDate.isBeforeShabbos()) {
					Date shabbosEnd = CalendarUtils.getShabbosEnd(location, calendar.getTime(), geoInfo, dynaZone);
					if(shabbosEnd != null && isNextDay(candlesTime.format(shabbosEnd))) {
						activateSgabbos(candlesImg, startTime, "shabbos");
						cellHDate.setShabbosEnd(shabbosEnd);
						if (cellHDate.getShabbosEnd() != null) {
							startTime.setText(candlesTime.format(cellHDate.getShabbosEnd()));
						}
					}
					//yom tom following shabos
				} else if (cellHDate.isBeforeYomTov() && cellHDate.isShabbos()) {
					activateSgabbos(candlesImg, startTime, "shabbos");
					cellHDate.setShabbosEnd(CalendarUtils.getShabbosEnd(location, calendar.getTime(), geoInfo, dynaZone));
					if(cellHDate.getShabbosEnd() !=null) {
						startTime.setText(candlesTime.format(cellHDate.getShabbosEnd()));
					}
				}
				//end of shabbos or last day of yom tov
				if ((cellHDate.isYomTov() || cellHDate.isShabbos()) && !cellHDate.isBeforeYomTov() && !cellHDate.isBeforeShabbos()) {
					Date shabbosEnd = CalendarUtils.getShabbosEnd(location, calendar.getTime(), geoInfo, dynaZone);
					if(shabbosEnd != null && !isNextDay(candlesTime.format(shabbosEnd))) {
						String imageName = "havdalah";
						if (cellHDate.isYomTov() && !cellHDate.isShabbos()) {
							imageName = "havdalahset";
						}
						activateSgabbos(havdalahImg, endTime, imageName);
						cellHDate.setShabbosEnd(shabbosEnd);
						endTime.setText(candlesTime.format(shabbosEnd));
					}
				}
				else if ((!cellHDate.isYomTov() && !cellHDate.isShabbos()) && (cellHDate.isDayAfterYomTov() || cellHDate.isDayAfterShabbos())) {
					calendar.roll(Calendar.DAY_OF_MONTH, -1);
					Date shabbosEnd = CalendarUtils.getShabbosEnd(location, calendar.getTime(), geoInfo, dynaZone);
					calendar.roll(Calendar.DAY_OF_MONTH, 1);
					if(shabbosEnd != null && isNextDay(candlesTime.format(shabbosEnd))) {
						String imageName = "havdalah";
						if (cellHDate.isDayAfterYomTov()) {
							imageName = "havdalahset";
						}
						activateSgabbos(havdalahImg, endTime, imageName);
						cellHDate.setShabbosEnd(shabbosEnd);
						endTime.setText(candlesTime.format(shabbosEnd));
					}
				}
			}

			if (today.equals(calendar.getTime())) {
				dayCell.requestFocus();
			}

			// advance to the next day
			calendar.roll(Calendar.DAY_OF_MONTH, 1);
		}

	}

	private boolean isNextDay(String shabbosEnd) {
		boolean nextDay = false;

		if(shabbosEnd.substring(0,1).equals("0") ){
			nextDay = true;
		}
		return nextDay;
	}

	private void activateSgabbos(ImageView candlesImg, TextView time, String image) {

		candlesImg.setImageDrawable(HebrewCalendarUtils.icons.get(image));
		candlesImg.setVisibility(View.VISIBLE);
		time.setVisibility(View.VISIBLE);

	}

	private void refreshEvents() {
		events = HebrewCalendarAdapter.getAllEvents(context);
		updateDisplay();
	}

	private void clearAllCells() {

		// clear cells
		for (int i = 0; i < 42; i++) {

			View v = dayCells[i];
			v.setBackgroundResource(EMPTY_CELL);

			TextView grDay = (TextView) v.findViewById(R.id.gDay);
			TextView hMonth = (TextView) v.findViewById(R.id.hMonth);
			TextView hDay = (TextView) v.findViewById(R.id.hDay);
			ImageView img = (ImageView) v.findViewById(R.id.eventicon);
			ImageView candlesImg = (ImageView) v.findViewById(R.id.candleicon);
			TextView startTime = (TextView) v.findViewById(R.id.startTime);
			TextView endTime = (TextView) v.findViewById(R.id.endTime);
			ImageView havdalahImg = (ImageView) v.findViewById(R.id.havdalah);

			startTime.setVisibility(View.INVISIBLE);
			img.setVisibility(View.INVISIBLE);
			candlesImg.setVisibility(View.INVISIBLE);
			havdalahImg.setVisibility(View.INVISIBLE);
			grDay.setText("");
			hMonth.setText("");
			hDay.setText("");
			endTime.setText("");

			hDate = (HebrewDate) v.getTag();
			if (hDate != null) {
				hDate.setShabbosEnd(null);
				hDate.setShabbosStart(null);
			}
		}
	}

	// ****************** menu support section
	// ***********************************************************

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.events_menu, menu);

		menuHelper = new MenuHelper(getActivity());
		MenuHelper.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menuHelper.onOptionsItemSelected(item);

		String menuTitle = (String) item.getTitle();

		if (menuTitle.equals((String) context.getText(R.string.refresh))) {
			refreshEvents();
		}

		return false;
	}

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
			AddCustomEventDialog addDialog = new AddCustomEventDialog(getActivity(), saveEventListener);
			addDialog.updateDate(hDate, calendarDate);
			addDialog.show();
			break;
		case EDITEVENT_DIALOG_ID:
			UpdateEventDialog dialog = new UpdateEventDialog(callBack, hEvent);
			dialog.show(ft, String.valueOf(dialogId));
			break;
		case CELL_DIALOG_ID:
			// set events to display);
			List<HebrewCalendarEvent> dayEvents = getDayEvents();

			// set title
			Date grDate = DateConverter.getGregorianDate(hDate);
			String title = cellTitleFormat.format(grDate) + " / " + hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay() + " (" + hDate.getYear() + ")";

			CalendarDayDialog dayDialog = new CalendarDayDialog(dayEvents, title, addEventListener);
			dayDialog.show(ft, String.valueOf(dialogId));

			break;
		}

	}

	private List<HebrewCalendarEvent> getDayEvents() {

		List<HebrewCalendarEvent> dayEvents = null;

		if (hDate != null) {

			String eventKey = hDate.getHebrewMonth().toString() + "," + Integer.toString(hDate.getDay());
			if (events.get(eventKey) != null) {
				dayEvents = HebrewCalendarAdapter.applyEventRules(events.get(eventKey), hDate.getYear());
			}
			// add rosh hodesh events
			if (hDate.getDay() == 30 || hDate.getDay() == 1) {
				if (dayEvents == null) {
					dayEvents = new ArrayList<HebrewCalendarEvent>();
				}

				String description = (String) context.getText(R.string.ROSH_HODESH);
				if (hDate.getDay() == 1) {
					description = description + " " + hDate.getHebrewMonth().getMonthName();
				} else {
					HebrewMonth nextHMonth = CalendarUtils.hebrewNextMonth(hDate.getHebrewMonth(), hDate.getYear());
					description = description + " " + nextHMonth.getMonthName();
				}

				HebrewCalendarEvent roshHodesh = new HebrewCalendarEvent(HebrewEvent.ROSH_HODESH, description, "moon", hDate, false);
				dayEvents.add(roshHodesh);
			}
			// add candle lighting event
			if (hDate.getShabbosStart() != null) {
				if (dayEvents == null) {
					dayEvents = new ArrayList<HebrewCalendarEvent>();
				}
				String description = (String) context.getText(R.string.shabbas_yomtov_starts);
				String date = candlesTime.format(hDate.getShabbosStart());
				description = description + " " + date;
				HebrewCalendarEvent shabbosYomtom = new HebrewCalendarEvent(HebrewEvent.CANDLE_LIGHTING, description, "shabbos_theme", hDate, false);
				dayEvents.add(shabbosYomtom);
			}
			// add shabbos yom tov end event
			if (hDate.getShabbosEnd() != null || hDate.isDayAfterYomTov()) {
				if (dayEvents == null) {
					dayEvents = new ArrayList<HebrewCalendarEvent>();
				}
				String description = "";
				String image = "";
				if (hDate.isShabbos() || hDate.isDayAfterShabbos()) {
					description = (String) context.getText(R.string.shabbas_ends);
					image = "havdalah_theme";
				} else if(hDate.isYomTov() && hDate.isBeforeYomTov()){
					description = (String) context.getText(R.string.shabbas_yomtov_starts);
					image = "shabbos_theme";
				} else {
					description = (String) context.getText(R.string.yomtov_ends);
					image = "havdalah_theme";
				}
				String date = candlesTime.format(hDate.getShabbosEnd());
				description = description + " " + date;
				HebrewCalendarEvent shabbosYomtom = new HebrewCalendarEvent(HebrewEvent.SHABBOS_END, description, image, hDate, false);
				dayEvents.add(0,shabbosYomtom);
			}

		}
		return dayEvents;
	}
}
