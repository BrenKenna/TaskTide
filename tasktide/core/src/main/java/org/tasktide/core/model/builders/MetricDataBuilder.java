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

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricType;


/**
 * Builder class for {@link MetricData}.
 *
 * @author Brendan Kenna
 */
public class MetricDataBuilder extends ModelBuilder<MetricData> {

    // Attributes for building
    private String id, label, units;
    private double total, available, used;
    private MetricType metricType;
    private CustomAnnotation anno;
    private long timestamp;

    /**
     * Construct
     */
    public MetricDataBuilder() {
        super();
    }

    
    /**
     * Adds Id field
     * 
     * @param id
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withId(String id) {
        this.id = id;
        return this;
    }

    
    /**
     * Adds label field
     * 
     * @param label
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    
    /**
     * Adds units field
     * 
     * @param units
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withUnits(String units) {
        this.units = units;
        return this;
    }

    
    /**
     * Adds total field
     * 
     * @param total
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withTotal(double total) {
        this.total = total;
        return this;
    }

    
    /**
     * Adds available field
     * 
     * @param available
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withAvailable(double available) {
        this.available = available;
        return this;
    }

    
    /**
     * Adds used field
     * 
     * @param used
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withUsed(double used) {
        this.used = used;
        return this;
    }

    
    /**
     * Adds timestamp field
     * 
     * @param timestamp
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
    /**
     * Adds metric type
     * 
     * @param metricType
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withMetricType(MetricType metricType) {
        this.metricType = metricType;
        return this;
    }

    
    /**
     * Adds anno
     * 
     * @param anno
     * @return {@link MetricDataBuilder}
     */
    public MetricDataBuilder withAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }

    
    /**
     * Build {@link MetricData}
     *
     * @return {@link MetricData}
     */
    @Override
    public MetricData build() {
        return new MetricData(id, label, timestamp, total, available, used, units, metricType, anno);
    }
}