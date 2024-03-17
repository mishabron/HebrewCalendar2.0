package com.mbronshteyn.android.calendar.hebrew.util.geocode;

import java.util.ArrayList;

public class Result {
	private ProvidedLocation providedLocation;

	public ProvidedLocation getProvidedLocation() {
		return this.providedLocation;
	}

	public void setProvidedLocation(ProvidedLocation providedLocation) {
		this.providedLocation = providedLocation;
	}

	private ArrayList<Location> locations;

	public ArrayList<Location> getLocations() {
		return this.locations;
	}

	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}
}
