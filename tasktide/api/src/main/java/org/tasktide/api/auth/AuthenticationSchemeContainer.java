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


/**
 * Container class for {@link AuthenticationScheme}
 *
 * @author Bren
 */
public class AuthenticationSchemeContainer {

    // Authentication scheme
    private static AuthenticationScheme AUTH_SCHEME;
    
    
    /**
     * Configure container class with provided {@link AuthenticationScheme}
     * 
     * @param authScheme
     * @throws AuthenticationException 
     */
    public static void configureAuthenticationSchemeProvider(AuthenticationScheme authScheme) throws AuthenticationException {
        if ( AUTH_SCHEME == null ) {
            AUTH_SCHEME = authScheme;
        }
        else {
            throw new AuthenticationException("AuthenticationScheme already configured");
        }
    }
    
    
    /**
     * Fetch configured {@link AuthenticationScheme}
     * 
     * @return {@link AuthenticationScheme}
     * @throws AuthenticationException 
     */
    public static AuthenticationScheme getAuthenticationScheme() throws AuthenticationException {
        if ( AUTH_SCHEME == null ) {
            throw new AuthenticationException("AuthenticationScheme has not been configured"); 
        }
        else {
            return AUTH_SCHEME;
        }
    }
}