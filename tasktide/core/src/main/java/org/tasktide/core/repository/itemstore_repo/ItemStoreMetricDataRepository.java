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
package org.tasktide.core.repository.itemstore_repo;


import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.repository.ItemStoreRepository;
import org.tasktide.itemstore.ItemStore;


/**
 * {@link MetricData} {@link TaskTideRepository} backed by RocksDB
 *
 * @author Brendan Kenna
 */
public class ItemStoreMetricDataRepository extends ItemStoreRepository<MetricData> {
    
    /**
     * Construct {@link MetricData} repository with {@link ItemStore}
     * 
     * @param itemStore
     * @param collectionName
     */
    public ItemStoreMetricDataRepository(
       ItemStore itemStore,
       String collectionName
    ) {
        super(itemStore, MetricData.class, collectionName);
    }
}