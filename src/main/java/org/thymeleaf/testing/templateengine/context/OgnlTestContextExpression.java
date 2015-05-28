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
package org.thymeleaf.testing.templateengine.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.Ognl;
import ognl.OgnlException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.Aggregates;
import org.thymeleaf.expression.Arrays;
import org.thymeleaf.expression.Bools;
import org.thymeleaf.expression.Calendars;
import org.thymeleaf.expression.Dates;
import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;
import org.thymeleaf.expression.Numbers;
import org.thymeleaf.expression.Objects;
import org.thymeleaf.expression.Sets;
import org.thymeleaf.expression.Strings;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.thymeleaf.util.Validate;


public final class OgnlTestContextExpression implements ITestContextExpression {


    private static final ConcurrentHashMap<Locale,Map<String,Object>> EXPRESSION_EVALUATION_UTILITY_OBJECTS_BY_LOCALE =
            new ConcurrentHashMap<Locale, Map<String,Object>>();


    private final String expression;



    public OgnlTestContextExpression(final String expression) {
        super();
        Validate.notNull(expression, "Expression cannot be null or empty");
        this.expression = (expression.trim().equals("")? "''" : expression);
    }


    
    public Object evaluate(final Map<String,Object> context, final Locale locale) {

        final Map<String,Object> contextVariables = new HashMap<String, Object>();
        final Map<String,Object> expressionUtilityObjects =
                buildExpressionEvaluationUtilityObjects(locale);
        if (expressionUtilityObjects != null) {
            contextVariables.putAll(expressionUtilityObjects);
        }
        
        try {
            
            final Object varExpression = Ognl.parseExpression(this.expression);
            return Ognl.getValue(varExpression, contextVariables, context);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + this.expression + "\"", e);
        }
        
    }



    private static Map<String,Object> buildExpressionEvaluationUtilityObjects(final Locale locale) {

        Map<String,Object> objects = EXPRESSION_EVALUATION_UTILITY_OBJECTS_BY_LOCALE.get(locale);
        if (objects == null) {

            objects = new HashMap<String, Object>(30);

            if (locale != null) {
                objects.put(StandardExpressionObjectFactory.CALENDARS_EXPRESSION_OBJECT_NAME, new Calendars(locale));
                objects.put(StandardExpressionObjectFactory.DATES_EXPRESSION_OBJECT_NAME, new Dates(locale));
                objects.put(StandardExpressionObjectFactory.NUMBERS_EXPRESSION_OBJECT_NAME, new Numbers(locale));
                objects.put(StandardExpressionObjectFactory.STRINGS_EXPRESSION_OBJECT_NAME, new Strings(locale));
            }
            objects.put(StandardExpressionObjectFactory.BOOLS_EXPRESSION_OBJECT_NAME, new Bools());
            objects.put(StandardExpressionObjectFactory.OBJECTS_EXPRESSION_OBJECT_NAME, new Objects());
            objects.put(StandardExpressionObjectFactory.ARRAYS_EXPRESSION_OBJECT_NAME, new Arrays());
            objects.put(StandardExpressionObjectFactory.LISTS_EXPRESSION_OBJECT_NAME, new Lists());
            objects.put(StandardExpressionObjectFactory.SETS_EXPRESSION_OBJECT_NAME, new Sets());
            objects.put(StandardExpressionObjectFactory.MAPS_EXPRESSION_OBJECT_NAME, new Maps());
            objects.put(StandardExpressionObjectFactory.AGGREGATES_EXPRESSION_OBJECT_NAME, new Aggregates());

            EXPRESSION_EVALUATION_UTILITY_OBJECTS_BY_LOCALE.put(locale, objects);

        }

        return new HashMap<String, Object>(objects);

    }


}
