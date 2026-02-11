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
package org.tasktide.mutex.strategy;

import java.nio.file.Path;


/**
 *
 * @author Brendan Kenna
 */
public class ElectionState {
    
    // Attributes
    private int pos = -1;
    private int lastPos = -1;
    private int streak = 0;
    private Path predecessor = null;
    private Path leader = null;
    
    
    public ElectionState() {}
    
    
    /**
     * Construct election state
     * 
     * @param pos
     * @param lastPos
     * @param streak
     * @param predecessor
     * @param leader
     */
    public ElectionState(int pos, int lastPos, int streak, Path predecessor, Path leader) {
        this.pos = pos;
        this.lastPos = lastPos;
        this.streak = streak;
        this.predecessor = predecessor;
        this.leader = leader;
    }
    
    
    /**
     * Reset state
     * 
     */
    public void reset() {
        this.pos = -1;
        this.lastPos = -1;
        this.streak = -1;
        this.predecessor = null;
        this.leader = null;
    }
    

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLastPos() {
        return lastPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public Path getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Path predecessor) {
        this.predecessor = predecessor;
    }

    public Path getLeader() {
        return leader;
    }

    public void setLeader(Path leader) {
        this.leader = leader;
    }
    
    
    /**
     * Represent as string
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "ElectionState{" +
            "pos=" + pos +
            ", lastPos=" + lastPos +
            ", streak=" + streak +
            ", predecessor=" + predecessor +
        '}';
    }
}