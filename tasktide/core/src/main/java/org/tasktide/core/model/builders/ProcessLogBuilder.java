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
package org.tasktide.core.model.builders;

import org.tasktide.core.model.task.ProcessLog;


/**
 * 
 * Allow ProcessLog objects to be built where all fields are optional
 *
 * @author bkenna
 */
public class ProcessLogBuilder extends ModelBuilder {
    
    // Attributes
    private String id = "";
    private String[] stdout, stderr = null;
    
    
    public ProcessLogBuilder() {
        super();
    }
    
    
    /**
     * Add id field
     * 
     * @param id
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    
    /**
     * Add stdout field
     * 
     * @param stdout
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder stdout(String[] stdout) {
        this.stdout = stdout;
        return this;
    }
    
    
    /**
     * Add stderr field
     * 
     * @param stderr
     * @return {@link ProcessLogBuilder}
     */
    public ProcessLogBuilder stderr(String[] stderr) {
        this.stderr = stderr;
        return this;
    }
    
    
    /**
     * Construct ProcessLog from provided fields
     * 
     * @return {@link ProcessLogBuilder}
     */
    @Override
    public ProcessLog build() {
        return new ProcessLog(id, stdout, stderr);
    }
}
