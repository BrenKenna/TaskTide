/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.supporting;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Class to support various actions
 * 
 * @author bkenna
 */
public class Utils {
    
    // Attributes
    private final Random rand;
    private final Charset charSet;
    private final DateUtility dateUtils;
    
    /**
     * Construct with a task generator and random
     * 
     * @param dateFormat
     * @param expiration
     */
    public Utils(
       @ConfigProperty(name = "task-tide.core.utils.date-format", defaultValue = "dd/MM/yy HH:mm:ss") String dateFormat,
       @ConfigProperty(name = "task-tide.core.utils.expiration", defaultValue = "4") int expiration
    ) {
        this.rand = new Random();
        this.dateUtils = new DateUtility(dateFormat, expiration);
        this.charSet = StandardCharsets.UTF_8;
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
        nowDate = dateUtils.getNowString();
        salt = generateSalt();
        token = nowDate + "-" + salt;
        
        // Return string as hexdecimal
        return convertStringToHex(token);
    }
    
    
    /**
     * Generate base64 encode token
     * 
     * @return String
     */
    public String generateBase64Token() {
        
        // Initialize vars
        String nowDate, salt, token;
        
        // Create token string
        nowDate = dateUtils.getNowString();
        salt = generateSalt();
        token = nowDate + "-" + salt;
        
        // Return string as hexdecimal
        return Base64.getEncoder().encodeToString(token.getBytes());
    }
    
    
    /**
     * Decode base64 encode token
     * 
     * @param token
     * @return 
     */
    public String decodeBase64Token(String token) {
        return new String(Base64.getDecoder().decode(token.getBytes()));
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
        String output;
        try {

            // Generate digest for salted password
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            String toHash = input + "-" + salt;
            messageDigest.update(toHash.getBytes(StandardCharsets.UTF_8));

            // Parse digest to hexadecimal string
            byte[] digest = messageDigest.digest();
            output = getHexString(digest);
            messageDigest.reset();
            return output;
            
        }
        catch (NoSuchAlgorithmException ex) {
            return null;
        }
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
     * @return {@link Random Random}
     */
    public Random getRand() {
        return this.rand;
    }

    
    /**
     * Get char set
     * 
     * @return Charset
     */
    public Charset getCharSet() {
        return charSet;
    }
    
    
    /**
     * Get the date utility
     * 
     * @return {@link DateUtility DateUtility}
     */
    public DateUtility getDateUtility() {
        return this.dateUtils;
    }
}
