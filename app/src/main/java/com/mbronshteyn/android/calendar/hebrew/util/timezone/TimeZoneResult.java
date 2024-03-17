package com.mbronshteyn.android.calendar.hebrew.util.timezone;

public class TimeZoneResult {
	private String status;

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private String message;

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String countryCode;

	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	private String countryName;

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	private String zoneName;

	public String getZoneName() {
		return this.zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	private String abbreviation;

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	private int gmtOffset;

	public int getGmtOffset() {
		return this.gmtOffset;
	}

	public void setGmtOffset(int gmtOffset) {
		this.gmtOffset = gmtOffset;
	}

	private String dst;

	public String getDst() {
		return this.dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	private int zoneStart;

	public int getZoneStart() {
		return this.zoneStart;
	}

	public void setZoneStart(int zoneStart) {
		this.zoneStart = zoneStart;
	}

	private int zoneEnd;

	public int getZoneEnd() {
		return this.zoneEnd;
	}

	public void setZoneEnd(int zoneEnd) {
		this.zoneEnd = zoneEnd;
	}

	private String nextAbbreviation;

	public String getNextAbbreviation() {
		return this.nextAbbreviation;
	}

	public void setNextAbbreviation(String nextAbbreviation) {
		this.nextAbbreviation = nextAbbreviation;
	}

	private int timestamp;

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	private String formatted;

	public String getFormatted() {
		return this.formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}
}
