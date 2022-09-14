/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.springintegration.model;

import java.util.HashMap;
import java.util.Map;

public class FormBean08 {

    private String normal = null;
    private Map<String,String> somethings = new HashMap<String,String>();


    public String getNormal() {
        return this.normal;
    }

    public void setNormal(final String normal) {
        this.normal = normal;
    }

    public Map<String,String> getSomethings() {
        return somethings;
    }

    public void setSomethings(final Map<String,String> somethings) {
        this.somethings = somethings;
    }
}
