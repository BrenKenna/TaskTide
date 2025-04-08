/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * Class to support various actions
 * 
 * @author bkenna
 */
public class Utils {
    
    // Attributes
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    private final int EXPIRATION_DAYS = 2;
    private final Charset charSet;
    private static final Random rand = new Random();
    
    
    /**
     * Constructing because it holds various methods
     */
    public Utils() {
        this.charSet = StandardCharsets.UTF_8;
    }
    
    
    /**
     * Construct utility with desired charset
     * 
     * @param charset 
     */
    public Utils(Charset charset) {
        this.charSet = charset;
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
        Date output = null;
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
    
    
    /**
     * Generate a salt from a random UUID string
     * 
     * @return String
     */
    public String generateSalt() {
        return UUID.randomUUID().toString();
    }
    
    
    /**
     * Fetch UUID from input string
     * 
     * @param input
     * @return String
     */
    public UUID getUUID(String input) {
        return UUID.fromString(input);
    }
    
    
    /**
     * Convert input string to hexidecimal
     * 
     * @param input
     * @return String
     */
    public String convertStringToHex(String input) {
        
        // Intialize vars
        byte[] bytes;
        StringBuilder hexBuild;
        
        // Fetch hex string
        bytes = input.getBytes(charSet);
        hexBuild = new StringBuilder();
        for ( byte b : bytes ) {
            hexBuild.append(String.format("%02x", b));
        }
        return hexBuild.toString();
    }
    
    
    /**
     * Generate hexidecimal string from salted current time
     * 
     * @return String
     */
    public String generateToken() {
        
        // Initialize vars
        String nowDate, salt, token;
        
        // Create token string
        nowDate = getNowString();
        salt = generateSalt();
        token = nowDate + "-" + salt;
        
        // Return string as hexdecimal
        return convertStringToHex(token);
    }
    
    
    /**
     * Hexideciaml string from input byte array
     * 
     * @param digest
     * @return String
     */
    public String getHexString(byte[] digest) {
        StringBuilder hexString = new StringBuilder(2 * digest.length);
	    for (int i = 0; i < digest.length; i++) {
	        String hex = Integer.toHexString(0xff & digest[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	return hexString.toString();
    }
    
    
    /**
     * Generate fingerprint from salted input
     * 
     * @param input
     * @param salt
     * @return String
     */
    public String generateSHA_Hash(String input, String salt) {
		
        // Try generate hash from password
        String output = null;
        try {

            // Generate digest for salted password
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String toHash = input + "-" + salt;
            messageDigest.update(toHash.getBytes(StandardCharsets.UTF_8));

            // Parse digest to hexadecimal string
            byte[] digest = messageDigest.digest();
            output = getHexString(digest);
            messageDigest.reset();
            
        } // Catch exception
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        // Return results
        // Null string & proper output should be handled appropriately
        return output;
    }
    
    
    /**
     * Generate random number from 0 to limit
     * 
     * @param limit
     * @return int
     */
    public int getRandInt(int limit) {
        return rand.nextInt(limit);
    }

    
    /**
     * Wait random amount of seconds up to limit
     * 
     * @param amount
     * @return boolean
     */
    public boolean waitSeconds(int amount) {
        amount = this.getRandInt(amount) + 1;
        try {
            TimeUnit.SECONDS.sleep(amount);
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }
    
    
    /**
     * Wait random amount of milliseconds up to limit
     * 
     * @param amount
     * @return boolean
     */
    public boolean waitMilliSeconds(int amount) {
        amount = this.getRandInt(amount) + 1;
        try {
            TimeUnit.MILLISECONDS.sleep(amount);
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }
    
    
    /**
     * Fetch this random instance
     * 
     * @return Random
     */
    public Random getRand() {
        return this.getRand();
    }
    
    public static DateFormat getDATE_FORMAT() {
        return DATE_FORMAT;
    }

    public int getEXPIRATION_DAYS() {
        return EXPIRATION_DAYS;
    }

    public Charset getCharSet() {
        return charSet;
    }
}
