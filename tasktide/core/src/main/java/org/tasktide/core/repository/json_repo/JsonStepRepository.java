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
package org.tasktide.core.repository.json_repo;

import java.util.List;

import org.tasktide.core.model.collection.Step;
import org.tasktide.core.repository.JsonRepository;


/**
 *
 * JSON File I/O repository for Step
 * 
 * @author bkenna
 */
public class JsonStepRepository extends JsonRepository<Step> {


    /**
     * Construct StepRepository with injectable template and configurable collection name
     * 
     * @param modelCollection
     * @param collectionName task-tide.repository.json.collection.step.name
     */
    public JsonStepRepository(
        List<Step> modelCollection,
        String collectionName
    ) {
        super(modelCollection, Step.class, collectionName);
    }
}
