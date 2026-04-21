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
package org.tasktide.api.hateoas.links;

import jakarta.inject.Inject;

import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;

import jakarta.annotation.Priority;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tasktide.api.hateoas.resource_map.ResourceMap;
import org.tasktide.api.hateoas.resource_map.ResourceBinding;
import org.tasktide.api.hateoas.resource_map.ResourceEnvelope;


/**
 * Intercept response objects and apply {@link ResourceEnvelope}
 *
 * @author Bren
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class HateoasFilter implements ContainerResponseFilter {

    // Building links
    @Inject
    LinkService linkService;
    
    @Context
    ResourceInfo resourceInfo;
    
    // Pattern match for templating links
    private static final Pattern TEMPLATE_PATTERN =
        Pattern.compile("\\{(\\w+)}");

    
    /**
     * Intercept response objects and apply {@link ResourceEnvelope}
     * 
     * @param req
     * @param resp 
     */
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext resp) {
        
        // Initialize required variables
        Object entity;
        ResourceEnvelope<?> wrapped;
        
        // Do nothing on request errors
        if ( resp.getStatus() >= 200 || resp.getEntity() == null ) {
            return;
        }
        
        // Proceed if wrapped
        entity = resp.getEntity();
        if ( entity instanceof ResourceEnvelope<?> existing ) {
            wrapped = existing;
        }
        
        // Otherwise wrap
        else {
            wrapped = ResourceEnvelope.of(entity);
            resp.setEntity(wrapped);
        }
        
        
        // Avoid duplicate processing
        if ( !wrapped.getLinks().isEmpty() ) {
            return;
        }
        
        // Otherwise fetch annotations
        Class<?> resourceClass = resourceInfo.getResourceClass();
        ResourceBinding binding = resourceClass.getAnnotation(ResourceBinding.class);
        if ( binding == null ) {
            return;
        }
        
        // Fetch resource map if defined
        ResourceMap resourceMap = this.instantiate(binding.value());
        UriInfo uriInfo = req.getUriInfo();
        
        // Apply links to active response
        for ( Map.Entry<String, LinkTemplate> entry : resourceMap.getLinks().entrySet() ) {
            String rel = entry.getKey();
            LinkTemplate template = entry.getValue();
            
            String resolved = this.resolvePath(template.getPath(), wrapped.getData());
            String href = this.linkService.buildUri(req, uriInfo, resolved);
            wrapped.addLink(rel, this.buildLink(href, template));
        }
    }
    
    
    /**
     * Build link for response
     * 
     * @param href
     * @param tmpl
     * 
     * @return Map-String, Object
     */
    private Map<String, Object> buildLink(String href, LinkTemplate tmpl) {
        Map<String, Object> link = new java.util.LinkedHashMap<>();
        link.put("href", href);
        if (tmpl.isTemplated()) {
            link.put("templated", true);
        }
        return link;
    }
    
    /**
     * Construct {@link ResourceMap} from annotation
     * 
     * @param clazz
     * @return {@link ResourceMap}
     */
    private ResourceMap instantiate(Class<? extends ResourceMap> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /**
     * Populate templated link, matches path parameter
     *  template key to a value inside the corresponding
     *  data model. For instance /path/{id}, expects
     *  DataModel.id as a field
     * 
     * @param path
     * @param dto
     * @return String
     */
    private String resolvePath(String path, Object dto) {

        Matcher matcher = TEMPLATE_PATTERN.matcher(path);

        return matcher.replaceAll(match -> {
            String field = match.group(1);
            Object value = extract(dto, field);

            return value != null
                    ? Matcher.quoteReplacement(value.toString())
                    : match.group(0); // leave {id} if missing
        });
    }
    
    
    
    /**
     * Fetch field from provided object
     * 
     * @param dto
     * @param fieldName
     * 
     * @return Object
     */
    private Object extract(Object dto, String fieldName) {
        try {
            var field = dto.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(dto);
        } catch (Exception e) {
            return null;
        }
    }
}