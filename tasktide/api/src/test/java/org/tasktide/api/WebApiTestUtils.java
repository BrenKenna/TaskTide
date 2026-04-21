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

import io.smallrye.jwt.build.Jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import java.util.Base64;


/**
 * Utility to support unit-testing web api resources.
 *  Generating Pub-Priv keypair, JWT tokens etc
 *
 * @author Bren
 */
public class WebApiTestUtils {

    // Key pairs
    private static KeyPairGenerator KPG;
    private static KeyPair KP;
    
    
    /**
     * Get {@link KeyPair}
     * 
     * @return {@link KeyPair}
     * 
     * @throws Exception 
     */
    public static KeyPair getKeyPair() throws Exception {
        if ( KP == null ) {
            generateKeyPair();
        }
        return KP;
    }

    
    /**
     * Generate {@link KeyPair}
     * 
     * @throws Exception 
     */
    private static void generateKeyPair() throws Exception {
        if ( KP != null ) {
            return;
        }
        
        if ( KPG == null ) {
             KPG = KeyPairGenerator.getInstance("RSA");
        }
        KPG.initialize(2048);
        KP = KPG.generateKeyPair();
    }

    
    /**
     * PEM format {@link PublicKey}
     * 
     * @param key
     * @return String
     */
    public static String toPemPublic(PublicKey key) {
        return "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getEncoder().encodeToString(key.getEncoded()) +
                "\n-----END PUBLIC KEY-----";
    }
    
    
    /**
     * Sign JWT with private key 
     * 
     * @param user
     * @return String
     */
    public static String token(String user) {        
        return Jwt
            .issuer("web-api-testing")
            .subject(user)
            .groups("user")
        .sign(KP.getPrivate());
    }
}