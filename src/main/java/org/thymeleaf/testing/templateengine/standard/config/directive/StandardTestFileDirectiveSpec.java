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
package org.thymeleaf.testing.templateengine.standard.config.directive;

import org.thymeleaf.testing.templateengine.standard.config.test.IStandardDirectiveResolver;






public final class StandardTestFileDirectiveSpec<T> {

    
    private final String name;
    private final Class<T> expectedClass;
    private final IStandardDirectiveResolver<? extends T> defaultConfigResolver;

    
    
    public StandardTestFileDirectiveSpec(
            final String name, final Class<T> expectedClass, IStandardDirectiveResolver<? extends T> defaultConfigResolver) {
        super();
        this.name = name;
        this.expectedClass = expectedClass;
        this.defaultConfigResolver = defaultConfigResolver;
    }


    public String getName() {
        return this.name;
    }


    public Class<T> getExpectedClass() {
        return this.expectedClass;
    }


    public IStandardDirectiveResolver<? extends T> getDefaultConfigResolver() {
        return this.defaultConfigResolver;
    }
    
    
}
