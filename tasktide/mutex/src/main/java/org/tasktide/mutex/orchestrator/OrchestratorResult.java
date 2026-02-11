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
package org.tasktide.mutex.orchestrator;



/**
 *
 * @author Brendan Kenna
 */
public class OrchestratorResult {
 
    // Attributes
    private boolean success;
    private OrchestratorStatus status;
    private OrchestratorAction action;
    private Exception exception;
    private int attemptCount;
    
    
    /**
     * Default constructor
     */
    public OrchestratorResult() {}
    
    
    /**
     * Construct with data
     * 
     * @param success
     * @param status
     * @param action
     * @param exception
     */
    public OrchestratorResult(
        boolean success, OrchestratorStatus status,
        OrchestratorAction action, Exception exception
    ) {
        this.success = success;
        this.status = status;
        this.action = action;
        this.exception = exception;
    }
    
    
    /**
     * 
     * @param success
     * @param status
     * @param action 
     */
    public OrchestratorResult(
        boolean success, OrchestratorStatus status,
        OrchestratorAction action
    ) {
        this.success = success;
        this.status = status;
        this.action = action;
    }
    

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public OrchestratorStatus getStatus() {
        return status;
    }

    public void setStatus(OrchestratorStatus status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public OrchestratorAction getAction() {
        return action;
    }

    public void setAction(OrchestratorAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "OrchestratorResult{" +
            "success=" + success +
            ", status=" + status +
            ", action=" + action +
            ", exception=" + exception +
            ", attemptCount=" + attemptCount +
        '}';
    }
}