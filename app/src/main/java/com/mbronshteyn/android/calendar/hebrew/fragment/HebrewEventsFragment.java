package com.mbronshteyn.android.calendar.hebrew.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarAdapter;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.android.calendar.hebrew.dialog.CustomProgressDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateCalendarsDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateCalendarsDialog.OnUpdateButtonClickListener;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateEventDialog;
import com.mbronshteyn.android.calendar.hebrew.dialog.UpdateEventDialog.OnEventUpdateListener;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.android.calendar.hebrew.util.MenuHelper;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;

/**
 * @author misha
 */
public class HebrewEventsFragment extends ListFragment {

	private CustomProgressDialog mProgressDialog = null;
	private EventAdapter m_adapter;
	private Runnable viewEvents;
	private Runnable updateCalendars;
	private static ArrayList<HebrewCalendarEvent> events = null;
	private Context context;
	private SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
	private int hYear;
	private EditText yearInput;
	private Button previousBtn;
	private Button nextBtn;
	private OnClickListener click;
	private Map<Integer, Integer> positionMap = new HashMap<Integer, Integer>();
	private Thread thread;
	private static Map<Integer, String> myCalendars = null;
	private ContentResolver contentResolver;
	private int calendarId;
	private int reminderMinutes;
	private static final int UPDATE_EVENT_DIALOG_ID = 0;
	private static final int UPDATE_CALENDAR_DIALOG_ID = 1;
	private static HebrewCalendarEvent hEvent;
	private MenuHelper menuHelper;

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (events != null && events.size() > 0) {
				m_adapter.clear();
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < events.size(); i++) {
					m_adapter.add(events.get(i));
				}
			}

			m_adapter.notifyDataSetChanged();
			mProgressDialog.killProgressDialog();
			setSelection(getNextEventPosition());
		}
	};

	private Runnable returnCalendars = new Runnable() {

		@Override
		public void run() {
			unselectAll();
			mProgressDialog.killProgressDialog();
		}
	};

	private OnEventUpdateListener callBack = new OnEventUpdateListener() {

		@Override
		public void onUpdate() {
			updateEvents();
		}

	};

	private OnUpdateButtonClickListener updateCallBack = new OnUpdateButtonClickListener() {

		@Override
		public void onClick(int dialogCalendarId, int minutes) {

			calendarId = dialogCalendarId;
			reminderMinutes = minutes;
			mProgressDialog.showProgressDialog((String) context.getText(R.string.updatingCalendar) + ": " + myCalendars.get(calendarId),
					ProgressDialog.STYLE_SPINNER);

			thread = new Thread(null, updateCalendars, "UpdateCalendars");
			thread.start();
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getActivity().getBaseContext();
		events = new ArrayList<HebrewCalendarEvent>();

		// Retain this fragment across configuration changes.
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.events_list, container, false);

		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(R.string.invalid_year_title);
		alertDialog.setIcon(R.drawable.calendar);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		mProgressDialog = new CustomProgressDialog(getActivity());
		mProgressDialog.setProgressDialog(ProgressDialog.STYLE_SPINNER);
		if (mProgressDialog.isDialogShowing()) {
			mProgressDialog.showProgressDialog();
		}

		hYear = DateConverter.getTodayHebrewDate().getYear();

		yearInput = (EditText) view.findViewById(R.id.year_input);
		previousBtn = (Button) view.findViewById(R.id.previous);
		nextBtn = (Button) view.findViewById(R.id.next);

		yearInput.setText(Integer.toString(hYear));
		yearInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		yearInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 4) {
					int year = Integer.parseInt(s.toString());
					// validate input
					if (year < CalendarUtils.HEBR_BASE_YEAR) {
						alertDialog.setMessage(context.getText(R.string.minimum_hebrew_year) + " " + Integer.toString(CalendarUtils.HEBR_BASE_YEAR));
						alertDialog.show();
						if (!yearInput.getText().equals(Integer.toString(hYear))) {
							s.replace(0, 4, Integer.toString(hYear));
						}
					} else if (year > CalendarUtils.HEBR_LAST_YEAR) {
						alertDialog.setMessage(context.getText(R.string.maximum_hebrew_year) + " " + Integer.toString(CalendarUtils.HEBR_LAST_YEAR));
						alertDialog.show();
						if (!yearInput.getText().equals(Integer.toString(hYear))) {
							s.replace(0, 4, Integer.toString(hYear));
						}
					} else {
						hYear = year;
						updateEvents();

					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
		});

		nextBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ((hYear + 1) > CalendarUtils.HEBR_LAST_YEAR) {
					alertDialog.setMessage(context.getText(R.string.maximum_hebrew_year) + " " + Integer.toString(CalendarUtils.HEBR_LAST_YEAR));
					alertDialog.show();
					yearInput.setText(Integer.toString(hYear));
				} else {
					hYear++;
					updateYear();
				}
			}
		});

		previousBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ((hYear - 1) < CalendarUtils.HEBR_BASE_YEAR) {
					alertDialog.setMessage(context.getText(R.string.minimum_hebrew_year) + " " + Integer.toString(CalendarUtils.HEBR_BASE_YEAR));
					alertDialog.show();
					yearInput.setText(Integer.toString(hYear));
				} else {
					hYear--;
					updateYear();
				}
			}
		});

		click = new OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				int position = positionMap.get(cb.hashCode());
				if (cb.isChecked()) {
					HebrewCalendarEvent event = events.get(position);
					event.setSelected(true);

					if (event.getEvent().equals(HebrewEvent.SUKKOT)) {
						checkAllEvents(events, HebrewEvent.SUKKOT);
					}
					if (event.getEvent().equals(HebrewEvent.CHANUKAH)) {
						checkAllEvents(events, HebrewEvent.CHANUKAH);
					}
					if (event.getEvent().equals(HebrewEvent.PESACH)) {
						checkAllEvents(events, HebrewEvent.PESACH);
					}

				} else {
					events.get(position).setSelected(false);
				}

			}
		};

		this.m_adapter = new EventAdapter(context, R.layout.event_item, events);

		setListAdapter(this.m_adapter);

		viewEvents = new Runnable() {
			@Override
			public void run() {
				loadEvents();
			}
		};

		updateCalendars = new Runnable() {
			@Override
			public void run() {
				updateMyCalendars();
			}
		};

		contentResolver = context.getContentResolver();

		if (myCalendars == null) {
			myCalendars = HebrewCalendarUtils.getLocalCalendars(contentResolver);
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		updateEvents();

		ListView listView = getListView();
		listView.setItemsCanFocus(true);
	}

	protected void checkAllEvents(ArrayList<HebrewCalendarEvent> events, HebrewEvent eventName) {

		for (HebrewCalendarEvent event : events) {
			if (event.getEvent().equals(eventName)) {
				event.setSelected(true);
			}
		}
		m_adapter.notifyDataSetChanged();
	}

	private void updateEvents() {

		thread = new Thread(null, viewEvents, "UpdateEvents");
		thread.start();

		mProgressDialog.showProgressDialog((String) context.getText(R.string.gettigData), ProgressDialog.STYLE_SPINNER);
	}

	@SuppressWarnings("unchecked")
	private void loadEvents() {

		try {

			if (HebrewCalendarUtils.icons == null) {
				HebrewCalendarUtils.icons = HebrewCalendarUtils.loadIcons(getResources());
			}

			Object data = getActivity().getLastNonConfigurationInstance();

			if (data == null) {
				Log.d("loadEvents", "SAVED DATA IS NULL LOAD NEW EVENTS");
				emptyEvents(events);
				events = (ArrayList<HebrewCalendarEvent>) HebrewCalendarAdapter.getEventsForYear(context, hYear);
				Thread.sleep(100);
			} else {
				events = (ArrayList<HebrewCalendarEvent>) data;
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getActivity().runOnUiThread(returnRes);
	}

	private void emptyEvents(ArrayList<HebrewCalendarEvent> events) {

		for (int i = 0; events != null && i < events.size(); i++) {
			events.remove(i);
		}
		events.clear();
	}

	private void updateYear() {
		yearInput.setText(Integer.toString(hYear));
		yearInput.clearFocus();
	}

	private class EventAdapter extends ArrayAdapter<HebrewCalendarEvent> {

		private ArrayList<HebrewCalendarEvent> adapterEvents;
		private LayoutInflater mInflater;

		public EventAdapter(Context context, int textViewResourceId, ArrayList<HebrewCalendarEvent> events) {
			super(context, textViewResourceId, events);
			this.adapterEvents = events;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder eventItem = null;

			if (convertView == null) {

				try {
					convertView = mInflater.inflate(R.layout.event_item, null);

					convertView.setFocusable(true);
					convertView.setFocusableInTouchMode(true);
					convertView.setClickable(true);

					// Creates a ViewHolder and store references to the two
					// children views
					// we want to bind data to.
					eventItem = new ViewHolder();
					eventItem.eventName = (TextView) convertView.findViewById(R.id.eventName);
					eventItem.hDate = (TextView) convertView.findViewById(R.id.hDate);
					eventItem.gDate = (TextView) convertView.findViewById(R.id.gDate);
					eventItem.image = (ImageView) convertView.findViewById(R.id.icon);
					eventItem.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

					convertView.setTag(eventItem);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// Get the ViewHolder back to get fast access to the list items
				eventItem = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			HebrewCalendarEvent event = adapterEvents.get(position);
			if (event != null) {

				eventItem.eventName.setText(event.getDescription());
				eventItem.hDate.setText(event.getMonth().getMonthName() + " " + event.getDay());
				eventItem.gDate.setText(df.format(event.getGrDate()));
				eventItem.image.setImageDrawable(HebrewCalendarUtils.icons.get(event.getImage()));
				// save position of the checkbox
				positionMap.put(eventItem.checkbox.hashCode(), position);
				eventItem.checkbox.setOnClickListener(click);
				eventItem.checkbox.setChecked(event.isSelected());

				final TextView eventName = eventItem.eventName;
				convertView.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					// make event name scrollable by selecting the text
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							eventName.setSelected(true);
						} else {
							eventName.setSelected(false);
						}

					}
				});
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						hEvent = adapterEvents.get(position);
						if (hEvent != null) {
							showDialog(UPDATE_EVENT_DIALOG_ID);
						}
					}
				});

			}
			return convertView;
		}

		class ViewHolder {
			TextView eventName;
			TextView hDate;
			TextView gDate;
			ImageView image;
			CheckBox checkbox;
		}
	}

	// ****************** menu support section
	// ***********************************************************

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.hebrew_events_menu, menu);
		inflater.inflate(R.menu.events_menu, menu);

		menuHelper = new MenuHelper(getActivity());
		MenuHelper.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		String menuTitle = (String) item.getTitle();

		menuHelper.onOptionsItemSelected(item);

		if (menuTitle.equals((String) context.getText(R.string.select_all))) {
			selectAll();
		} else if (menuTitle.equals((String) context.getText(R.string.unselect_all))) {
			unselectAll();
		} else if (menuTitle.equals((String) context.getText(R.string.refresh))) {
			updateEvents();
		} else if (menuTitle.equals((String) context.getText(R.string.update_calendar))) {
			showDialog(UPDATE_CALENDAR_DIALOG_ID);
		}
		return false;
	}

	private void selectAll() {

		for (int i = 0; i < events.size(); i++) {
			events.get(i).setSelected(true);
		}
		m_adapter.notifyDataSetChanged();
	}

	private void unselectAll() {

		for (int i = 0; i < events.size(); i++) {
			events.get(i).setSelected(false);
		}
		m_adapter.notifyDataSetChanged();
	}

	private void updateMyCalendars() {
		try {
			HebrewCalendarUtils.updateCalendar(contentResolver, context, calendarId, events, reminderMinutes);
			Thread.sleep(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getActivity().runOnUiThread(returnCalendars);
	}

	private int getNextEventPosition() {

		int returnPos = events.size();
		Date curGrDate = new Date();

		for (int i = 0; i < events.size(); i++) {

			Date eventGrDate = events.get(i).getGrDate();
			int compare = eventGrDate.compareTo(curGrDate);

			if (compare >= 0) {
				returnPos = i;
				break;
			}
		}
		return returnPos;
	}

	protected void showDialog(int dialogId) {

		switch (dialogId) {
		case UPDATE_EVENT_DIALOG_ID:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment prev = getFragmentManager().findFragmentByTag(String.valueOf(dialogId));
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);
			UpdateEventDialog dialog = new UpdateEventDialog(callBack, hEvent);
			dialog.show(ft, String.valueOf(dialogId));
			break;
		case UPDATE_CALENDAR_DIALOG_ID:
			UpdateCalendarsDialog updateCalendarDialog = new UpdateCalendarsDialog(getActivity(), myCalendars, updateCallBack);
			updateCalendarDialog.show();
			break;
		}

	}
}
