/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core.repository;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
// import jakarta.nosql.Id;

// import jakarta.nosql.Convert;
// import org.eclipse.jnosql.databases.mongodb.mapping.ObjectIdConverter;


/**
 *
 * @author bkenna
 */
@Entity("Tunes")
public class Tunes {
    
    private String id;
    
    @Column
    private String name;
    
    @Column
    private String artist;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Tunes() {
    }

    public Tunes(String id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Music{" + "id=" + id + ", name=" + name + ", artist=" + artist + '}';
    }
}
