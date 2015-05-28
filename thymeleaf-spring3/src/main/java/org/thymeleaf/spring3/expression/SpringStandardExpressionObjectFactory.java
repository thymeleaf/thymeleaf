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
package org.thymeleaf.spring3.expression;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.expression.ExpressionObjectDefinition;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/**
 * <p>
 *   Builds the expression objects to be used by SpringStandard dialects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class SpringStandardExpressionObjectFactory extends StandardExpressionObjectFactory {

    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" See below.
     */
    public static final String FIELDS_EXPRESSION_OBJECT_NAME = "fields";
    public static final String FIELDS_EXPRESSION_OBJECT_DESCRIPTION = "Access to form field errors and binding info";
    public static final boolean FIELDS_EXPRESSION_OBJECT_CACHEABLE = true;
    public static final ExpressionObjectDefinition FIELDS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(FIELDS_EXPRESSION_OBJECT_NAME, FIELDS_EXPRESSION_OBJECT_DESCRIPTION, FIELDS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String THEMES_EXPRESSION_OBJECT_NAME = "themes";
    public static final String THEMES_EXPRESSION_OBJECT_DESCRIPTION = "Spring MVC themes operation";
    public static final boolean THEMES_EXPRESSION_OBJECT_CACHEABLE = true;
    public static final ExpressionObjectDefinition THEMES_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(THEMES_EXPRESSION_OBJECT_NAME, THEMES_EXPRESSION_OBJECT_DESCRIPTION, THEMES_EXPRESSION_OBJECT_CACHEABLE);

    public static final String MVC_EXPRESSION_OBJECT_NAME = "mvc";
    public static final String MVC_EXPRESSION_OBJECT_DESCRIPTION = "Creation of Spring MVC controller-bound URLs with MvcUriComponentsBuilder";
    public static final boolean MVC_EXPRESSION_OBJECT_CACHEABLE = true;
    public static final ExpressionObjectDefinition MVC_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(MVC_EXPRESSION_OBJECT_NAME, MVC_EXPRESSION_OBJECT_DESCRIPTION, MVC_EXPRESSION_OBJECT_CACHEABLE);



    public static final Map<String,ExpressionObjectDefinition> ALL_EXPRESSION_OBJECT_DEFINITIONS;

    private static final Mvc MVC_EXPRESSION_OBJECT = new Mvc();




    static {

        final Map<String,ExpressionObjectDefinition> allExpressionObjectDefinitions = new LinkedHashMap<String, ExpressionObjectDefinition>();
        allExpressionObjectDefinitions.putAll(StandardExpressionObjectFactory.ALL_EXPRESSION_OBJECT_DEFINITIONS);
        allExpressionObjectDefinitions.put(FIELDS_EXPRESSION_OBJECT_NAME, FIELDS_EXPRESSION_OBJECT_DEFINITION);
        allExpressionObjectDefinitions.put(THEMES_EXPRESSION_OBJECT_NAME, THEMES_EXPRESSION_OBJECT_DEFINITION);
        allExpressionObjectDefinitions.put(MVC_EXPRESSION_OBJECT_NAME, MVC_EXPRESSION_OBJECT_DEFINITION);

        ALL_EXPRESSION_OBJECT_DEFINITIONS = Collections.unmodifiableMap(allExpressionObjectDefinitions);

    }




    public SpringStandardExpressionObjectFactory() {
        super();
    }





    public Map<String,ExpressionObjectDefinition> getObjectDefinitions() {
        return ALL_EXPRESSION_OBJECT_DEFINITIONS;
    }



    public Object buildObject(final IProcessingContext processingContext, final String expressionObjectName) {


        if (MVC_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return MVC_EXPRESSION_OBJECT;
        }
        if (THEMES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Themes(processingContext);
        }
        if (FIELDS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Fields(processingContext);
        }

        return super.buildObject(processingContext, expressionObjectName);

    }


}
