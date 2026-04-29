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
package org.tasktide.test_utils.api;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Bren
 */
public class MockJwtBuilder {

    private String name;
    private final Map<String, Object> claims = new HashMap<>();

    public MockJwtBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MockJwtBuilder withClaim(String key, Object value) {
        claims.put(key, value);
        return this;
    }

    public MockJwt build() {
        return new MockJwt(name, claims);
    }
}