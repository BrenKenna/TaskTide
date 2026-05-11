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
package org.tasktide.core.model.collection;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.List;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;



/**
 *
 * @author Bren
 */
@Interceptor
@WorkflowLifecycle
@Priority(Interceptor.Priority.APPLICATION)
public class WorkflowLifecycleInterceptor {

    private final Logger LOGGER = LogManager.getLogger(WorkflowLifecycleInterceptor.class);
    
    @AroundInvoke
    public Object applyLifecycle(InvocationContext ctx) throws Exception {

        Object target = ctx.getTarget();

        if (target instanceof Workflow wf) {

            // BEFORE repository calls (save/update)
            if (isWriteOperation(ctx)) {
                wf.setStepIds(
                    WorkflowStepConverter.convertToDatabaseColumn(
                        wf.getWorkflowSteps()
                    )
                );
            }
        }

        Object result = ctx.proceed();

        if (target instanceof Workflow wf2) {

            // AFTER load operations
            if (result instanceof Workflow loaded) {
                loaded.hydrateSteps(); // optional helper
            }

            if (result instanceof List<?> list) {
                list.forEach(obj -> {
                    if (obj instanceof Workflow w) {
                        w.getWorkflowSteps(); // triggers lazy hydration
                    }
                });
            }
        }

        return result;
    }

    private boolean isWriteOperation(InvocationContext ctx) {
        String name = ctx.getMethod().getName();
        LOGGER.info("Workflow Lifecycle Interceptor invoked and evaluating:\t'{}'", name);
        return name.contains("insert")
            || name.contains("update")
            || name.contains("save");
    }
}