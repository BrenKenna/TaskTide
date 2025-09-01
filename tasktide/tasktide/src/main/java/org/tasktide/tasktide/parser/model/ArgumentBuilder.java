/*
 * Copyright 2025 Brendan Kenna.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tasktide.tasktide.parser.model;


/**
 * For building {@link Argument}
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
     * Build with reference class
     * 
     * @param <T>
     * @param refClass
     * @return {@link ArgumentBuilder}
     */
    public <T> ArgumentBuilder withRefClass(Class<T> refClass) {
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
        
        else if ( this.refClass != null ) {
            return new Argument<>(name, description, shortFlag, longFlag, argType);
        }
        return new Argument<>(name, description, shortFlag, longFlag, argType);
    }
}
