package com.mbronshteyn.android.calendar.hebrew.dialog;

import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.calendar.hebrew.data.HebrewCalendarEvent;
import com.mbronshteyn.android.calendar.hebrew.util.HebrewCalendarUtils;

public class CalendarDayDialog extends DialogFragment {

	private View mainView;
	private LayoutInflater inflater;
	private OnButtonClickListener callBack;
	private List<HebrewCalendarEvent> dayEvents;
	private String title;

	public CalendarDayDialog(List<HebrewCalendarEvent> dayEvents, String title, final OnButtonClickListener callBack) {

		this.callBack = callBack;
		this.dayEvents = dayEvents;
		this.title = title;
	}

	private void setTitle(String string) {

		getDialog().setTitle(string);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mainView = inflater.inflate(R.layout.daycell_popup, null);

		final Button closeButton = (Button) mainView.findViewById(R.id.close_dialog_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

		final Button addButton = (Button) mainView.findViewById(R.id.add_dialog_button);
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				callBack.onAddClick();
			}
		});

		if (dayEvents != null && !dayEvents.isEmpty()) {
			updateDisplay(dayEvents);
		}

		updateTitle(title);

		return mainView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * The callback used to indicate the user pressed dialog button.
	 */
	public interface OnButtonClickListener {

		void onAddClick();

		void onEditClick(HebrewCalendarEvent dayEvent);

	}

	public void updateTitle(String title) {
		setTitle(title);
	}

	public void updateDisplay(List<HebrewCalendarEvent> dayEvents) {

		TableLayout items = (TableLayout) mainView.findViewById(R.id.events);

		// display events from database
		for (final HebrewCalendarEvent dayEvent : dayEvents) {

			TableRow trow = new TableRow(getActivity());

			LayoutInflater vi = LayoutInflater.from(getActivity());

			View dayItem = vi.inflate(R.layout.daycell_item, null);

			// set event name
			TextView eventName = (TextView) dayItem.findViewById(R.id.eventName);
			if (eventName != null) {
				eventName.setText(dayEvent.getDescription());
			}

			// set event image
			ImageView image = (ImageView) dayItem.findViewById(R.id.icon);
			if (image != null) {
				image.setImageDrawable(HebrewCalendarUtils.icons.get(dayEvent.getImage()));
			}

			dayItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					callBack.onEditClick(dayEvent);
				}

			});

			trow.addView(dayItem);
			items.addView(trow);

		}

	}

	public void clearEvents() {
		TableLayout items = (TableLayout) mainView.findViewById(R.id.events);
		items.removeAllViews();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

}
