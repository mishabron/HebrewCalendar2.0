package com.mbronshteyn.android.calendar.hebrew.dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarAdapter;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;
import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;
import com.mbronshteyn.calendar.hebrew.exceptions.HebrewDateException;

public class UpdateEventDialog extends DialogFragment {

	private View mainView;
	private Button updateButton;
	private Button deleteButton;
	private SimpleDateFormat grTitleFormat = new SimpleDateFormat("MMMM d yyyy");
	private AlertDialog.Builder confirmBuilder;
	private Button dateButton;
	private AlertDialog confirmAlert;

	private OnEventUpdateListener callBack;
	private Button reverseButton;
	private HebrewCalendarEvent hEvent;
	private HebrewDate hDate;
	private Date grDate;

	private View.OnClickListener deleteListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			confirmAlert.show();
		}
	};

	private View.OnClickListener updateListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			if (hEvent.isCustom()) {

				// set event description
				EditText descriptionField = (EditText) getDialog().findViewById(R.id.eventDescription);
				String eventDescription = descriptionField.getText().toString();

				hEvent.setDescription(eventDescription);
				hEvent.setMonth(hDate.getMonthName());
				hEvent.setDay(hDate.getDay());
				hEvent.setStart_year(hDate.getYear());
				hEvent.setHebDate(hDate);

				HebrewCalendarAdapter.updateCustomEvent(getActivity(), hEvent);

				if (callBack != null) {
					callBack.onUpdate();
				}
				dismiss();
			}
		}
	};

	/**
	 * The callback used to indicate the user is done dialog activity.
	 */
	public interface OnEventUpdateListener {
		void onUpdate();
	}

	public UpdateEventDialog(OnEventUpdateListener callBack, HebrewCalendarEvent hEvent) {

		// super(context,R.style.PauseDialog);
		this.callBack = callBack;
		this.hEvent = hEvent;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mainView = inflater.inflate(R.layout.event_update, null);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		final Button closeButton = (Button) mainView.findViewById(R.id.cancel_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

		dateButton = (Button) mainView.findViewById(R.id.dateButton);

		reverseButton = (Button) mainView.findViewById(R.id.reverseButton);

		dateButton.setTag(true);

		updateButton = (Button) mainView.findViewById(R.id.update_button);
		deleteButton = (Button) mainView.findViewById(R.id.delete_button);
		updateButton.setOnClickListener(updateListener);
		deleteButton.setOnClickListener(deleteListener);

		try {

			hDate = hEvent.getHebDate();
			grDate = DateConverter.getGregorianDate(hDate);

			dateButton.setText(hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay() + (hDate.getYear() != 0 ? " " + hDate.getYear() : ""));

		} catch (HebrewDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Boolean hebDate = (Boolean) dateButton.getTag();
				if (hebDate) {
					HewbrewDatePicker hDateDialog = new HewbrewDatePicker(getActivity(), hDateSetListener, hDate);
					hDateDialog.show();
				} else {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(grDate);
					GRDatePicker grDialog = new GRDatePicker(getActivity(), mDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH));
					grDialog.show();
				}

			}
		});

		reverseButton.setOnClickListener(new OnClickListener() {

			boolean hebDate = true;

			@Override
			public void onClick(View v) {
				hebDate = !hebDate;
				dateButton.setTag(hebDate);
				if (hebDate) {
					dateButton.setText(hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay() + (hDate.getYear() != 0 ? " " + hDate.getYear() : ""));
				} else {
					dateButton.setText(grTitleFormat.format(grDate));
				}

			}
		});

		updateDisplay(hEvent);

		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Activity context = getActivity();

		// create confirm delete dialog
		confirmBuilder = new AlertDialog.Builder(context);
		confirmBuilder.setMessage(context.getText(R.string.deleteQuestion));
		confirmBuilder.setCancelable(false);

		confirmBuilder.setPositiveButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				deleteCustomEvent();
				;
			}
		});

		confirmBuilder.setNegativeButton(context.getText(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		confirmAlert = confirmBuilder.create();
	}

	protected void deleteCustomEvent() {

		if (hEvent.isCustom()) {
			HebrewCalendarAdapter.deleteCustomEvent(getActivity(), hEvent.getId());
			if (callBack != null) {
				callBack.onUpdate();
			}
			dismiss();
		}
	}

	public void updateTitle(HebrewCalendarEvent dayEvent) {

		HebrewDate hDate = null;
		try {
			hDate = dayEvent.getHebDate();
		} catch (HebrewDateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set title
		getDialog().setTitle(hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay());

	}

	public void updateDisplay(HebrewCalendarEvent dayEvent) {

		// set event name
		TextView eventName = (TextView) mainView.findViewById(R.id.eventName);
		if (eventName != null) {
			eventName.setText(dayEvent.getEvent().getEventName());
		}

		// set event image
		ImageView image = (ImageView) mainView.findViewById(R.id.icon);
		if (image != null) {
			image.setImageDrawable(HebrewCalendarUtils.icons.get(dayEvent.getImage()));
		}

		// set event description
		EditText descriptionField = (EditText) mainView.findViewById(R.id.eventDescription);
		descriptionField.setText(dayEvent.getDescription());

		if (!dayEvent.isCustom()) {
			descriptionField.setEnabled(false);
			updateButton.setEnabled(false);
			deleteButton.setEnabled(false);
			reverseButton.setVisibility(View.INVISIBLE);
			dateButton.setEnabled(false);
		} else {
			descriptionField.setEnabled(true);
			updateButton.setEnabled(true);
			deleteButton.setEnabled(true);
			reverseButton.setVisibility(View.VISIBLE);
			dateButton.setEnabled(true);
		}
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	private HewbrewDatePicker.OnDateSetListener hDateSetListener = new HewbrewDatePicker.OnDateSetListener() {

		public void onDateSet(int year, HebrewMonth monthOfYear, int dayOfMonth) {

			hDate.setYear(year);
			hDate.setHebrewMonth(monthOfYear);
			hDate.setDay(dayOfMonth);

			grDate = DateConverter.getGregorianDate(hDate);

			dateButton.setText(hDate.getHebrewMonth().getMonthName() + " " + hDate.getDay() + (hDate.getYear() != 0 ? " " + hDate.getYear() : ""));

		}
	};

	private GRDatePicker.OnDateSetListener mDateSetListener = new GRDatePicker.OnDateSetListener() {

		public void onDateSet(int year, int monthOfYear, int dayOfMonth, String dayNight) {

			Calendar calendar = Calendar.getInstance();
			calendar.set(year, monthOfYear, dayOfMonth);
			grDate = calendar.getTime();
			hDate = DateConverter.getHebrewDate(grDate);

			if (getActivity().getText(R.string.nightTime).equals(dayNight)) {
				hDate = DateConverter.getNextHebrewDate(hDate);
			}
			dateButton.setText(grTitleFormat.format(grDate));
		}
	};
}
