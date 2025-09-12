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

import java.util.HashMap;
import java.util.Map;

import org.tasktide.core.model.job_env.metrics.MetricData;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.model.job_env.metrics.ProfileData;



/**
 * {@link ProfileData}
 *
 * @author Brendan Kenna
 */
public class ProfileDataBuilder extends ModelBuilder<ProfileData> {

    // Attributes
    private String id;
    private Map<String, MetricData> metricProfile = new HashMap<>();

    
    /**
     * Construct
     */
    public ProfileDataBuilder() {
        super();
    }

    
    /**
     * Adds Id field
     * 
     * @param id
     * @return {@link MetricDataBuilder}
     */
    public ProfileDataBuilder withId(String id) {
        this.id = id;
        return this;
    }

    
    /**
     * Adds Metric Profile field
     * 
     * @param metricProfile
     * @return {@link MetricDataBuilder}
     */
    public ProfileDataBuilder withMetricProfile(Map<String, MetricData> metricProfile) {
        this.metricProfile = metricProfile;
        return this;
    }

    
    /**
     * Build {@link ProfileData}
     * 
     * @return {@link ProfileData}
     */
    @Override
    public ProfileData build() {
        return new ProfileData(id, metricProfile);
    }
}