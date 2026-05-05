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
package org.tasktide.mutex.bucketing;

import org.tasktide.mutex.utils.MutexLabellingUtils;


/**
 * Bucket key used for hashing
 *
 * @author Bren
 */
public class BucketKey {

    // Fields
    private final String hostLabel;
    private final String runId;
    private final String DELIMITER = ":";
    
    
    /**
     * Construct with using host name, and process
     *  Id and current millisecond time stamp
     * 
     */
    public BucketKey() {
        this.hostLabel = resolveHostLabel();
        this.runId = this.resolveRunId();
    }
    
    
    /**
     * Construct with job identity properties
     * 
     * @param hostLabel
     * @param runId
     */
    public BucketKey(
        String hostLabel,
        String runId
    ) {
        this.hostLabel = hostLabel;
        this.runId = runId;
    }

    
    /**
     * Represent as a colon delimited string
     * 
     * @return String
     */
    public String asBucketKeyString() {
        return this.hostLabel + this.DELIMITER + this.runId;
    }

    
    /**
     * Get host label
     * 
     * @return String
     */
    public String getHostLabel() {
        return this.hostLabel;
    }

    
    /**
     * Get run label
     * 
     * @return String
     */
    public String getRunId() {
        return this.runId;
    }

    
    /**
     * Resolve host name
     * 
     * @return String
     */
    private String resolveHostLabel() {
        return MutexLabellingUtils.getHostIp().getHostAddress().replace(".", "-");
    }
    
    
    /**
     * Resolve run Id
     * 
     * @return String
     */
    private String resolveRunId() {
        long procId = ProcessHandle.current().pid();
        long currentTime = System.currentTimeMillis();
        return procId + this.DELIMITER + currentTime;
    }


    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "JobIdentity{" +
            "hostLabel=" + hostLabel +
            ", runId=" + runId +
        '}';
    }
}