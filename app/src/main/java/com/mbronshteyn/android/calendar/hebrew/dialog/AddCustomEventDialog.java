package com.mbronshteyn.android.calendar.hebrew.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarAdapter;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCustomEventDialog extends AlertDialog implements OnClickListener {

	private SimpleDateFormat grTitleFormat = new SimpleDateFormat("MMMM d yyyy");
	private Context context;
	private OnButtonClickListener callBack = null;
	private HebrewDate hDate;

	public AddCustomEventDialog(Context context) {

		super(context);

		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.add_event, null);
		setView(view);
		setTitle(context.getText(R.string.hDatePicker_title));

		setButton(BUTTON_NEUTRAL, context.getText(R.string.save_Button), this);
		setButton(BUTTON_POSITIVE, context.getText(R.string.hDatePicker_Cancel_Button), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
	}

	public AddCustomEventDialog(Context context, OnButtonClickListener callBack) {
		this(context);
		this.callBack = callBack;
	}

	/**
	 * The callback used to indicate the user pressed dialog button.
	 */
	public interface OnButtonClickListener {

		void onClick();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		CharSequence[] eventNames = context.getResources().getTextArray(R.array.eventlist);

		Spinner eventSpinner = (Spinner) findViewById(R.id.eventType);
		eventSpinner.setAdapter(new Spinnerdapter(context, R.layout.eventspinner_item, eventNames));

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		EditText descriptionField = (EditText) findViewById(R.id.eventDescription);
		String description = descriptionField.getText().toString();

		Spinner eventSpinner = (Spinner) findViewById(R.id.eventType);
		int position = eventSpinner.getSelectedItemPosition();

		HebrewEvent hEvent = null;
		String icon = null;

		switch (position) {
		case 0:
			hEvent = HebrewEvent.YAHRZEIT;
			icon = "yahrzeit";
			break;
		case 1:
			hEvent = HebrewEvent.BIRTHDAY;
			icon = "birthday";
			break;
		case 2:
			hEvent = HebrewEvent.ANNIVERSARY;
			icon = "anniversary";
			break;
		case 3:
			hEvent = HebrewEvent.OTHER;
			icon = "other";
			break;
		}

		HebrewCalendarEvent event = new HebrewCalendarEvent();

		event.setDescription(description);
		event.setEvent(hEvent);
		event.setImage(icon);
		event.setMonth(hDate.getHebrewMonth());
		event.setDay(hDate.getDay());
		event.setStart_year(hDate.getYear());
		event.setShabbos(false);

		if (!event.getEvent().equals(HebrewEvent.YAHRZEIT)) {
			HebrewCalendarAdapter.insertCustomEvent(context, event);
		} else {
			HebrewCalendarAdapter.insertYahrzeitEvent(context, event);
		}

		// refresh events
		if (callBack != null) {
			callBack.onClick();
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public void updateDate(HebrewDate hDate, Date grDate) {

		this.hDate = hDate;

		// set title
		String grDateTitle = grTitleFormat.format(grDate);
		setTitle(grDateTitle + "\n" + hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay() + " " + hDate.getYear());

		// init fields
		// EditText descriptionField = (EditText)
		// findViewById(R.id.eventDescription);
		// descriptionField.setText("");

	}

	public class Spinnerdapter extends ArrayAdapter<CharSequence> {

		private CharSequence[] eventNames;

		public Spinnerdapter(Context context, int textViewResourceId, CharSequence[] objects) {
			super(context, textViewResourceId, objects);
			eventNames = objects;

		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomVew(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomVew(position, convertView, parent);
		}

		private View getCustomVew(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.eventspinner_item, parent, false);

			TextView label = (TextView) row.findViewById(R.id.eventName);
			label.setText(eventNames[position]);

			// set event imageanniversary
			ImageView image = (ImageView) row.findViewById(R.id.icon);
			switch (position) {
			case 0:
				image.setImageDrawable(HebrewCalendarUtils.icons.get("yahrzeit"));
				break;

			case 1:
				image.setImageDrawable(HebrewCalendarUtils.icons.get("birthday"));
				break;
			case 2:
				image.setImageDrawable(HebrewCalendarUtils.icons.get("anniversary"));
				break;

			case 3:
				image.setImageDrawable(HebrewCalendarUtils.icons.get("other"));
				break;
			}

			return row;
		}

	}
}
