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
package org.tasktide.core.model.job_env.metrics;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.lang.reflect.Field;

import org.tasktide.core.TaskTideModel;


/**
 * Holds metrics data for {@link MetricType} instances.
 *  These datapoints are annotated with a Label to denote
 *  any related collections (ex storage), and a Timestamp
 *  when metric was calculated.
 * 
 * @author Brendan Kenna
 */
@jakarta.nosql.Entity("MetricData")
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "MetricData")
public class MetricData implements TaskTideModel<MetricData> {
    
    // Metric Identifier
    @jakarta.nosql.Id
    @jakarta.persistence.Id
    @JsonbProperty("Id")
    private String id;
    
    // Label for metric
    @jakarta.nosql.Column("Label")
    @jakarta.persistence.Column(name = "Label")
    @JsonbProperty("Label")
    private String label;
    
    // Timestamp for metric
    @jakarta.nosql.Column("Timestamp")
    @jakarta.persistence.Column(name = "Timestamp")
    @JsonbProperty("Timestamp")
    private long timestamp;
    
    // Total value for metric
    @jakarta.nosql.Column("Total")
    @jakarta.persistence.Column(name = "Total")
    @JsonbProperty("Total")
    private double total;
    
    // Avaliable for metric
    @jakarta.nosql.Column("Available")
    @jakarta.persistence.Column(name = "Available")
    @JsonbProperty("Available")
    private double available;
    
    // Used for metric
    @jakarta.nosql.Column("Used")
    @jakarta.persistence.Column(name = "Used")
    @JsonbProperty("Used")
    private double used;
    
    // Units for metric
    @jakarta.nosql.Column("Units")
    @jakarta.persistence.Column(name = "Units")
    @JsonbProperty("Units")
    private String units;
    
    // Type of metric data
    @jakarta.nosql.Column("Type")
    @jakarta.persistence.Column(name = "Type")
    @JsonbProperty("Metric Type")
    private MetricType metricType;
    
    
    /**
     * Null constructor
     */
    public MetricData() {}
    
    
    /**
     * Construct with all arguments
     * 
     * @param id
     * @param label
     * @param timestamp
     * @param total
     * @param available
     * @param used
     * @param units
     * @param metricType 
     */
    @JsonbCreator
    public MetricData(
        @JsonbProperty("Id") String id,
        @JsonbProperty("Label") String label,
        @JsonbProperty("Timestamp") long timestamp,
        @JsonbProperty("Total") double total,
        @JsonbProperty("Available") double available,
        @JsonbProperty("Used") double used,
        @JsonbProperty("Units") String units,
        @JsonbProperty("Metric Type") MetricType metricType
    ) {
        this.id = id;
        this.label = label;
        this.timestamp = timestamp;
        this.total = total;
        this.available = available;
        this.used = used;
        this.units = units;
        this.metricType = metricType;
    }

    
    /**
     * Get Id
     * 
     * @return String
     */
    @Override
    public String getId() {
        return id;
    }

    
    /**
     * Set Id
     * 
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
    /**
     * Get label
     * 
     * @return String
     */
    public String getLabel() {
        return this.label;
    }

    
    /**
     * Set label
     * 
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    
    /**
     * Get timestamp
     * 
     * @return long
     */
    public long getTimestamp() {
        return timestamp;
    }

    
    /**
     * Sets timestamp
     * 
     * @param timestamp 
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    
    /**
     * Get total resources
     * 
     * @return double
     */
    public double getTotal() {
        return total;
    }

    
    /**
     * Sets total
     * 
     * @param total 
     */
    public void setTotal(double total) {
        this.total = total;
    }

    
    /**
     * Get available resources
     * 
     * @return 
     */
    public double getAvailable() {
        return available;
    }

    
    /**
     * Sets available resources
     * 
     * @param available 
     */
    public void setAvailable(double available) {
        this.available = available;
    }

    
    /**
     * Get used resources
     * 
     * @return double
     */
    public double getUsed() {
        return used;
    }

    
    /**
     * Sets used resources
     * 
     * @param used 
     */
    public void setUsed(double used) {
        this.used = used;
    }

    
    /**
     * Get units of data point
     * 
     * @return String
     */
    public String getUnits() {
        return units;
    }

    
    /**
     * Set unit of data point
     * 
     * @param units 
     */
    public void setUnits(String units) {
        this.units = units;
    }

    
    /**
     * Get metrics type
     * 
     * @return {@link MetricType}
     */
    public MetricType getMetricType() {
        return metricType;
    }

    
    /**
     * Sets metric type
     * 
     * @param metricType 
     */
    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    
    /**
     * Represent as JSON-B string
     * 
     * @return String
     */
    @Override
    public String toJson() {
        Jsonb json = JsonbBuilder.create();
        return json.toJson(this);
    }
    
    
    /**
     * Represent as JSON-B Document
     * 
     * @return 
     */
    @Override
    public String toJsonDoc() {
        JsonbConfig conf = new JsonbConfig().withFormatting(Boolean.TRUE);
        Jsonb json = JsonbBuilder.create(conf);
        return json.toJson(this);
    }

    
    /**
     * Returns units of the metric data
     * 
     * @return String
     */
    @Override
    public String getState() {
        return this.units;
    }

    
    /**
     * Returns {@link MetricType} as String
     * 
     * @return String
     */
    @Override
    public String getCollection() {
        return this.metricType.name();
    }

    
    /**
     * Nulls model
     * 
     */
    @Override
    public void resetModel() {
        this.total = 0L;
        this.available = 0L;
        this.used = 0L;
    }

    
    /**
     * Represent as String
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "MetricData{" +
            "id=" + id +
            ", label=" + label +
            ", timestamp=" + timestamp +
            ", total=" + total +
            ", available=" + available +
            ", used=" + used +
            ", units=" + units +
            ", metricType=" + metricType +
        '}';
    }
    
    
    /**
     * Get value of queried field
     * 
     * @param field
     * @return Object
     */
    @Override
    public Object getValueFromField(String field) {
        try {
            // Use reflection to get the declared field from this class
            Field declaredField = this.getClass().getDeclaredField(field);
            declaredField.setAccessible(true); // In case the field is private
            Object fieldValue = declaredField.get(this);

            return fieldValue;

        }
        catch (Exception ex) {
            // Optional: Log or rethrow if needed
            return null;
        }
    }
}