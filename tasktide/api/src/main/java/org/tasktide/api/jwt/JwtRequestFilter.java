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
package org.tasktide.api.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import java.security.interfaces.RSAPublicKey;

import java.text.ParseException;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tasktide.api.utils.WebApiUtils;


/**
 *
 * @author Bren
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtRequestFilter implements ContainerRequestFilter {

    private final RSAPublicKey publicKey;
    
    
    public JwtRequestFilter() {
        this.publicKey = WebApiUtils.getPublicKey();
    }
    
    public JwtRequestFilter(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }
    
    
    /**
     * Blanket abort method for container requests
     * 
     * @param ctx 
     */
    private void abort(ContainerRequestContext ctx) {
        ctx.abortWith(
            Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }
    
    
    /**
     * Fetches authorization header
     * 
     * @param ctx
     * 
     * @return String
     */
    public String getAuthHeader(ContainerRequestContext ctx) {
        return ctx.getHeaderString("Authorization");
    }
    
    
    /**
     * Parse bearer token from authorization header
     * 
     * @param ctx
     * 
     * @return String
     */
    public String getToken(ContainerRequestContext ctx) {
        return this.getAuthHeader(ctx).substring("Bearer ".length());
    }
    
    
    /**
     * Checks for bearer token aborting if absent
     * 
     * @param ctx
     * 
     * @return boolean
     */
    public boolean checkHeader(ContainerRequestContext ctx) {
        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * Verifies token signature and expiration
     * 
     * @param ctx
     * @return 
     * 
     * @throws ParseException
     * @throws JOSEException 
     */
    public SignedJWT verifiySignature(ContainerRequestContext ctx) throws ParseException, JOSEException {
    
        // Verify token signature
        String token = this.getToken(ctx);
        SignedJWT jwt = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier(this.publicKey);

        if ( !jwt.verify(verifier) ) {
            return null;
        }
        
        // Verify date
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Date now = new Date();
        if ( 
            claims.getExpirationTime() == null ||
            claims.getExpirationTime().before(now)
        ) {
            return null;
        }
        
        
        // Return SignedJwt for handling
        return jwt;
    }
    
    
    /**
     * Validates authorization header
     * 
     * @param ctx 
     */
    @Override
    public void filter(ContainerRequestContext ctx) {
    
        if ( !this.checkHeader(ctx) ) {
            this.abort(ctx);
            return;
        }
        
        try {
            SignedJWT jwt = this.verifiySignature(ctx);
            if ( jwt == null ) {
                this.abort(ctx);
                return;
            }
            
            // Extract subject and their roles
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String subject = claims.getSubject();
            List<String> roles = (List<String>) claims.getClaim("groups");

            // Apply this as security context
            Set<String> rolesSet = new HashSet<>(roles);
            JwtPrincipal principal = new JwtPrincipal(subject, rolesSet);
            SecurityContext original = ctx.getSecurityContext();
            ctx.setSecurityContext(
                new JwtSecurityContext(principal, original.isSecure(), rolesSet)
            );
        }
        
        catch (ParseException | JOSEException ex) {
            this.abort(ctx);
        } 
    }
}