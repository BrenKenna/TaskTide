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
package org.tasktide.itemstore.mutex;

/**
 *
 * @author Brendan Kenna
 */
public class Timestamps {
    
    private long start;
    private long postLock;
    private long end;

    public Timestamps() {
    }

    public Timestamps(long start, long postLock, long end) {
        this.start = start;
        this.postLock = postLock;
        this.end = end;
    }

    
    
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getPostLock() {
        return postLock;
    }

    public void setPostLock(long postLock) {
        this.postLock = postLock;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Timestamps{" + "start=" + start + ", postLock=" + postLock + ", end=" + end + '}';
    }
}
