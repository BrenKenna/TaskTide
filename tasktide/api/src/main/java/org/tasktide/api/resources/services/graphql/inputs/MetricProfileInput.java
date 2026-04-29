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
package org.tasktide.api.resources.services.graphql.inputs;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Description;

import org.tasktide.core.model.CustomAnnotation;

import org.tasktide.core.model.builders.MetricProfileBuilder;
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.model.job_env.metrics.ProfileData;

import org.tasktide.core.supporting.JsonUtils;


/**
 * Data model for GraphQL for {@link MetricProfile}
 *
 * @author Bren
 */
@Input
@Description("GraphQL Data Model for TaskTide-MetricProfile")
public class MetricProfileInput {

    private final MetricProfileBuilder metricProfileBuilder = new MetricProfileBuilder();
    
    public String id, label, units, jobEnvId;
    
    @Description("Epoch time metric profile was measured")
    public long timestamp;
    
    public float meanTotal, meanAvailable, meanUsed;
    
    @Description("Dataset of metric profile")
    public String profileData;
    
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
    
    
    /**
     * Parse the JSON string annotation field
     *  to {@link ProfileData}
     * 
     * @return {@link ProfileData}
     */
    public ProfileData parseProfileData() {
        return JsonUtils.fromJson(profileData, ProfileData.class);
    }
    
    
    /**
     * Represent as {@link MetricProfile}
     * 
     * @return {@link MetricProfile}
     */
    public MetricProfile asMetricProfile() {
        return this.metricProfileBuilder
            .withId(id)
            .withLabel(label)
            .withTimestamp(timestamp)
            .withUnits(units)
            .withMeanTotal(meanTotal)
            .withMeanAvailable(meanAvailable)
            .withMeanUsed(meanUsed)
            .withProfile(this.parseProfileData())
            .withType(metricType)
            .withJobEnvId(jobEnvId)
            .withAnnotation(this.parseAnnotation())
        .build();
    }
}