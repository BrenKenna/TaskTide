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
package org.tasktide.core.manager.generator;

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.supporting.Utils;


/**
 *
 * Generating seq tasks
 * 
 * @author bkenna
 */
public class SeqGenerator {
    
    private final Utils utils;
    
    public SeqGenerator() {
        utils = new Utils("dd/MM/yy HH:mm:ss", 4);
    }

    public Map<String, String> generateCmd() {
        Map<String, String> output = new HashMap<>();
        int limit = utils.getRandInt(100);
        String cmd = "seq " + limit;
        output.put("Task Name", "Seq " + limit);
        output.put("Task Script", cmd);
        return output;
    }
}
