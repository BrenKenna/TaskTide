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

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tasktide.api.utils.WebApiUtils;


/**
 * Embedded authentication scheme to support unit tests
 *  where RSA key pair is statically configured by
 *  {@link WebApiUtils}. And can be used to create
 *  mock bearer tokens
 *
 * @author Bren
 */
public class EmbeddedAuthenicationScheme implements AuthenticationScheme {

    private final RSAPublicKey publicKey;
    private final JWSVerifier verifier;
    private SignedJWT jwt;
    
    private final AuthenticationSchemeType SCHEME_TYPE = AuthenticationSchemeType.EMBEDDED;
    
    
    /**
     * Use statically configured {@link WenApiUtils}
     * 
     */
    public EmbeddedAuthenicationScheme() {
        this.publicKey = WebApiUtils.getPublicKey();
        this.verifier = new RSASSAVerifier(this.publicKey);
    }
    
    
    /**
     * Use provided RSA public key
     * 
     * @param publicKey 
     */
    public EmbeddedAuthenicationScheme(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
        this.verifier = new RSASSAVerifier(this.publicKey);
    }
    
    
    /**
     * Authenticate provided token
     * 
     * @param input
     * @return
     * @throws AuthenticationException 
     */
    @Override
    public AuthPrincipal authenticate(Object input) throws AuthenticationException {
        String token = (String) input;
        
        try {
            if ( this.validate(token) ) {
                return this.mapToAuthPrincipal(this.jwt.getJWTClaimsSet());
            }
            return null;
        }
        catch ( Exception ex ) {
            return null;
        }
    }
    
    
    /**
     * Map JWT Claims set to {@link AuthPrincipal}
     * 
     * @param input
     * @return 
     */
    @SuppressWarnings("unchecked")
    @Override
    public AuthPrincipal mapToAuthPrincipal(Object input) {
        JWTClaimsSet claims = (JWTClaimsSet) input;
        String subject = claims.getSubject();
        List<String> roles = (List<String>) claims.getClaim("groups");

        // Apply this as security context
        Set<String> rolesSet = new HashSet<>(roles);
        return new AuthPrincipal(subject, rolesSet);
    }


    /**
     * Validate provided bearer token
     * 
     * @param input
     * @return 
     */
    @Override
    public boolean validate(Object input) {
        String token = (String) input;
        
        // Verify signature
        if ( !this.verifySignature(token) ) {
            return false;
        }
        
        // Verify token properties
        try {
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return this.verifyExpiration(claims);
        }
        catch ( Exception ex ) {
            return false;
        }
    }
    
    
    /**
     * Verifies token signature
     * 
     * @param input
     * @return boolean
     */
    public boolean verifySignature(Object input) {
        String token = (String) input;
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if ( jwt.verify(this.verifier) ) {
                this.jwt = jwt;
                return true;
            }
            else {
                return false;
            }
        }
        catch ( Exception ex ) {
            return false;
        }
    }
    
    
    /**
     * Verify expiration of JWTClaimsSet
     * 
     * @param input
     * @return boolean
     */
    @Override
    public boolean verifyExpiration(Object input) {
        JWTClaimsSet claims = (JWTClaimsSet) input;
        Date now = new Date();
        return !(
            claims.getExpirationTime() == null ||
            claims.getExpirationTime().before(now)
        );
    }

    
    /**
     * Get {@link AuthenticationSchemeType}
     * 
     * @return {@link AuthenticationSchemeType}
     */
    @Override
    public AuthenticationSchemeType getSchemeType() {
        return this.SCHEME_TYPE;
    }
    
    
    @Override
    public boolean verifiyIssuer(Object input) {
        return true;
    }

    
    @Override
    public boolean verifyAudience(Object input) {
        return true;
    }
}