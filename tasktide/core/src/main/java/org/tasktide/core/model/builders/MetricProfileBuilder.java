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
import org.tasktide.core.model.job_env.metrics.MetricProfile;
import org.tasktide.core.model.job_env.metrics.MetricType;
import org.tasktide.core.model.job_env.metrics.ProfileData;

// For JavaDocs
import org.tasktide.core.model.job_env.JobEnvironment;


/**
 * {@link MetricProfile} builder
 *
 * @author Brendan Kenna
 */
public class MetricProfileBuilder extends ModelBuilder<MetricProfile> {

    // Attributes
    private String id, label, units, jobEnvId;
    private long timestamp;
    private float meanTotal, meanAvailable, meanUsed;
    private ProfileData profile;
    private MetricType type;
    private CustomAnnotation anno;

    
    /**
     * Construct
     */
    public MetricProfileBuilder() {
        super();
    }

    
    /**
     * Adds Id field
     * 
     * @param id
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withId(String id) {
        this.id = id;
        return this;
    }

    
    /**
     * Adds label field
     * 
     * @param label
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    
    /**
     * Adds timestamp field
     * 
     * @param timestamp
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    
    /**
     * Adds mean total field
     * 
     * @param meanTotal
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withMeanTotal(float meanTotal) {
        this.meanTotal = meanTotal;
        return this;
    }

    
    /**
     * Adds mean available field
     * 
     * @param meanAvailable
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withMeanAvailable(float meanAvailable) {
        this.meanAvailable = meanAvailable;
        return this;
    }

    
    /**
     * Adds mean used field
     * 
     * @param meanUsed
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withMeanUsed(float meanUsed) {
        this.meanUsed = meanUsed;
        return this;
    }

    
    /**
     * Adds {@link ProfileData} field
     * 
     * @param profile
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withProfile(ProfileData profile) {
        this.profile = profile;
        return this;
    }

    
    /**
     * Add units field
     * 
     * @param units
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withUnits(String units) {
        this.units = units;
        return this;
    }

    
    /**
     * Adds {@link MetricType} field
     * 
     * @param type
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withType(MetricType type) {
        this.type = type;
        return this;
    }

    
    /**
     * Adds {@link JobEnvironment} Id field
     * 
     * @param jobEnvId
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withJobEnvId(String jobEnvId) {
        this.jobEnvId = jobEnvId;
        return this;
    }

    
    /**
     * Adds {@link CustomAnnotation} field
     * 
     * @param anno
     * @return {@link MetricProfileBuilder}
     */
    public MetricProfileBuilder withAnnotation(CustomAnnotation anno) {
        this.anno = anno;
        return this;
    }

    
    /**
     * Builds {@link MetricProfile}
     * 
     * @return {@link MetricProfile}
     */
    @Override
    public MetricProfile build() {
        return new MetricProfile(
            id,
            label,
            timestamp,
            units,
            meanTotal,
            meanAvailable,
            meanUsed,
            profile,
            type,
            jobEnvId,
            anno
        );
    }
}