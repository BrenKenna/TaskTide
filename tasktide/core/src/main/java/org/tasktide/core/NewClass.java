/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.core;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;


/**
 *
 * @author bkenna
 */
@Entity
public class NewClass {
    @Id
    private String id;
        
    @Column
    private String columnOne;
    
    public NewClass(){}
}
