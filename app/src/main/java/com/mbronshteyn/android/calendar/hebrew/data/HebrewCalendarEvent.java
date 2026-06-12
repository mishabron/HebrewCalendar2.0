package com.mbronshteyn.android.calendar.hebrew.data;

import com.mbronshteyn.calendar.hebrew.DateConverter;
import com.mbronshteyn.calendar.hebrew.HebrewDate;
import com.mbronshteyn.calendar.hebrew.data.HebrewEvent;
import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;
import com.mbronshteyn.calendar.hebrew.exceptions.HebrewDateException;

import java.util.Date;

/**
 * @author misha
 */
public class HebrewCalendarEvent {

	private HebrewEvent event;
	private HebrewMonth month;
	private int day;
	private String image;
	private Date grDate;
	private HebrewDate hebDate;
	private boolean selected = false;
	private boolean shabbos = false;
	private int start_year = 3000;
	private String notIf;
	private String onlyIf;
	private String description;

	private boolean custom;
	private int id;

	public HebrewEvent getEvent() {
		return event;
	}

	public HebrewCalendarEvent(HebrewEvent event, HebrewMonth month, int day, String image, boolean shabbos, int start_year, String onlyIf, String notIf,
			String description) {
		this.event = event;
		this.month = month;
		this.day = day;
		this.image = image;
		this.onlyIf = onlyIf;
		this.notIf = notIf;
		this.description = description;
		this.shabbos = shabbos;
		this.start_year = start_year;
	}

	public void setEvent(HebrewEvent event) {
		this.event = event;
	}

	public HebrewMonth getMonth() {
		return month;
	}

	public void setMonth(HebrewMonth month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public HebrewCalendarEvent() {
		super();
	}

	public HebrewCalendarEvent(HebrewEvent event, String description, String image, HebrewDate hDate, boolean shabbos) {

		this.event = event;
		this.month = hDate.getHebrewMonth();
		this.day = hDate.getDay();
		this.image = image;
		this.description = description;
		this.shabbos = shabbos;

	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isShabbos() {
		return shabbos;
	}

	public void setShabbos(boolean shabbos) {
		this.shabbos = shabbos;
	}

	public int getStart_year() {
		return start_year;
	}

	public void setStart_year(int start_year) {
		this.start_year = start_year;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGrDate() {
		if (grDate == null) {
			grDate = DateConverter.getGregorianDate(hebDate);
			setGrDate(grDate);
		}
		return grDate;
	}

	public void setGrDate(Date grDate) {
		this.grDate = grDate;
	}

	public void setGrDate(int year, HebrewMonth month, int day) throws HebrewDateException {

		HebrewDate hebDate = new HebrewDate(year, month, day);
		setGrDate(DateConverter.getGregorianDate(hebDate));
	}

	public HebrewDate getHebDate() throws HebrewDateException {

		if (hebDate == null) {
			try {
				setHebDate(start_year, month, day);
			} catch (HebrewDateException e) {
				// Hebrew day may be invalid because the original event
				start_year++;
				setHebDate(start_year, month, day);

			}
		}

		return hebDate;
	}

	public void setHebDate(int year, HebrewMonth month, int day) throws HebrewDateException {
		this.hebDate = new HebrewDate(year, month, day);
	}

	public String getNotIf() {
		return notIf;
	}

	public void setNotIf(String notIf) {
		this.notIf = notIf;
	}

	public String getOnlyIf() {
		return onlyIf;
	}

	public void setOnlyIf(String onlyIf) {
		this.onlyIf = onlyIf;
	}

	public void setHebDate(HebrewDate hebDate) {
		this.hebDate = hebDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HebrewCalendarEvent)) {
			return false;
		}
		HebrewCalendarEvent other = (HebrewCalendarEvent) obj;
		if (day != other.day) {
			return false;
		}
		if (event != other.event) {
			return false;
		}
		if (image == null) {
			if (other.image != null) {
				return false;
			}
		} else if (!image.equals(other.image)) {
			return false;
		}
		if (month != other.month) {
			return false;
		}
		return true;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setId(int id) {
		this.id = id;

	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
