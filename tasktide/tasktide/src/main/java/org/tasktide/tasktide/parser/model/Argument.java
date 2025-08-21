/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.tasktide.tasktide.parser.model;


/**
 *
 * @author bkenna
 */
public class Argument<T> {

    // Attributes
    private T value;
    private Class<T> refClass;
    private final String name, shortFlag, longFlag, description;
    private final ArgumentType argType;

    
    /**
     * Construct defining value later
     *
     * @param name
     * @param description
     * @param shortFlag
     * @param longFlag
     * @param argType
     */
    public Argument(
        String name,
        String description,
        String shortFlag,
        String longFlag,
        ArgumentType argType
    ) {
        this.name = name;
        this.description = description;
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
        this.argType = argType;
    }

    /**
     * Construct with reference class
     *
     * @param name
     * @param description
     * @param shortFlag
     * @param longFlag
     * @param argType
     * @param clazz
     */
    public Argument(
        String name,
        String description,
        String shortFlag,
        String longFlag,
        ArgumentType argType,
        Class<T> clazz
    ) {
        this.name = name;
        this.description = description;
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
        this.argType = argType;
        this.refClass = clazz;
    }
    
    /**
     * Construct with value
     *
     * @param value
     * @param name
     * @param description
     * @param shortFlag
     * @param longFlag
     * @param argType
     * @param clazz
     */
    public Argument(
        T value,
        String name,
        String description,
        String shortFlag,
        String longFlag,
        ArgumentType argType,
        Class<T> clazz
    ) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
        this.argType = argType;
        this.refClass = clazz;
    }

    
    /**
     * Set value if undefined
     *
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    
    /**
     * Get value
     *
     * @return T
     */
    public T getValue() {
        return this.value;
    }

    
    /**
     * Get the reference {@link Class} of this argument
     *
     * @return {@link Class}
     */
    public Class<T> getRefClass() {
        return this.refClass;
    }
    
    
    /**
     * Sets reference class
     * 
     * @param refClass 
     */
    public void setRefClass(Class<T> refClass) {
        this.refClass = refClass;
    }

    
    /**
     * Get argument name
     * 
     * @return String
     */
    public String getName() {
        return this.name;
    }

    
    /**
     * Get argument description
     * 
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    
    /**
     * Get argument short flag
     * 
     * @return String
     */
    public String getShortFlag() {
        return this.shortFlag;
    }

    
    /**
     * Get argument long flag
     * 
     * @return String
     */
    public String getLongFlag() {
        return this.longFlag;
    }

    
    /**
     * Get {@link ArgumentType}
     * 
     * @return {@link ArgumentType}
     */
    public ArgumentType getArgumentType() {
        return this.argType;
    }

        
    /**
     * Set argument through parsing of raw input string
     * 
     * @param raw 
     */
    public void parseValue(String raw) {
        Object val;
        if (refClass == Integer.class) {
            val = Integer.valueOf(raw);
        } else if (refClass == Double.class) {
            val = Double.valueOf(raw);
        } else if (refClass == Long.class) {
            val = Long.valueOf(raw);
        } else if (refClass == Boolean.class) {
            val = Boolean.valueOf(raw);
        } else if (refClass == String.class) {
            val = raw;
        } else if (refClass == Character.class) {
            val = raw.charAt(0);
        } else if (refClass == Boolean.class || refClass == boolean.class)  {
            val = true;
        } else {
            throw new IllegalArgumentException("Unsupported Type:\t" + refClass);
        }
        this.setValue(refClass.cast(val));
    }

    
    @Override
    public String toString() {
        return "Argument{" + 
            "value=" + value + 
            ", refClass=" + refClass +
            ", name=" + name + 
            ", shortFlag=" + shortFlag +
            ", longFlag=" + longFlag +
            ", description=" + description +
            ", argType=" + argType +
        '}';
    }
}