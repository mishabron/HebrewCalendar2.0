package com.mbronshteyn.android.calendar.hebrew.util.geocode;

import java.util.ArrayList;

public class Info {
	private int statuscode;

	public int getStatuscode() {
		return this.statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	private Copyright copyright;

	public Copyright getCopyright() {
		return this.copyright;
	}

	public void setCopyright(Copyright copyright) {
		this.copyright = copyright;
	}

	private ArrayList<Object> messages;

	public ArrayList<Object> getMessages() {
		return this.messages;
	}

	public void setMessages(ArrayList<Object> messages) {
		this.messages = messages;
	}

}
