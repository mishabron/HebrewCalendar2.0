package com.mbronshteyn.calendar.hebrew.data;

public class GeoInfo {

	private Double latitude;
	private Double longitude;
	private String timeZone;
	private String addressName;
	private int beforeSunset;
	private int afterSunset;
	private int dstOffset;
	private int rawOffset;

	public GeoInfo(Double latitude, Double longitude, String timeZone, String addressName, int beforeSunset, int afterSunset, int dstOffset, int rawOffset) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.timeZone = timeZone;
		this.addressName = addressName;
		this.beforeSunset = beforeSunset;
		this.afterSunset = afterSunset;
		this.dstOffset = dstOffset;
		this.rawOffset = rawOffset;
	}

	public GeoInfo(Double latitude, Double longitude, String timeZone, String addressName, int dstOffset, int rawOffset) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.timeZone = timeZone;
		this.addressName = addressName;
		this.dstOffset = dstOffset;
		this.rawOffset = rawOffset;
	}

	public GeoInfo() {
		// TODO Auto-generated constructor stub
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getTimeZone() {
		if (timeZone != null && timeZone.trim().isEmpty()) {
			timeZone = null;
		}
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public int getBeforeSunset() {
		return beforeSunset;
	}

	public void setBeforeSunset(int beforeSunset) {
		this.beforeSunset = beforeSunset;
	}

	public int getAfterSunset() {
		return afterSunset;
	}

	public void setAfterSunset(int afterSunset) {
		this.afterSunset = afterSunset;
	}

	public int getDstOffset() {
		return dstOffset;
	}

	public void setDstOffset(int dstOffset) {
		this.dstOffset = dstOffset;
	}

	public int getRawOffset() {
		return rawOffset;
	}

	public void setRawOffset(int rawOffset) {
		this.rawOffset = rawOffset;
	}

}
