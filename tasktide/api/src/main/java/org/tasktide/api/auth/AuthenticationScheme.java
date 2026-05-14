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
 *  verification, and token properties. Letting
 *  endpoints to focus on evaluating roles.
 *
 * @author Bren
 */
public interface AuthenticationScheme {
    
    
    /**
     * Provide {@link SchemeType} of implementing class
     * 
     * @return {@link SchemeType}
     */
    public SchemeType getSchemeType();
    
    
    /**
     * Verify authenticity of token
     * 
     * @param token
     * @return {@link AuthPrincipal}
     * 
     * @throws AuthenticationException 
     */
    public AuthPrincipal authenticate(String token) throws AuthenticationException;
    
    
    /**
     * Map {@link JWTClaimsSet} to {@link AuthPrincipal}
     * 
     * @param claims
     * @return {@link AuthPrincipal}
     */
    public AuthPrincipal mapToAuthPrincipal(JWTClaimsSet claims);
    
    
    /**
     * Validate JWT token properties
     * 
     * @param claims
     * @return boolean
     */
    public boolean validate(JWTClaimsSet claims);
    
    
    /**
     * Verify issuer of HWT
     * 
     * @param claims
     * @return boolean
     */
    public boolean verifiyIssuer(JWTClaimsSet claims);
    
    
    /**
     * Verify audience of JWT
     * 
     * @param claims
     * @return boolean
     */
    public boolean verifyAudience(JWTClaimsSet claims);

    
    /**
     * Verify expiration of JWT
     * 
     * @param claims
     * @return boolean
     */
    public boolean verifyExpiration(JWTClaimsSet claims);
}