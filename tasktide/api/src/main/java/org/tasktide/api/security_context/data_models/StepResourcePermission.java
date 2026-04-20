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
package org.tasktide.api.security_context.data_models;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import org.tasktide.core.model.CustomAnnotation;
import org.tasktide.core.model.collection.Step;


/**
 * {@link AbstractResourcePermission} typed for {@link Step}
 *
 * @author Bren
 */
@jakarta.nosql.Entity("StepResourcePermission")
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "StepResourcePermission")
public class StepResourcePermission extends AbstractResourcePermission<StepResourcePermission> {

    /**
     * Default constructor
     * 
     */
    public StepResourcePermission() {
        super();
    }
    
    
    /**
     * Construct with attributes
     * 
     * @param id
     * @param type
     * @param subjectId
     * @param permission 
     * @param anno
     */
    @JsonbCreator
    public StepResourcePermission(
        @JsonbProperty("Resource Id") String id,
        @JsonbProperty("Resource Type") String type,
        @JsonbProperty("Subject Id") String subjectId,
        @JsonbProperty("Permission") String permission,
        @JsonbProperty("Annotations") CustomAnnotation anno
    ) {
        super(id, type, subjectId, permission, anno);
    }
}