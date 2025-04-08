/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/AnnotationType.java to edit this template
 */
package org.tasktide.core.repository.qualifiers;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 *
 * Qualifier annotation to distinguish between different implementations of
 * 
 * @author bkenna
 */
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER})
public @interface WorkItemRepoQualifier {
    StorageType type();
}
