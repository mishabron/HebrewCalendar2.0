package com.mbronshteyn.android.calendar.hebrew.util.geocode;

public class Options {
	private int maxResults;

	public int getMaxResults() {
		return this.maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	private boolean thumbMaps;

	public boolean getThumbMaps() {
		return this.thumbMaps;
	}

	public void setThumbMaps(boolean thumbMaps) {
		this.thumbMaps = thumbMaps;
	}

	private boolean ignoreLatLngInput;

	public boolean getIgnoreLatLngInput() {
		return this.ignoreLatLngInput;
	}

	public void setIgnoreLatLngInput(boolean ignoreLatLngInput) {
		this.ignoreLatLngInput = ignoreLatLngInput;
	}
}
