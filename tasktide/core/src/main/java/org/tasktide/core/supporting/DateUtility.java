/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.text.DateFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Utility to handle dates
 * 
 * @author bkenna
 */
public class DateUtility {
    
    // Attributes
    private final DateFormat DATE_FORMAT;
    private final int EXPIRATION_DAYS;
    
    /**
     * Constructing because it holds various methods
     * 
     * @param dateFormat
     * @param expiration
     */
    public DateUtility(
       @ConfigProperty(name = "task-tide.date-format", defaultValue = "dd/MM/yy HH:mm:ss") String dateFormat,
       @ConfigProperty(name = "task-tide.expiration", defaultValue = "2") int expiration
    ) {
        this.DATE_FORMAT = new SimpleDateFormat(dateFormat);
        this.EXPIRATION_DAYS = expiration;
    }

    
    /**
     * Construct default utility
     */
    public DateUtility() {
        this.DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        this.EXPIRATION_DAYS = 2;
    }
    
    
    /**
     * Get date current time
     * 
     * @return long
     */
    public long getDateLong() {
        return new Date().getTime();
    }
    
    
    /**
     * Get date from current time
     * 
     * @return Date
     */
    public Date getDate() {
        return new Date( getDateLong() );
    }
    
    
    /**
     * Get date from provided time
     * 
     * @param time
     * @return Date
     */
    public Date getDateFromTime(long time) {
        return new Date(time);
    }
    
    
    /**
     * Format provided date to string constant
     * 
     * @param date
     * @return String
     */
    public String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Get current date as a string
     * 
     * @return String
     */
    public String getNowString() {
        return formatDate( getDate() );
    }
    
    
    /**
     * Get provided date as a string
     * 
     * @param date
     * @return String
     */
    public String getDateString(Date date) {
        return formatDate( date );
    }
    
    
    /**
     * Parse provided date string to Date
     * 
     * @param date
     * @return Date
     */
    public Date parseDate(String date) {
		
        // Try format input date string
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    
    
    /**
     * Get an expirary date for input
     * 
     * @param input
     * @return Date
     */
    public Date getExpireDate(Date input) {
        
        // Set calendar to input
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input); 
        
        // Add the expiration time
        calendar.add(Calendar.DAY_OF_YEAR, EXPIRATION_DAYS);
        return calendar.getTime();
    }
    
    
    /**
     * Check if provided is before
     * 
     * @param inputDate
     * @return 
     */
    public boolean hasExceeded(Date inputDate) {
	return getExpireDate(inputDate).getTime() > inputDate.getTime();
    }
    
    
    /**
     * Get time taken
     * 
     * @param startDate
     * @param endDate
     * @return long
     */
    public long getTimeTaken(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }
}
