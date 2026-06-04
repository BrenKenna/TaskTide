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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;

import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;

import com.nimbusds.jwt.JWTClaimsSet;

import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import jakarta.enterprise.context.ApplicationScoped;

import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * OIDC {@link AuthenticationScheme}. Nimbus performs OIDC cryptographic
 *  verification, and token properties. Letting endpoints to focus
 *  on evaluating roles
 *
 * @author Bren
 */
@ApplicationScoped
public class OidcAuthenticationScheme implements AuthenticationScheme {

    // Nimbus security context processor
    private final ConfigurableJWTProcessor<SecurityContext> processor;
    
    // OIDC parameters
    private final OidcConfig config;
    private final OIDCProviderMetadata metadata;
    private final URL jwksUrl;
    
    // Scheme type
    private final SchemeType SCHEME_TYPE = SchemeType.OIDC;
    

    /**
     * Construct with properties
     * 
     * @param config
     * @param metadata
     * @param jwksUrl 
     */
    public OidcAuthenticationScheme(OidcConfig config, OIDCProviderMetadata metadata, URL jwksUrl) {
        this.config = config;
        this.metadata = metadata;
        this.jwksUrl = jwksUrl;
        
        // Build processor for cryptographic verification
        this.processor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> jwkSource =
            JWKSourceBuilder
                .create(this.jwksUrl)
            .build();
        JWSKeySelector<SecurityContext>
            keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.ES256, jwkSource);
        this.processor.setJWSKeySelector(keySelector);
    }
    
    
    /**
     * Get scheme type
     * 
     * @return {@link SchemeType}
     */
    @Override
    public SchemeType getSchemeType() {
        return this.SCHEME_TYPE;
    }


    /**
     * Validate JWT properties
     * 
     * @param input
     * @return boolean
     */
    @Override
    public boolean validate(Object input) {

        // Verify issue
        JWTClaimsSet claims = (JWTClaimsSet) input;
        if ( !this.verifiyIssuer(claims) ) {
            return false;
        }

        // Verify audience
        if ( !this.verifyAudience(claims) ) {
            return false;
        }
        
        // Verify expiration
        return this.verifyExpiration(claims);
    }
    
    
    /**
     * Verify that the claim has not expired
     * 
     * @param input
     * @return boolean
     */
    @Override
    public boolean verifyExpiration(Object input) {
        JWTClaimsSet claims = (JWTClaimsSet) input;
        
        Date notBeforeTimeOnClaim = claims.getNotBeforeTime();
        Date now = new Date();
        if ( notBeforeTimeOnClaim == null ) {
            return false;
        }
        return notBeforeTimeOnClaim.before(now);
    }
    
    
    /**
     * Verify application is configured audience of 
     *  JWT claim
     * 
     * @param input
     * @return boolean
     */
    @Override
    public boolean verifyAudience(Object input) {
        JWTClaimsSet claims = (JWTClaimsSet) input;
        
        List<String> audienceOnClaim = claims.getAudience();
        if ( audienceOnClaim == null ) {
            return false;
        }
        return audienceOnClaim.contains(this.config.audience());
    }
    
    
    /**
     * Verifies that the issuer on JWT claims set
     *  matches configured issuer
     * 
     * @param input
     * 
     * @return boolean
     */
    @Override
    public boolean verifiyIssuer(Object input) {
        JWTClaimsSet claims = (JWTClaimsSet) input;
        
        String issuerOnClaim = claims.getIssuer();
        String configuredIssuer = this.config.issuer().toString();
        if ( issuerOnClaim == null ) {
            return false;
        }
        return issuerOnClaim.equals(configuredIssuer);
    }
    

    /**
     * Map JWT to {@link AuthPrincipal}
     * 
     * @param input
     * @return {@link AuthPrincipal}
     */
    @Override
    public AuthPrincipal mapToAuthPrincipal(Object input) {
        
        JWTClaimsSet claims = (JWTClaimsSet) input;
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    /**
     * Verify authenticity of bearer token
     * 
     * @param input
     * @return
     * @throws AuthenticationException 
     */
    @Override
    public AuthPrincipal authenticate(Object input) throws AuthenticationException {
        
        // Initialize vars
        String token = (String) input;
        JWTClaimsSet claims;
        
        // Try parse jwt
        try {
            
            // Map if cryptographically verified
            claims = this.processor.process(token, null);
            if ( this.validate(claims) ) {
                return this.mapToAuthPrincipal(claims);
            }
            else {
                throw new AuthenticationException("Unable to verify token");
            }
        }
        
        // Otherwise raise exception
        catch (ParseException | BadJOSEException | JOSEException ex) {
            throw new AuthenticationException("Invalid bearer token:\n", ex);
        }
    }
}