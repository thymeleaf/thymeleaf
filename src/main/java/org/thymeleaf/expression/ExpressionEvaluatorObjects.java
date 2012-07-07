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
package org.thymeleaf.expression;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;

/**
 * <p>
 *   Utility class containing methods for creating utility
 *   objects that will be included into expression evaluation contexts.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class ExpressionEvaluatorObjects {

    
    
    public static final String CONTEXT_VARIABLE_NAME = "ctx";
    public static final String ROOT_VARIABLE_NAME = "root";
    public static final String SELECTION_VARIABLE_NAME = "object";
    public static final String LOCALE_EVALUATION_VARIABLE_NAME = "locale";
    
    /**
     * @since 1.1.2
     */
    public static final String HTTP_SERVLET_REQUEST_VARIABLE_NAME = "httpServletRequest";
    
    /**
     * @since 1.1.2
     */
    public static final String HTTP_SESSION_VARIABLE_NAME = "httpSession";
    
    public static final String PARAM_EVALUATION_VARIABLE_NAME = "param";
    public static final String SESSION_EVALUATION_VARIABLE_NAME = "session";
    public static final String APPLICATION_EVALUATION_VARIABLE_NAME = "application";
    
    public static final String CALENDARS_EVALUATION_VARIABLE_NAME = "calendars";
    public static final String DATES_EVALUATION_VARIABLE_NAME = "dates";
    public static final String BOOLS_EVALUATION_VARIABLE_NAME = "bools";
    public static final String NUMBERS_EVALUATION_VARIABLE_NAME = "numbers";
    public static final String OBJECTS_EVALUATION_VARIABLE_NAME = "objects";
    public static final String STRINGS_EVALUATION_VARIABLE_NAME = "strings";
    public static final String ARRAYS_EVALUATION_VARIABLE_NAME = "arrays";
    public static final String LISTS_EVALUATION_VARIABLE_NAME = "lists";
    public static final String SETS_EVALUATION_VARIABLE_NAME = "sets";
    public static final String MAPS_EVALUATION_VARIABLE_NAME = "maps";
    public static final String AGGREGATES_EVALUATION_VARIABLE_NAME = "aggregates";
    public static final String MESSAGES_EVALUATION_VARIABLE_NAME = "messages";
    public static final String IDS_EVALUATION_VARIABLE_NAME = "ids";

    public static final Calendars CALENDARS = new Calendars();
    public static final Dates DATES = new Dates();
    public static final Bools BOOLS = new Bools();
    public static final Numbers NUMBERS = new Numbers();
    public static final Objects OBJECTS = new Objects();
    public static final Strings STRINGS = new Strings();
    public static final Arrays ARRAYS = new Arrays();
    public static final Lists LISTS = new Lists();
    public static final Sets SETS = new Sets();
    public static final Maps MAPS = new Maps();
    public static final Aggregates AGGREGATES = new Aggregates();
    

    public static final Map<String,Object> EXPRESSION_EVALUATION_UTILITY_OBJECTS;
    
    
    
    
    static {
        EXPRESSION_EVALUATION_UTILITY_OBJECTS = new HashMap<String, Object>();
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(CALENDARS_EVALUATION_VARIABLE_NAME, CALENDARS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(DATES_EVALUATION_VARIABLE_NAME, DATES);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(BOOLS_EVALUATION_VARIABLE_NAME, BOOLS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(NUMBERS_EVALUATION_VARIABLE_NAME, NUMBERS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(OBJECTS_EVALUATION_VARIABLE_NAME, OBJECTS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(STRINGS_EVALUATION_VARIABLE_NAME, STRINGS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(ARRAYS_EVALUATION_VARIABLE_NAME, ARRAYS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(LISTS_EVALUATION_VARIABLE_NAME, LISTS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(SETS_EVALUATION_VARIABLE_NAME, SETS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(MAPS_EVALUATION_VARIABLE_NAME, MAPS);
        EXPRESSION_EVALUATION_UTILITY_OBJECTS.put(AGGREGATES_EVALUATION_VARIABLE_NAME, AGGREGATES);
    }
    
    
    
    
    
    private ExpressionEvaluatorObjects() {
        super();
    }

    
    
    
    /**
     * @deprecated Use {@link #computeExpressionEvaluationObjectsForArguments(Arguments)} instead.
     *             Will be removed in 2.1.x
     */
    @Deprecated
    public static Map<String,Object> computeEvaluationVariablesForArguments(final Arguments arguments) {
        return computeEvaluationObjectsForArguments(arguments);
    }
    
    
    
    /**
     * 
     * @since 2.0.9
     */
    public static Map<String,Object> computeEvaluationObjectsForArguments(final Arguments arguments) {

        final Map<String,Object> variables = new HashMap<String,Object>();
        
        variables.putAll(computeEvaluationObjectsForProcessingContext(arguments));
        
        final Messages messages = new Messages(arguments);
        variables.put(MESSAGES_EVALUATION_VARIABLE_NAME, messages);

        final Ids ids = new Ids(arguments);
        variables.put(IDS_EVALUATION_VARIABLE_NAME, ids);
        
        return variables;
        
    }
    
    
    
    
    /**
     * 
     * @since 2.0.9
     */
    public static Map<String,Object> computeEvaluationObjectsForProcessingContext(
            final IProcessingContext expressionEvaluationContext) {

        final Map<String,Object> variables = new HashMap<String,Object>();
        
        variables.putAll(computeEvaluationObjectsForContext(expressionEvaluationContext.getContext()));
        
        variables.put(ROOT_VARIABLE_NAME, expressionEvaluationContext.getExpressionEvaluationRoot());
        
        if (expressionEvaluationContext.hasSelectionTarget()) {
            variables.put(SELECTION_VARIABLE_NAME, expressionEvaluationContext.getSelectionTarget());
        } else {
            variables.put(SELECTION_VARIABLE_NAME, expressionEvaluationContext.getExpressionEvaluationRoot());
        }
        
        return variables;
        
    }

    
    
    
    
    /**
     * 
     * @since 2.0.9
     */
    public static Map<String,Object> computeEvaluationObjectsForContext(final IContext context) {

        final Map<String,Object> variables = new HashMap<String,Object>();

        variables.putAll(EXPRESSION_EVALUATION_UTILITY_OBJECTS);
        
        variables.put(CONTEXT_VARIABLE_NAME, context);
        variables.put(LOCALE_EVALUATION_VARIABLE_NAME, context.getLocale());
        
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext) context;
            variables.put(
                    PARAM_EVALUATION_VARIABLE_NAME, webContext.getRequestParameters());
            variables.put(
                    SESSION_EVALUATION_VARIABLE_NAME, webContext.getSessionAttributes());
            variables.put(
                    APPLICATION_EVALUATION_VARIABLE_NAME, webContext.getApplicationAttributes());
            variables.put(
                    HTTP_SERVLET_REQUEST_VARIABLE_NAME, webContext.getHttpServletRequest());
            variables.put(
                    HTTP_SESSION_VARIABLE_NAME, webContext.getHttpSession());
        }
        
        return variables;
        
    }


    
}
