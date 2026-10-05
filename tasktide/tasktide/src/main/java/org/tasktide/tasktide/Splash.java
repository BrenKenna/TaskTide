/*
 * Copyright 2026 Brendan Kenna.
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
package org.tasktide.tasktide;


/**
 * Print splash message
 *
 * @author Bren
 */
public final class Splash {

    private static final
        Package PKG = Splash.class
            .getPackage();
    
    public static String render() {
        String version = Splash.class
           .getPackage()
           .getImplementationVersion();
        if ( version == null ) {
           version = "-development";
        }
        
        return String.format(
            """
                 _____         _      _____ _     _
                |_   _|_ _ ___| | __ |_   _(_) __| | ___
                  | |/ _` / __| |/ /   | | | |/ _` |/ _ \\
                  | | (_| \\__ \\   <    | | | | (_| |  __/
                  |_|\\__,_|___/_|\\_\\   |_| |_\\__,_|\\___/

                TaskTide-v%s
                _________________________________________________
            """, version);
    }
}