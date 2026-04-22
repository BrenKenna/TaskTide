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
package org.tasktide.api;


import jakarta.enterprise.inject.se.SeContainer;
import jakarta.nosql.Template;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

/**
 *
 * @author Bren
 */
public class AbstractJerseyCdiTest extends JerseyTest {

    protected SeContainer container;
    protected Template template;

    @Override
    protected Application configure() {
        container = TestEnvironment.startWeldContainer(
                "microprofile-config.properties",
                getClass()
        );

        template = TestEnvironment.fetchDocumentTemplate(container);

        ResourceConfig config = new ResourceConfig()
                .packages("org.tasktide.api.services.rest")
                .register(org.glassfish.jersey.jackson.JacksonFeature.class);

        return config;
    }

    protected <T> T bean(Class<T> type) {
        return container.select(type).get();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if (container != null) {
            container.close();
        }
    }
}