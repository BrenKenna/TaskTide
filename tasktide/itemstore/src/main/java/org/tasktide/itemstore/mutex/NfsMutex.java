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
package org.tasktide.itemstore.mutex;

import org.tasktide.itemstore.mutex.exceptions.MutexCheckedException;
import org.tasktide.itemstore.mutex.model.MutexState;
import org.tasktide.itemstore.mutex.utils.MutexLabellingUtils;
import org.tasktide.itemstore.mutex.model.HostLock;
import org.tasktide.itemstore.mutex.model.Mutex;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author Brendan Kenna
 */
public class NfsMutex implements InterProcessMutex {

    // Attributes
    private Mutex mutex;
    private final String procId, nodeProcId;
    private HostLock hostLock;
    private final Random RAND;
    
    
    /**
     * Construct
     * 
     */
    public NfsMutex() {
        this.procId = MutexLabellingUtils.getInstanceId();
        this.nodeProcId = MutexLabellingUtils.getNodeProcId();
        RAND = new Random();
    }

    
    /**
     * Get current state
     * 
     * @return {@link MutexState}
     */
    @Override
    public MutexState getState() {
        return this.mutex.getState();
    }

    
    /**
     * Set new {@link MutexState}
     * 
     * @param newState 
     */
    @Override
    public void setState(MutexState newState) {
        this.mutex.setState(newState);
    }
    
    
    @Override
    public void acquire() throws MutexCheckedException {
        
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws MutexCheckedException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void release() throws MutexCheckedException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean lockedByActiveHost() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean lockedByActiveProcess() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int inferPosition() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int queueSize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}