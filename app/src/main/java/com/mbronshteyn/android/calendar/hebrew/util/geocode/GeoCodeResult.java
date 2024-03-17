package com.mbronshteyn.android.calendar.hebrew.util.geocode;

import java.util.ArrayList;

public class GeoCodeResult {
	private Info info;

	public Info getInfo() {
		return this.info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	private Options options;

	public Options getOptions() {
		return this.options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	private ArrayList<Result> results;

	public ArrayList<Result> getResults() {
		return this.results;
	}

	public void setResults(ArrayList<Result> results) {
		this.results = results;
	}

}
