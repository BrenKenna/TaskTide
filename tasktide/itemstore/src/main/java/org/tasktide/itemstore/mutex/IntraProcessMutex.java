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
package org.tasktide.itemstore.mutex;

import org.tasktide.itemstore.mutex.model.HostLock;


/**
 * Uses {@link HostLock} data model to conduct
 *  package private OS file lock with the same
 *  semantics as the inter process mutex
 * 
 * @author Brendan Kenna
 */
public abstract class IntraProcessMutex implements MutexElection {

    
}