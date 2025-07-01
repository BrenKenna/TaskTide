/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser.model;

/**
 *
 * @author bkenna
 */
public class ArgumentBuilder {
    
    // Attributes
    private String name, shortFlag, longFlag, description;
    private ArgumentType argType;
    
    // Optional properties handled generically
    private boolean withValue;
    private Object value;
    private Class<?> refClass;
    
    
    /**
     * Build with name
     * 
     * @param name
     * @return {@link ArgumentBuilder}
     */
    public ArgumentBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    
    /**
     * Build with short flag for argument
     * 
     * @param shortFlag
     * @return {@link ArgumentBuilder}
     */
    public ArgumentBuilder withShortFlag(String shortFlag) {
        this.shortFlag = shortFlag;
        return this;
    }
    
    
    /**
     * Build with long flag for argument
     * 
     * @param longFlag
     * @return {@link ArgumentBuilder}
     */
    public ArgumentBuilder withLongFlag(String longFlag) {
        this.longFlag = longFlag;
        return this;
    }
    
    
    /**
     * Build with ddescription
     * 
     * @param description
     * @return {@link ArgumentBuilder}
     */
    public ArgumentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    
    /**
     * Build with {@link ArgumentType}
     * 
     * @param argType
     * @return {@link ArgumentBuilder}
     */
    public ArgumentBuilder withArgType(ArgumentType argType) {
        this.argType = argType;
        return this;
    }
    
    
    /**
     * Build value and reference class
     * 
     * @param <T>
     * @param value
     * @param refClass
     * @return {@link ArgumentBuilder}
     */
    public <T> ArgumentBuilder withValue(T value, Class<T> refClass) {
        this.withValue = true;
        this.value = value;
        this.refClass = refClass;
        return this;
    }
    
    
    /**
     * Build {@link Argument} of required type
     * 
     * @param <T>
     * @return {@link Argument}
     */
    @SuppressWarnings("unchecked")
    public <T> Argument<T> build() {
        if ( this.withValue ) {
            return new Argument<>((T) value, name, description, shortFlag, longFlag, argType, (Class<T>) refClass);
        }
        return new Argument<>(name, description, shortFlag, longFlag, argType);
    }
}
