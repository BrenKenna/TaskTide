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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.JsonWebToken;


/**
 *
 * @author Bren
 */
public class MockJwt implements JsonWebToken {
    
    private String name;
    private final Map<String, Object> claims;

    public MockJwt(String name, Map<String, Object> claims) {
        this.name = name;
        this.claims = claims != null ? claims : new HashMap<>();
    }
    

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getClaimNames() {
        return this.claims.keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String claimName) {
        return (T) claims.get(claimName);
    }

}