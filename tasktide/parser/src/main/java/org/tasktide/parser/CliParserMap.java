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
package org.tasktide.parser;

import java.util.Arrays;
import java.util.List;

import org.tasktide.parser.model.Argument;


/**
 * Deprecated in favour of {@link CliParser}
 * 
 * @author bkenna
 */
public class CliParserMap {

    // Attributes
    private final Arguments args;
    private final String[] argIn;

    
    /**
     * Construct command-line argument parser
     * 
     * @param argIn
     * @param args
     */
    public CliParserMap(String[] argIn, Arguments args) {
        this.args = args;
        this.argIn = argIn;
    }

    
    /**
     * Parse defined arguments, or return help string
     * 
     * @return List-String
     */
    public List<String> parseArguments() {
        
        // Check whether help flag was raised
        boolean helpFlag = Arrays.stream(argIn)
            .anyMatch( 
                elm -> elm.contains("-h")
        );
        if (helpFlag) {
            return this.args.getHelp();
        }
        
        // Parse global arguments
        for (Argument<?> arg : this.args.getGlobalArguments().getArgMap().values()) {
            String val = this.fetchRawValueFromArr(arg);
            if (val != null) {
                arg.parseValue(val);
            }
        }

        // Parse action arguments
        for (String action : this.args.getActionMap().keySet()) {
            for (Argument<?> arg : this.args.getActionArguments(action).getArgMap().values()) {
                String val = this.fetchRawValueFromArr(arg);
                if (val != null) {
                    arg.parseValue(val);
                }
            }
        }
        return null;
    }

    
    /**
     * Fetch the raw value for an argument from args arr
     * 
     * @param arg
     * @return String
     */
    private String fetchRawValueFromArr(Argument<?> arg) {
        int index = indexOf(arg);
        if (index < 0) {
            return null;
        }

        String current = this.argIn[index];
        if ( current.contains("=") ) {
            return current.split("=")[1];
        }

        // Fetch all values up to next arg
        int j = index + 1;
        boolean hitNext = false;
        StringBuilder result = new StringBuilder();
        while (j < this.argIn.length && !hitNext) {
            String active = this.argIn[j];
            if (active.startsWith("-")) {
                hitNext = true;
            } else {
                if ( result.length() > 0 ) {
                    result.append(",");
                }
                result.append(active);
                j++;
            }
        }
        
        // Return result
        return result.toString();
    }

    
    /**
     * Fetch index of {@link Argument} in args[]
     * 
     * @param arr
     * @param arg
     * @return int
     */
    private int indexOf(Argument<?> arg) {
        for (int i = 0; i < this.argIn.length; i++) {
            String current = this.argIn[i];
            if (matchToArg(current, arg)) {
                return i;
            }
        }
        return -1;
    }

    
    /**
     * Check whether queried matches either the short or
     *   long flag for provided {@link Argument}
     * 
     * @param query
     * @param arg
     * @return boolean
     */
    private boolean matchToArg(String query, Argument<?> arg) {
        if (arg.getShortFlag().equals(query)) {
            return true;
        } else if (arg.getLongFlag().equals(query)) {
            return true;
        }
        return false;
    }
}