package com.mbronshteyn.calendar.hebrew;

import com.mbronshteyn.calendar.hebrew.data.HebrewMonth;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {
	
	
	public static HebrewDate getHebrewDate(Date gDate){
		
		HebrewDate retDate = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(gDate);
				
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
     
        int absDate = CalendarUtils.absoluteFromGgregorian(month, day, year);	
        		
        retDate = CalendarUtils.hebrewFromAbsolute(absDate);
                
		return retDate;
		
	}
	
	public static HebrewDate getNextHebrewDate(HebrewDate hDate){
		
		int abs = CalendarUtils.absoluteFromHebrew(hDate.getMonth(), hDate.getDay(), hDate.getYear());
		abs++;
		
		return CalendarUtils.hebrewFromAbsolute(abs);
	}

	public static HebrewDate getPreviousHebrewDate(HebrewDate hDate){

		int abs = CalendarUtils.absoluteFromHebrew(hDate.getMonth(), hDate.getDay(), hDate.getYear());
		abs--;

		return CalendarUtils.hebrewFromAbsolute(abs);
	}

	public static Date getGregorianDate(HebrewDate hDate){
		
		Date retDate = null;
		                
        int absDate = CalendarUtils.absoluteFromHebrew(hDate.getMonth(), hDate.getDay(), hDate.getYear());
        
        retDate =  CalendarUtils.gregorianFromAbsolute(absDate);
		                
		return retDate;
		
	}	

	public static HebrewDate getTodayHebrewDate(){
		return getHebrewDate(new Date());
	}
	
	public static int getTodayHebrewYear(){
		return getHebrewDate(new Date()).getYear();
	}
	
	public static HebrewMonth checkProperAdar(HebrewMonth hMonth, int hYear){

		HebrewMonth returnMonth = hMonth;
		boolean leapYear = CalendarUtils.hebrLeapYear(hYear);

		if(hMonth.equals(HebrewMonth.ADAR) || hMonth.equals(HebrewMonth.ADAR_I) || hMonth.equals(HebrewMonth.ADAR_II)){
			if(leapYear && hMonth.equals(HebrewMonth.ADAR)){
				returnMonth = HebrewMonth.ADAR_II;
			}
			else if(!leapYear){
				returnMonth = HebrewMonth.ADAR;
			}
		}

		return returnMonth;
	}
	
	
	public static HebrewDate correctHebrewDate(HebrewDate hDate){
		
		//validate months of ADARs
		HebrewMonth hMonth = hDate.getHebrewMonth();
		hMonth = checkProperAdar(hMonth,hDate.getYear());
		hDate.setHebrewMonth(hMonth);
		
		//check for 30th of the month
		if(hMonth.equals(HebrewMonth.ADAR) || hMonth.equals(HebrewMonth.KISLEV) || hMonth.equals(HebrewMonth.CHESHVAN)){
			if(hDate.getDay() == 30 && !HebrewDate.hebrewDateValid(hDate.getYear(), hDate.getHebrewMonth(), hDate.getDay())){
				hDate.setDay(29);
			}			
		}
		
		return hDate;
	}
	
	
	public static HebrewDate getHebrewDateForGrYear(int grYear, HebrewDate hDate){
		
		Calendar calendar = Calendar.getInstance();	
		calendar.set(grYear,0,1);
		HebrewDate firstHebrewDate = getHebrewDate(calendar.getTime());
		
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        
		int firstDay = CalendarUtils.absoluteFromGgregorian(month, day, year);
		int thisDay  = CalendarUtils.absoluteFromHebrew(hDate.getMonth(), hDate.getDay(), firstHebrewDate.getYear());
				
		if(thisDay <firstDay){			
			//advance hebrew year
			int hYear = firstHebrewDate.getYear();
			hYear++;
			hDate.setYear(hYear);	
		}
		else{
			hDate.setYear(firstHebrewDate.getYear());
		}
		
		//validate new date
		hDate = correctHebrewDate(hDate);
		
		return hDate;
	}
}
