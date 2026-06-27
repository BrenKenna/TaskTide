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
package org.tasktide.api.auth;

import com.nimbusds.jwt.JWTClaimsSet;


/**
 * Interface through which authentication
 *  is verified. Nimbus performs OIDC cryptographic
 *  verification, and input properties. Letting
 *  endpoints to focus on evaluating roles.
 *
 * @author Bren
 */
public interface AuthenticationScheme {
    
    
    /**
     * Provide {@link AuthenticationSchemeType} of implementing class
     * 
     * @return {@link AuthenticationSchemeType}
     */
    public AuthenticationSchemeType getSchemeType();
    
    
    /**
     * Verify authenticity of input
     * 
     * @param input
     * @return {@link AuthPrincipal}
     * 
     * @throws AuthenticationException 
     */
    public AuthPrincipal authenticate(Object input) throws AuthenticationException;
    
    
    /**
     * Map {@link JWTClaimsSet} to {@link AuthPrincipal}
     * 
     * @param input
     * @return {@link AuthPrincipal}
     */
    public AuthPrincipal mapToAuthPrincipal(Object input);
    
    
    /**
     * Validate JWT input properties
     * 
     * @param input
     * @return boolean
     */
    public boolean validate(Object input);
    
    
    /**
     * Verify issuer of HWT
     * 
     * @param input
     * @return boolean
     */
    public boolean verifiyIssuer(Object input);
    
    
    /**
     * Verify audience of JWT
     * 
     * @param input
     * @return boolean
     */
    public boolean verifyAudience(Object input);

    
    /**
     * Verify expiration of JWT
     * 
     * @param input
     * @return boolean
     */
    public boolean verifyExpiration(Object input);
}