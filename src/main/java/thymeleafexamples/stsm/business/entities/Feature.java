/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package thymeleafexamples.stsm.business.entities;



public enum Feature {
    
    SEEDSTARTER_SPECIFIC_SUBSTRATE("SEEDSTARTER_SPECIFIC_SUBSTRATE"), 
    FERTILIZER("FERTILIZER"), 
    PH_CORRECTOR("PH_CORRECTOR");

    
    public static final Feature[] ALL = { SEEDSTARTER_SPECIFIC_SUBSTRATE, FERTILIZER, PH_CORRECTOR };
    
    
    private final String name;

    
    
    public static Feature forName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null for feature");
        }
        if (name.toUpperCase().equals("SEEDSTARTER_SPECIFIC_SUBSTRATE")) {
            return SEEDSTARTER_SPECIFIC_SUBSTRATE;
        } else if (name.toUpperCase().equals("FERTILIZER")) {
            return FERTILIZER;
        } else if (name.toUpperCase().equals("PH_CORRECTOR")) {
            return PH_CORRECTOR;
        }
        throw new IllegalArgumentException("Name \"" + name + "\" does not correspond to any Feature");
    }
    
    
    private Feature(final String name) {
        this.name = name;
    }
    
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
}
