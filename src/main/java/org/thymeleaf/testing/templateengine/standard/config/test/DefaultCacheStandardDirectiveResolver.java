/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.standard.config.test;





public class DefaultCacheStandardDirectiveResolver extends AbstractStandardDirectiveResolver<Boolean> {

    
    public static final DefaultCacheStandardDirectiveResolver INSTANCE = new DefaultCacheStandardDirectiveResolver();
    public static final Boolean DEFAULT_VALUE = Boolean.TRUE; 
    
    private DefaultCacheStandardDirectiveResolver() {
        super(Boolean.class);
    }


    @Override
    public Boolean getValue(final String executionId, final String documentName, 
            final String directiveName, final String directiveValue) {
        
        if (directiveValue == null || directiveValue.trim().equals("")) {
            return DEFAULT_VALUE;
        }
        
        final String value = directiveValue.trim().toLowerCase();
        final int valueLen = value.length();
        
        switch (valueLen) {
            case 2: return Boolean.valueOf(value.equals("on"));
            case 3: return Boolean.valueOf(value.equals("yes"));
            case 4: return Boolean.valueOf(value.equals("true"));
        }

        return Boolean.FALSE;
        
    }

    
}
