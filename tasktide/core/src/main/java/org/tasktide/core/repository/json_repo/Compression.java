/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository.json_repo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Class to support json file I/O (de)compression
 * 
 * @author bkenna
 */
public class Compression {
    
    public Compression(){}
    
    
    /**
     * GZIP input string
     * 
     * @param input
     * @return - Compress string
     * @throws IOException 
     */
    public byte[] compress(String input) throws IOException {
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(byteArr)) {
            gzip.write(input.getBytes(StandardCharsets.UTF_8));
        }
        return byteArr.toByteArray();
    }
    
    
    /**
     * Compress a database of JSON Arrays
     * 
     * @param input
     * @return
     * @throws IOException 
     */
    public Map<String, byte[]> compressDB(Map<String, JsonArray> input) throws IOException {
        Map<String, byte[]> output = new HashMap<>();
        for (Entry<String, JsonArray> entry : input.entrySet()) {
            String json = entry.getValue().toString(); // Convert JsonArray to string
            byte[] compressed = compress(json);
            output.put(entry.getKey(), compressed);
        }
        return output;
    }
    
    
    /**
     * Zip the compressed database to target file
     * 
     * @param database
     * @param targetFile
     * @return 
     */
    public boolean zipDatabase(Map<String, byte[]> database, String targetFile) {
        try (
            FileOutputStream fileOut = new FileOutputStream(targetFile);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fileOut))
        ) {
            for (Entry<String, byte[]> table : database.entrySet()) {
                ZipEntry zipElm = new ZipEntry(table.getKey());
                zipElm.setSize(table.getValue().length);
                zipOut.putNextEntry(zipElm);
                zipOut.write(table.getValue());
                zipOut.flush();
                zipOut.closeEntry();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    
    /**
     * Decompress input string
     * 
     * @param compressedString
     * @return
     * @throws IOException 
     */
    public String decompress(byte[] compressedString) throws IOException {
        try (
            GZIPInputStream gzipInpStream = new GZIPInputStream(new ByteArrayInputStream(compressedString));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInpStream, StandardCharsets.UTF_8))
        ) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        }
    }
    
    
    /**
     * Unzip database, providing the compressed byte arrays
     * 
     * @param databaseFile
     * @return 
     */
    public Map<String, byte[]> unzipDatabase(String databaseFile) {
        Map<String, byte[]> output = new HashMap<>();
        try (
            FileInputStream fileIn = new FileInputStream(databaseFile);
            ZipInputStream zipIn = new ZipInputStream(fileIn)
        ) {
            ZipEntry zipElm;
            while ((zipElm = zipIn.getNextEntry()) != null) {
                byte[] tableData = zipIn.readAllBytes();
                output.put(zipElm.getName(), tableData);
                zipIn.closeEntry();
            }
        } catch (Exception ex) {
            return null;
        }
        return output;
    }
    
    
    /**
     * Unzip database file to JSON Array DB
     * 
     * @param databaseFile
     * @return 
     */
    public Map<String, JsonArray> unzipToJson(String databaseFile) {
        Map<String, JsonArray> output = new HashMap<>();
        try (
            FileInputStream fileIn = new FileInputStream(databaseFile);
            ZipInputStream zipIn = new ZipInputStream(fileIn)
        ) {
            ZipEntry zipElm;
            while ((zipElm = zipIn.getNextEntry()) != null) {
                byte[] tableCompressedData = zipIn.readAllBytes();
                String decompressed = decompress(tableCompressedData);

                // Parse JSON string into JsonArray using JSON-P
                try (JsonReader reader = Json.createReader(new StringReader(decompressed))) {
                    JsonArray tableData = reader.readArray();
                    output.put(zipElm.getName(), tableData);
                }

                zipIn.closeEntry();
            }
        } catch (Exception ex) {
            return null;
        }
        return output;
    }
    
    
    /**
     * Write byte array to target file
     * 
     * @param byteArr
     * @param path
     * @throws IOException 
     */
    public void writeByteArrToFile(byte[] byteArr, String path) throws IOException {
        try (FileOutputStream out = new FileOutputStream(path)) {
            out.write(byteArr);
            out.flush();
        }
    }
    
    
    /**
     * Compress input to file
     * 
     * @param path
     * @param data
     * @return boolean
     */
    public boolean compressToFile(String path, String data) {
        try {
            writeByteArrToFile(compress(data), path);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    
    /**
     * Read byte array from file
     * 
     * @param path
     * @return
     * @throws IOException 
     */
    public byte[] readByteArrFromFile(String path) throws IOException {
        try ( FileInputStream in = new FileInputStream(path) ) {
            return in.readAllBytes();
        }
    }
    
    
    /**
     * Decompress file to string
     * 
     * @param path
     * @return String
     */
    public String decompressFromFile(String path) {
        try {
            return decompress( readByteArrFromFile(path) );
        }
        catch (Exception ex) {
            return null;
        }
    }
}
