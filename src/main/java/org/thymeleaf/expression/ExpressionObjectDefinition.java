/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.expression;

import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ExpressionObjectDefinition {

    private final String name;
    private final String description;
    private final boolean cacheable;


    public ExpressionObjectDefinition(final String name, final String description, final boolean cacheable) {
        super();
        Validate.notEmpty(name, "Expression object name cannot be null or empty");
        this.name = name;
        this.description = description;
        this.cacheable = cacheable;
    }

    public String getName() {
        return this.name;
    }

    public boolean isCacheable() {
        return this.cacheable;
    }

    public String getDescription() {
        return this.description;
    }

}
