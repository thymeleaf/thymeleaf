/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21spring31.requestdata.processor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;


public class TestRequestDataValueProcessor implements RequestDataValueProcessor {


    public String processAction(HttpServletRequest request, String action) {
        return "[" + action + "]";
    }

    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        return "(" + value + "," + name + "," + type + ")";
    }

    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        final Map<String,String> map = new LinkedHashMap<String,String>();
        map.put("extra-field1", "The value of the First Extra field");
        map.put("extra-field2", "The value of the Second Extra field");
        return map;
    }

    public String processUrl(HttpServletRequest request, String url) {
        return "{" + url + "}";
    }
}
