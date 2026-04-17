/*
 * Copyright 2026 Bren.
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
package org.tasktide.api.services.graphql.inputs;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Description;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.builders.MetricDataBuilder;
import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricType;

import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link MetricData}
 *
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-MetricData")
public class MetricDataInput {

    private final MetricDataBuilder metricDataBuilder = new MetricDataBuilder();
    
    public String id, label, units;
    
    @Description("Epoch time metric was measured")
    public long timestamp;
    
    public double total, available, used;
    
    @Description("Metric being profiled:\tCPU, RAM etc")
    public MetricType metricType;
    
    @Description("Annotation field")
    public String anno;
    
    
    /**
     * Parse the JSON string annotation field
     *  to {@link CustomAnnotation}
     * 
     * @return {@link CustomAnnotation}
     */
    public CustomAnnotation parseAnnotation() {
        return JsonUtils.fromJson(anno, CustomAnnotation.class);
    }
    
    
    public MetricData asMetricData() {
        return metricDataBuilder
            .withId(id)
            .withLabel(label)
            .withTimestamp(timestamp)
            .withTotal(total)
            .withAvailable(available)
            .withUsed(used)
            .withMetricType(metricType)
            .withAnnotation(this.parseAnnotation())
        .build();
    }
}