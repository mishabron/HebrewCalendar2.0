package com.mbronshteyn.android.calendar.hebrew.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mbronshteyn.android.calendar.hebrew.R;

public class UpdateCalendarsDialog extends AlertDialog{

    private Context context;
    private Map<Integer, String> myCalendars;
    private OnUpdateButtonClickListener callBack = null;    
    private Spinner eventSpinner;
    private List<Map.Entry<Integer, String>> reminders;
    private Spinner reminderSpinner;    
    
	/**
     * The callback used to indicate the user pressed dialog button.
     */
    public interface OnUpdateButtonClickListener {

        void onClick(int calendarId, int minutes);

    }
    
	public UpdateCalendarsDialog(Context context,Map<Integer, String> calendars,OnUpdateButtonClickListener dialogCallBack  ) {
		
		super(context);
		this.context = context;
		myCalendars = calendars;
		callBack = dialogCallBack;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.update_calendars, null);        
		setView(view);
		setTitle(context.getText(R.string.update_calendar));
		
		int[] alarms = context.getResources().getIntArray(R.array.alarms);
		CharSequence[] notofications = context.getResources().getTextArray(R.array.notifications);
		Map<Integer, String> alarmMap = new TreeMap<Integer, String>();
		for(int i=0; i< alarms.length;i++){
			alarmMap.put(alarms[i],notofications[i].toString());
		}
		reminders = new ArrayList<Map.Entry<Integer, String>>(alarmMap.entrySet());
		
		setButton(BUTTON_NEUTRAL, context.getText(R.string.updateCalendar_Button),
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						// refresh events
						if (callBack != null) {
							
							//get calendar id
							Spinner eventSpinner = (Spinner) findViewById(R.id.myCalendars);
							View calRrow  = eventSpinner.getSelectedView();
							TextView id=(TextView)calRrow.findViewById(R.id.calendarId);
							
							//get reminder minutes
							Spinner reminderSpinner = (Spinner) findViewById(R.id.notofications);
							View reminderRow  = reminderSpinner.getSelectedView();
							TextView minutes=(TextView)reminderRow.findViewById(R.id.notifyValue);							
							
							callBack.onClick(Integer.parseInt(id.getText().toString()),Integer.parseInt(minutes.getText().toString()));
						}
					}
				});
		
		setButton(BUTTON_POSITIVE ,context.getText(R.string.hDatePicker_Cancel_Button), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
	               dismiss();				
			}			
		});		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		//setup calendar spinner
		this.eventSpinner = (Spinner) findViewById(R.id.myCalendars);					
		List<Map.Entry<Integer, String>> spinnerCalendars = new ArrayList<Map.Entry<Integer, String>>(myCalendars.entrySet());					
		this.eventSpinner.setAdapter(new CalendarSpinnerdapter(context, R.layout.calendarspinner_item, spinnerCalendars));
		
		//setup notifications spinner
		this.reminderSpinner = (Spinner) findViewById(R.id.notofications);	
		reminderSpinner.setAdapter(new ReminderSpinnerdapter(context, R.layout.alarmspinner_item, reminders));	
		
	}	
			
	public class CalendarSpinnerdapter extends ArrayAdapter<Map.Entry<Integer, String>>{

		List<Map.Entry<Integer, String>> myCalendars;
		
		public CalendarSpinnerdapter(Context context, int textViewResourceId,List<Map.Entry<Integer, String>> calendars) {
			super(context, textViewResourceId, calendars);
			myCalendars = calendars;
		}

		@Override
		public View getDropDownView(int position, View convertView,ViewGroup parent) {

			return getCustomVew(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomVew(position, convertView, parent);
		}
		
		private View getCustomVew(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater=getLayoutInflater();
			View row = inflater.inflate(R.layout.calendarspinner_item, parent, false);
			
			TextView name=(TextView)row.findViewById(R.id.calendarName);
			name.setText(myCalendars.get(position).getValue());
			
			TextView id=(TextView)row.findViewById(R.id.calendarId);
			id.setText(myCalendars.get(position).getKey().toString());			
						
			return row;
		}			
			
	}
	
	public class ReminderSpinnerdapter extends ArrayAdapter<Map.Entry<Integer, String>>{

		List<Map.Entry<Integer, String>> myAlarms;
		
		public ReminderSpinnerdapter(Context context, int textViewResourceId,List<Map.Entry<Integer, String>> alarms) {
			super(context, textViewResourceId, alarms);
			myAlarms = alarms;
		}

		@Override
		public View getDropDownView(int position, View convertView,ViewGroup parent) {

			return getCustomVew(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomVew(position, convertView, parent);
		}
		
		private View getCustomVew(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater=getLayoutInflater();
			View row = inflater.inflate(R.layout.alarmspinner_item, parent, false);
			
			TextView name=(TextView)row.findViewById(R.id.notifyName);
			name.setText(myAlarms.get(position).getValue());
			
			TextView minutes=(TextView)row.findViewById(R.id.notifyValue);
			minutes.setText(myAlarms.get(position).getKey().toString());			
						
			ImageView image = (ImageView)row.findViewById(R.id.alarmIcon);	
			if(position ==0){			
				image.setImageDrawable(context.getResources().getDrawable(R.drawable.clock_delete));				
			}
			else{
				image.setImageDrawable(context.getResources().getDrawable(R.drawable.clock));			
			}
			
			return row;
		}			
			
	}	
}
