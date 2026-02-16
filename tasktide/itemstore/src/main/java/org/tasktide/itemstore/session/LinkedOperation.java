/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.itemstore.session;


/**
 * Interface adding support for {@link BulkOperation} 
 *  across multiple {@link ItemStoreSession} under "one" connection.
 *  For TaskTide this supports JobEnvironment/Step lookups
 *
 * @param <T>
 * @author Brendan Kenna
 */
@FunctionalInterface
public interface LinkedOperation<T> {
    
    /**
     * Exposes a method enabling executing class
     *  to perform {@link BulkOperation} over two
     *  {@link ItemStoreSession}
     * 
     * @param donor
     * @param recepient
     * @return T
     */
    T execute(ItemStoreSession donor, ItemStoreSession recepient);
}