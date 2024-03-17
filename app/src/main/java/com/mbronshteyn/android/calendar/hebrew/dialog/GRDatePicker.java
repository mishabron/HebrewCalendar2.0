package com.mbronshteyn.android.calendar.hebrew.dialog;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.custom.ui.DatePicker;
import com.mbronshteyn.android.custom.ui.DatePicker.OnDateChangedListener;


/**
 * @author  misha
 */
public class GRDatePicker extends AlertDialog  implements OnClickListener, OnDateChangedListener {


    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    
    /**
	 * @uml.property  name="mDatePicker"
	 * @uml.associationEnd  
	 */
    private final DatePicker mDatePicker;
    /**
	 * @uml.property  name="mCallBack"
	 * @uml.associationEnd  
	 */
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    private final java.text.DateFormat mDateFormat;
    private final String[] mWeekDays;

    private int mInitialYear;
    private int mInitialMonth;
    private int mInitialDay;
    
    private String dayPart;

    private RadioButton day;
    private RadioButton night;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(int year, int monthOfYear, int dayOfMonth, String dayNight);
    }


    private View.OnClickListener radio_listener = new View.OnClickListener() {
        public void onClick(View v) {
            // Perform action on clicks
            RadioButton rb = (RadioButton) v;
            dayPart = rb.getText().toString();
        }

    };
    
	/**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public GRDatePicker(Context context,int theme,OnDateSetListener callBack,int year,int monthOfYear,int dayOfMonth) {
        super(context);

        mCallBack = callBack;
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        DateFormatSymbols symbols = new DateFormatSymbols();
        mWeekDays = symbols.getShortWeekdays();
        
        mDateFormat = DateFormat.getMediumDateFormat(context);
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialYear, mInitialMonth, mInitialDay);
        
        setButton(context.getText(R.string.hDatePicker_Set_Button), this);
        setButton2(context.getText(R.string.hDatePicker_Cancel_Button), (OnClickListener) null);
        //setIcon(R.drawable.ic_dialog_time);
        
        LayoutInflater inflater = 
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gr_datepicker, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(mInitialYear, mInitialMonth, mInitialDay, this);  
                
        day = (RadioButton) view.findViewById(R.id.radio_day);
        night = (RadioButton) view.findViewById(R.id.radio_night);
        day.setChecked(true);
        day.setOnClickListener(radio_listener);
        night.setOnClickListener(radio_listener);        
    }

    public GRDatePicker(Context context,OnDateSetListener mDateSetListener, int mYear, int mMonth, int mDay) {
    	this(context, 0, mDateSetListener, mYear, mMonth, mDay);

	}

	@Override
    public void show() {
        super.show();
        day.setChecked(true);
        dayPart = day.getText().toString();
        /* Sometimes the full month is displayed causing the title
         * to be very long, in those cases ensure it doesn't wrap to
         * 2 lines (as that looks jumpy) and ensure we ellipsize the end.
         */
        //TextView title = (TextView) findViewById(R.id.alertTitle);
        //title.setSingleLine();
        //title.setEllipsize(TruncateAt.END);
    }
    
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            mDatePicker.clearFocus();                       
            mCallBack.onDateSet(mDatePicker.getYear(), 
                    mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),dayPart);
        }
    }
    
    public void onDateChanged(DatePicker view, int year,
            int month, int day) {
        updateTitle(year, month, day);
    }
    
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mInitialYear = year;
        mInitialMonth = monthOfYear;
        mInitialDay = dayOfMonth;
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    private void updateTitle(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        String weekday = mWeekDays[mCalendar.get(Calendar.DAY_OF_WEEK)];
        setTitle(weekday + ", " + mDateFormat.format(mCalendar.getTime()));
    }
    
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
        updateTitle(year, month, day);
    }
}
