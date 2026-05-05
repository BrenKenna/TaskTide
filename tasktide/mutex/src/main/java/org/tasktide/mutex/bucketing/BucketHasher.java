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

import java.nio.file.Path;
import java.util.zip.CRC32;


/**
 * Bucketing based on crc32
 *
 * @author Bren
 */
public class BucketHasher {
    
    // Bucket attributes
    private final Path bucketPrefix;
    private final int bucketLimit;
    
    private final CRC32 crc;
    
    
    /**
     * Construct hasher
     * 
     * @param bucketPrefix
     * @param bucketLimit 
     */
    public BucketHasher(
        Path bucketPrefix,
        int bucketLimit
    ) {
        this.bucketPrefix = bucketPrefix;
        this.bucketLimit = bucketLimit;
        this.crc = new CRC32();
    }
    
    
    /**
     * Calculate bucket for key
     * 
     * @param key
     * 
     * @return long
     */
    public long calculateBucketForKey(BucketKey key) {

        // Initialize vars
        byte[] inputBytes;
        long result;
        
        // Calculate hash
        inputBytes = key.asBucketKeyString().getBytes();
        crc.update(inputBytes);
        result = crc.getValue();
        
        // Reset crc
        crc.reset();
        return Math.floorMod(result, this.bucketLimit);
    }

    
    
    /**
     * Fetch a {@link BucketKey}
     * 
     * @return {@link BucketKey}
     */
    public BucketKey getBucketKey() {
        return new BucketKey();
    }
    
    
    /**
     * Fetch a {@link BucketKey} for hashing
     * 
     * @param hostLabel
     * @param runId
     * 
     * @return {@link BucketKey}
     */
    public BucketKey getBucketKey(String hostLabel, String runId) {
        return new BucketKey(hostLabel, runId);
    }
    
    
    /**
     * Get bucket path for key
     * 
     * @param key
     * @return Path
     */
    public Path getBucketPath(BucketKey key) {
        
        // Calculate bucket key
        long bucket;
        bucket = this.calculateBucketForKey(key);
        
        
        // Place ballot in bucket
        return this.bucketPrefix.resolve( String.valueOf(bucket) );
    }
}