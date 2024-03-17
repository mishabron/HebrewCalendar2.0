package com.mbronshteyn.calendar.hebrew.data;

/**
 * @author   misha
 */
public enum HebrewMonth {
	
	/**
	 * @uml.property  name="tISHREI"
	 * @uml.associationEnd  
	 */
	TISHREI(""), /**
	 * @uml.property  name="cHESHVAN"
	 * @uml.associationEnd  
	 */
	CHESHVAN(""),/**
	 * @uml.property  name="kISLEV"
	 * @uml.associationEnd  
	 */
	KISLEV(""), /**
	 * @uml.property  name="tEVETH"
	 * @uml.associationEnd  
	 */
	TEVETH(""), /**
	 * @uml.property  name="sHEVAT"
	 * @uml.associationEnd  
	 */
	SHEVAT(""), /**
	 * @uml.property  name="aDAR"
	 * @uml.associationEnd  
	 */
	ADAR(""), /**
	 * @uml.property  name="aDAR_I"
	 * @uml.associationEnd  
	 */
	ADAR_I(""), /**
	 * @uml.property  name="aDAR_II"
	 * @uml.associationEnd  
	 */
	ADAR_II(""), /**
	 * @uml.property  name="nISAN"
	 * @uml.associationEnd  
	 */
	NISAN(""),/**
	 * @uml.property  name="iYAR"
	 * @uml.associationEnd  
	 */
	IYAR(""), /**
	 * @uml.property  name="sIVAN"
	 * @uml.associationEnd  
	 */
	SIVAN(""), /**
	 * @uml.property  name="tAMMUZ"
	 * @uml.associationEnd  
	 */
	TAMMUZ(""), /**
	 * @uml.property  name="aV"
	 * @uml.associationEnd  
	 */
	AV(""), /**
	 * @uml.property  name="eLUL"
	 * @uml.associationEnd  
	 */
	ELUL("");
	
	/**
	 * @uml.property  name="monthName"
	 */
	private String  monthName;

	private HebrewMonth(String monthName) {
		this.monthName = monthName;
	}

	/**
	 * @return
	 * @uml.property  name="monthName"
	 */
	public String getMonthName() {
		return monthName;
	}

	/**
	 * @param monthName
	 * @uml.property  name="monthName"
	 */
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	
	

}
