package com.mbronshteyn.calendar.hebrew.data;

/**
 * @author misha
 */
public enum HebrewEvent {

	/**
	 * @uml.property name="rOSH_HASHANA"
	 * @uml.associationEnd
	 */
	ROSH_HASHANA("", ""), /**
	 * @uml.property name="yOM_KIPPUR"
	 * @uml.associationEnd
	 */
	YOM_KIPPUR("", ""), /**
	 * @uml.property name="sUKKOT"
	 * @uml.associationEnd
	 */
	SUKKOT("", ""), /**
	 * @uml.property name="sHEMINI_ATZERET"
	 * @uml.associationEnd
	 */
	SHEMINI_ATZERET("", ""), /**
	 * @uml.property name="sIMCHA_TORAH"
	 * @uml.associationEnd
	 */
	SIMCHA_TORAH("", ""), /**
	 * @uml.property name="cHANUKAH"
	 * @uml.associationEnd
	 */
	CHANUKAH("", ""), /**
	 * @uml.property name="tUBSHEVAT"
	 * @uml.associationEnd
	 */
	TUBSHEVAT("", ""), /**
	 * @uml.property name="pURIM"
	 * @uml.associationEnd
	 */
	PURIM("", ""),
	/**
	 * @uml.property name="pESACH"
	 * @uml.associationEnd
	 */
	PESACH("", ""), /**
	 * @uml.property name="oMER"
	 * @uml.associationEnd
	 */
	OMER("", ""), /**
	 * @uml.property name="lAG_BAOMER"
	 * @uml.associationEnd
	 */
	LAG_BAOMER("", ""), /**
	 * @uml.property name="sHAVUOT"
	 * @uml.associationEnd
	 */
	SHAVUOT("", ""), /**
	 * @uml.property name="tISHA_BAV"
	 * @uml.associationEnd
	 */
	TAMUZ_17("", ""), TEVETH_10("", ""), TISHA_BAV("", ""), /**
	 * @uml.property
	 *               name="sHABATH"
	 * @uml.associationEnd
	 */
	SHABATH("", ""), /**
	 * @uml.property name="yAHRZEIT"
	 * @uml.associationEnd
	 */
	YAHRZEIT("", ""), /**
	 * @uml.property name="bIRTHDAY"
	 * @uml.associationEnd
	 */
	BIRTHDAY("", ""), ANNIVERSARY("", ""), OTHER("", ""), ROSH_HODESH("", ""), CANDLE_LIGHTING("", ""), SHABBOS_END("", "");

	/**
	 * @uml.property name="eventName"
	 */
	private String eventName;
	/**
	 * @uml.property name="description"
	 */
	private String description;

	private HebrewEvent(String eventName, String description) {
		this.eventName = eventName;
		this.description = description;
	}

	/**
	 * @return
	 * @uml.property name="eventName"
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName
	 * @uml.property name="eventName"
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 * @uml.property name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
