package com.mbronshteyn.android.custom.ui;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.mbronshteyn.android.calendar.hebrew.R;
import com.mbronshteyn.android.custom.ui.NumberPicker.OnChangedListener;
import com.mbronshteyn.calendar.hebrew.CalendarUtils;

/**
 * A view for selecting a month / year / day based on a calendar like layout.
 * For a dialog using this view, see {@link android.app.DatePickerDialog} .
 */
public class DatePicker extends FrameLayout {

	/* UI Components */
	/**
	 * @uml.property name="mDayPicker"
	 * @uml.associationEnd
	 */
	private final NumberPicker mDayPicker;
	/**
	 * @uml.property name="mMonthPicker"
	 * @uml.associationEnd
	 */
	private final NumberPicker mMonthPicker;
	/**
	 * @uml.property name="mYearPicker"
	 * @uml.associationEnd
	 */
	private final NumberPicker mYearPicker;

	/**
	 * How we notify users the date has changed.
	 * 
	 * @uml.property name="mOnDateChangedListener"
	 * @uml.associationEnd
	 */
	private OnDateChangedListener mOnDateChangedListener;

	private int mDay;
	private int mMonth;
	private int mYear;
	private Context mContext;

	/**
	 * The callback used to indicate the user changes the date.
	 */
	public interface OnDateChangedListener {

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
		void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth);
	}

	public DatePicker(Context context) {
		this(context, null);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.date_picker, this, true);

		mDayPicker = (NumberPicker) findViewById(R.id.day);
		mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		mDayPicker.setSpeed(100);
		mDayPicker.setOnChangeListener(new OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				mDay = newVal;
				if (mOnDateChangedListener != null) {
					mOnDateChangedListener.onDateChanged(DatePicker.this, mYear, mMonth, mDay);
				}
			}
		});
		mMonthPicker = (NumberPicker) findViewById(R.id.month);
		mMonthPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
		DateFormatSymbols dfs = new DateFormatSymbols();
		mMonthPicker.setRange(1, 12, dfs.getShortMonths());
		mMonthPicker.setSpeed(200);
		mMonthPicker.setOnChangeListener(new OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {

				/*
				 * We display the month 1-12 but store it 0-11 so always
				 * subtract by one to ensure our internal state is always 0-11
				 */
				mMonth = newVal - 1;
				if (mOnDateChangedListener != null) {
					mOnDateChangedListener.onDateChanged(DatePicker.this, mYear, mMonth, mDay);
				}
				updateDaySpinner();
			}
		});
		mYearPicker = (NumberPicker) findViewById(R.id.year);
		mYearPicker.setSpeed(100);
		mYearPicker.setOnChangeListener(new OnChangedListener() {
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				mYear = newVal;
				if (mOnDateChangedListener != null) {
					mOnDateChangedListener.onDateChanged(DatePicker.this, mYear, mMonth, mDay);
				}
			}
		});

		mYearPicker.setRange(CalendarUtils.GRDEFAULT_START_YEAR, CalendarUtils.GEDEFAULT_END_YEAR);

		// initialize to current date
		Calendar cal = Calendar.getInstance();
		init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

		if (!isEnabled()) {
			setEnabled(false);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mDayPicker.setEnabled(enabled);
		mMonthPicker.setEnabled(enabled);
		mYearPicker.setEnabled(enabled);
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;
		updateSpinners();
	}

	private static class SavedState extends BaseSavedState {

		private final int mYear;
		private final int mMonth;
		private final int mDay;

		/**
		 * Constructor called from {@link DatePicker#onSaveInstanceState()}
		 */
		private SavedState(Parcelable superState, int year, int month, int day) {
			super(superState);
			mYear = year;
			mMonth = month;
			mDay = day;
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in) {
			super(in);
			mYear = in.readInt();
			mMonth = in.readInt();
			mDay = in.readInt();
		}

		public int getYear() {
			return mYear;
		}

		public int getMonth() {
			return mMonth;
		}

		public int getDay() {
			return mDay;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mYear);
			dest.writeInt(mMonth);
			dest.writeInt(mDay);
		}
	}

	/**
	 * Override so we are in complete control of save / restore for this widget.
	 */
	@Override
	protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
		dispatchThawSelfOnly(container);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		return new SavedState(superState, mYear, mMonth, mDay);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		mYear = ss.getYear();
		mMonth = ss.getMonth();
		mDay = ss.getDay();
	}

	/**
	 * Initialize the state.
	 * 
	 * @param year
	 *            The initial year.
	 * @param monthOfYear
	 *            The initial month.
	 * @param dayOfMonth
	 *            The initial day of the month.
	 * @param onDateChangedListener
	 *            How user is notified date is changed by user, can be null.
	 */
	public void init(int year, int monthOfYear, int dayOfMonth, OnDateChangedListener onDateChangedListener) {
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;
		mOnDateChangedListener = onDateChangedListener;
		updateSpinners();
	}

	private void updateSpinners() {
		updateDaySpinner();
		mYearPicker.setCurrent(mYear);

		/*
		 * The month display uses 1-12 but our internal state stores it 0-11 so
		 * add one when setting the display.
		 */
		mMonthPicker.setCurrent(mMonth + 1);
	}

	private void updateDaySpinner() {
		Calendar cal = Calendar.getInstance();
		cal.set(mYear, mMonth, mDay);
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		mDayPicker.setRange(1, max);
		mDayPicker.setCurrent(mDay);
	}

	public int getYear() {
		return mYear;
	}

	public int getMonth() {
		return mMonth;
	}

	public int getDayOfMonth() {
		return mDay;
	}

}
