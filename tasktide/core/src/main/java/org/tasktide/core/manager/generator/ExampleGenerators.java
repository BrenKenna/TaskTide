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

import org.tasktide.core.manager.ManagerTask;


/**
 * Enum of valid task types
 * 
 * @author bkenna
 */
public enum ExampleGenerators {
    
    PING {
        @Override
        public ManagerTask createTask() {
            ExampleGenerator pingGen = new PingGenerator();
            return pingGen.createTask();
        }
    },
    
    SEQ {
        @Override
        public ManagerTask createTask() {
            ExampleGenerator seqGen = new SeqGenerator();
            return seqGen.createTask();
        }
    },
    
    NSLOOKUPS {
        @Override
        public ManagerTask createTask() {
            ExampleGenerator nsGen = new NsLookupGenerator();
            return nsGen.createTask();
        }
    },
    
    HOSTNAME {
        @Override
        public ManagerTask createTask() {
            ExampleGenerator hostnameGen = new HostnameGenerator();
            return hostnameGen.createTask();
        }
    };
    
    
    /**
     * Abstract method to generate a task
     * 
     * @return ManagerTask
     */
    public abstract ManagerTask createTask();
}
