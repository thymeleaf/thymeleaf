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
package org.thymeleaf.standard.expression;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.expression.Aggregates;
import org.thymeleaf.expression.Arrays;
import org.thymeleaf.expression.Bools;
import org.thymeleaf.expression.Calendars;
import org.thymeleaf.expression.Conversions;
import org.thymeleaf.expression.Dates;
import org.thymeleaf.expression.ExecutionInfo;
import org.thymeleaf.expression.ExpressionObjectDefinition;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.expression.Ids;
import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;
import org.thymeleaf.expression.Messages;
import org.thymeleaf.expression.Numbers;
import org.thymeleaf.expression.Objects;
import org.thymeleaf.expression.Sets;
import org.thymeleaf.expression.Strings;
import org.thymeleaf.expression.Uris;

/**
 * <p>
 *   Builds the expression objects to be used by Standard dialects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardExpressionObjectFactory implements IExpressionObjectFactory {


    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" See below.
     */

    public static final String CONTEXT_EXPRESSION_OBJECT_NAME = "ctx";
    private static final String CONTEXT_EXPRESSION_OBJECT_DESCRIPTION = "Processing Context object";
    private static final boolean CONTEXT_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition CONTEXT_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(CONTEXT_EXPRESSION_OBJECT_NAME, CONTEXT_EXPRESSION_OBJECT_DESCRIPTION, CONTEXT_EXPRESSION_OBJECT_CACHEABLE);

    public static final String ROOT_EXPRESSION_OBJECT_NAME = "root";
    private static final String ROOT_EXPRESSION_OBJECT_DESCRIPTION = "Expression root object on which expressions are executed";
    private static final boolean ROOT_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition ROOT_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(ROOT_EXPRESSION_OBJECT_NAME, ROOT_EXPRESSION_OBJECT_DESCRIPTION, ROOT_EXPRESSION_OBJECT_CACHEABLE);

    public static final String VARIABLES_EXPRESSION_OBJECT_NAME = "vars";
    private static final String VARIABLES_EXPRESSION_OBJECT_DESCRIPTION = "Variables object (equivalent to the expression root object)";
    private static final boolean VARIABLES_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition VARIABLES_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(VARIABLES_EXPRESSION_OBJECT_NAME, VARIABLES_EXPRESSION_OBJECT_DESCRIPTION, VARIABLES_EXPRESSION_OBJECT_CACHEABLE);

    public static final String SELECTION_TARGET_EXPRESSION_OBJECT_NAME = "object";
    private static final String SELECTION_TARGET_EXPRESSION_OBJECT_DESCRIPTION = "Selection target (if applies)";
    private static final boolean SELECTION_TARGET_EXPRESSION_OBJECT_CACHEABLE = false;
    private static final ExpressionObjectDefinition SELECTION_TARGET_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(SELECTION_TARGET_EXPRESSION_OBJECT_NAME, SELECTION_TARGET_EXPRESSION_OBJECT_DESCRIPTION, SELECTION_TARGET_EXPRESSION_OBJECT_CACHEABLE);

    public static final String LOCALE_EXPRESSION_OBJECT_NAME = "locale";
    private static final String LOCALE_EXPRESSION_OBJECT_DESCRIPTION = "Locale being applied for executing a template";
    private static final boolean LOCALE_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition LOCALE_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(LOCALE_EXPRESSION_OBJECT_NAME, LOCALE_EXPRESSION_OBJECT_DESCRIPTION, LOCALE_EXPRESSION_OBJECT_CACHEABLE);



    public static final String HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME = "httpServletRequest";
    private static final String HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_DESCRIPTION = "HttpServletRequest object (only in web-enabled templates)";
    private static final boolean HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME, HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_DESCRIPTION, HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_CACHEABLE);

    public static final String HTTP_SESSION_EXPRESSION_OBJECT_NAME = "httpSession";
    private static final String HTTP_SESSION_EXPRESSION_OBJECT_DESCRIPTION = "HttpSession object (only in web-enabled templates)";
    private static final boolean HTTP_SESSION_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition HTTP_SESSION_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(HTTP_SESSION_EXPRESSION_OBJECT_NAME, HTTP_SESSION_EXPRESSION_OBJECT_DESCRIPTION, HTTP_SESSION_EXPRESSION_OBJECT_CACHEABLE);

    public static final String CONVERSIONS_EXPRESSION_OBJECT_NAME = "conversions";
    private static final String CONVERSIONS_EXPRESSION_OBJECT_DESCRIPTION = "Converter for reshaping objects into a different data type";
    private static final boolean CONVERSIONS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition CONVERSIONS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(CONVERSIONS_EXPRESSION_OBJECT_NAME, CONVERSIONS_EXPRESSION_OBJECT_DESCRIPTION, CONVERSIONS_EXPRESSION_OBJECT_CACHEABLE);



    public static final String URIS_EXPRESSION_OBJECT_NAME = "uris";
    private static final String URIS_EXPRESSION_OBJECT_DESCRIPTION = "URI-related operations";
    private static final boolean URIS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition URIS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(URIS_EXPRESSION_OBJECT_NAME, URIS_EXPRESSION_OBJECT_DESCRIPTION, URIS_EXPRESSION_OBJECT_CACHEABLE);



    public static final String CALENDARS_EXPRESSION_OBJECT_NAME = "calendars";
    private static final String CALENDARS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with java.util.Calendar objects";
    private static final boolean CALENDARS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition CALENDARS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(CALENDARS_EXPRESSION_OBJECT_NAME, CALENDARS_EXPRESSION_OBJECT_DESCRIPTION, CALENDARS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String DATES_EXPRESSION_OBJECT_NAME = "dates";
    private static final String DATES_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with java.util.Date objects";
    private static final boolean DATES_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition DATES_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(DATES_EXPRESSION_OBJECT_NAME, DATES_EXPRESSION_OBJECT_DESCRIPTION, DATES_EXPRESSION_OBJECT_CACHEABLE);

    public static final String BOOLS_EXPRESSION_OBJECT_NAME = "bools";
    private static final String BOOLS_EXPRESSION_OBJECT_DESCRIPTION = "Boolean utilities";
    private static final boolean BOOLS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition BOOLS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(BOOLS_EXPRESSION_OBJECT_NAME, BOOLS_EXPRESSION_OBJECT_DESCRIPTION, BOOLS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String NUMBERS_EXPRESSION_OBJECT_NAME = "numbers";
    private static final String NUMBERS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with numbers (formatting, etc.)";
    private static final boolean NUMBERS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition NUMBERS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(NUMBERS_EXPRESSION_OBJECT_NAME, NUMBERS_EXPRESSION_OBJECT_DESCRIPTION, NUMBERS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String OBJECTS_EXPRESSION_OBJECT_NAME = "objects";
    private static final String OBJECTS_EXPRESSION_OBJECT_DESCRIPTION = "General utilities for objects";
    private static final boolean OBJECTS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition OBJECTS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(OBJECTS_EXPRESSION_OBJECT_NAME, OBJECTS_EXPRESSION_OBJECT_DESCRIPTION, OBJECTS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String STRINGS_EXPRESSION_OBJECT_NAME = "strings";
    private static final String STRINGS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with Strings";
    private static final boolean STRINGS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition STRINGS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(STRINGS_EXPRESSION_OBJECT_NAME, STRINGS_EXPRESSION_OBJECT_DESCRIPTION, STRINGS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String ARRAYS_EXPRESSION_OBJECT_NAME = "arrays";
    private static final String ARRAYS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with arrays";
    private static final boolean ARRAYS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition ARRAYS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(ARRAYS_EXPRESSION_OBJECT_NAME, ARRAYS_EXPRESSION_OBJECT_DESCRIPTION, ARRAYS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String LISTS_EXPRESSION_OBJECT_NAME = "lists";
    private static final String LISTS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with List objects";
    private static final boolean LISTS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition LISTS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(LISTS_EXPRESSION_OBJECT_NAME, LISTS_EXPRESSION_OBJECT_DESCRIPTION, LISTS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String SETS_EXPRESSION_OBJECT_NAME = "sets";
    private static final String SETS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with Set objects";
    private static final boolean SETS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition SETS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(SETS_EXPRESSION_OBJECT_NAME, SETS_EXPRESSION_OBJECT_DESCRIPTION, SETS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String MAPS_EXPRESSION_OBJECT_NAME = "maps";
    private static final String MAPS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for dealing with Map objects";
    private static final boolean MAPS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition MAPS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(MAPS_EXPRESSION_OBJECT_NAME, MAPS_EXPRESSION_OBJECT_DESCRIPTION, MAPS_EXPRESSION_OBJECT_CACHEABLE);

    public static final String AGGREGATES_EXPRESSION_OBJECT_NAME = "aggregates";
    private static final String AGGREGATES_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for performing aggregation operations (sum, avg...)";
    private static final boolean AGGREGATES_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition AGGREGATES_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(AGGREGATES_EXPRESSION_OBJECT_NAME, AGGREGATES_EXPRESSION_OBJECT_DESCRIPTION, AGGREGATES_EXPRESSION_OBJECT_CACHEABLE);

    public static final String MESSAGES_EXPRESSION_OBJECT_NAME = "messages";
    private static final String MESSAGES_EXPRESSION_OBJECT_DESCRIPTION = "Message externalization/internationalization (i18n) utilities";
    private static final boolean MESSAGES_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition MESSAGES_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(MESSAGES_EXPRESSION_OBJECT_NAME, MESSAGES_EXPRESSION_OBJECT_DESCRIPTION, MESSAGES_EXPRESSION_OBJECT_CACHEABLE);

    public static final String IDS_EXPRESSION_OBJECT_NAME = "ids";
    private static final String IDS_EXPRESSION_OBJECT_DESCRIPTION = "Utilities for generating unique IDs for form fields";
    private static final boolean IDS_EXPRESSION_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition IDS_EXPRESSION_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(IDS_EXPRESSION_OBJECT_NAME, IDS_EXPRESSION_OBJECT_DESCRIPTION, IDS_EXPRESSION_OBJECT_CACHEABLE);



    public static final String EXECUTION_INFO_OBJECT_NAME = "execInfo";
    private static final String EXECUTION_INFO_OBJECT_DESCRIPTION = "Execution info: template name, current time, etc.";
    private static final boolean EXECUTION_INFO_OBJECT_CACHEABLE = true;
    private static final ExpressionObjectDefinition EXECUTION_INFO_OBJECT_DEFINITION =
            new ExpressionObjectDefinition(EXECUTION_INFO_OBJECT_NAME, EXECUTION_INFO_OBJECT_DESCRIPTION, EXECUTION_INFO_OBJECT_CACHEABLE);




    private static final Set<ExpressionObjectDefinition> ALL_EXPRESSION_OBJECT_DEFINITIONS_SET =
            Collections.unmodifiableSet(new LinkedHashSet<ExpressionObjectDefinition>(java.util.Arrays.asList(
                    new ExpressionObjectDefinition[]{
                            CONTEXT_EXPRESSION_OBJECT_DEFINITION,
                            ROOT_EXPRESSION_OBJECT_DEFINITION,
                            VARIABLES_EXPRESSION_OBJECT_DEFINITION,
                            SELECTION_TARGET_EXPRESSION_OBJECT_DEFINITION,
                            LOCALE_EXPRESSION_OBJECT_DEFINITION,
                            HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_DEFINITION,
                            HTTP_SESSION_EXPRESSION_OBJECT_DEFINITION,
                            CONVERSIONS_EXPRESSION_OBJECT_DEFINITION,
                            URIS_EXPRESSION_OBJECT_DEFINITION,
                            CALENDARS_EXPRESSION_OBJECT_DEFINITION,
                            DATES_EXPRESSION_OBJECT_DEFINITION,
                            BOOLS_EXPRESSION_OBJECT_DEFINITION,
                            NUMBERS_EXPRESSION_OBJECT_DEFINITION,
                            OBJECTS_EXPRESSION_OBJECT_DEFINITION,
                            STRINGS_EXPRESSION_OBJECT_DEFINITION,
                            ARRAYS_EXPRESSION_OBJECT_DEFINITION,
                            LISTS_EXPRESSION_OBJECT_DEFINITION,
                            SETS_EXPRESSION_OBJECT_DEFINITION,
                            MAPS_EXPRESSION_OBJECT_DEFINITION,
                            AGGREGATES_EXPRESSION_OBJECT_DEFINITION,
                            MESSAGES_EXPRESSION_OBJECT_DEFINITION,
                            IDS_EXPRESSION_OBJECT_DEFINITION,
                            EXECUTION_INFO_OBJECT_DEFINITION
                    }
            )));
    public static final Map<String,ExpressionObjectDefinition> ALL_EXPRESSION_OBJECT_DEFINITIONS;



    private static final Uris URIS_EXPRESSION_OBJECT = new Uris();
    private static final Bools BOOLS_EXPRESSION_OBJECT = new Bools();
    private static final Objects OBJECTS_EXPRESSION_OBJECT = new Objects();
    private static final Arrays ARRAYS_EXPRESSION_OBJECT = new Arrays();
    private static final Lists LISTS_EXPRESSION_OBJECT = new Lists();
    private static final Sets SETS_EXPRESSION_OBJECT = new Sets();
    private static final Maps MAPS_EXPRESSION_OBJECT = new Maps();
    private static final Aggregates AGGREGATES_EXPRESSION_OBJECT = new Aggregates();




    static {
        final Map<String,ExpressionObjectDefinition> allExpressionObjectDefinitions =
                new LinkedHashMap<String, ExpressionObjectDefinition>(ALL_EXPRESSION_OBJECT_DEFINITIONS_SET.size());
        for (final ExpressionObjectDefinition definition : ALL_EXPRESSION_OBJECT_DEFINITIONS_SET) {
            allExpressionObjectDefinitions.put(definition.getName(), definition);
        }
        ALL_EXPRESSION_OBJECT_DEFINITIONS = Collections.unmodifiableMap(allExpressionObjectDefinitions);
    }




    public StandardExpressionObjectFactory() {
        super();
    }




    public Map<String,ExpressionObjectDefinition> getObjectDefinitions() {
        return ALL_EXPRESSION_OBJECT_DEFINITIONS;
    }



    public Object buildObject(final IProcessingContext processingContext, final String expressionObjectName) {

        if (SELECTION_TARGET_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext.getVariablesMap().hasSelectionTarget()) {
                return processingContext.getVariablesMap().getSelectionTarget();
            }
            return processingContext.getVariablesMap();
        }

        if (ROOT_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return processingContext.getVariablesMap();
        }
        if (VARIABLES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return processingContext.getVariablesMap();
        }
        if (CONTEXT_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return processingContext;
        }
        if (LOCALE_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return processingContext.getLocale();
        }
        if (HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext.isWeb()) {
                return ((IWebVariablesMap) processingContext.getVariablesMap()).getRequest();
            }
            return null;
        }
        if (HTTP_SESSION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext.isWeb()) {
                return ((IWebVariablesMap) processingContext.getVariablesMap()).getSession();
            }
            return null;
        }
        if (CONVERSIONS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Conversions(processingContext);
        }
        if (URIS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return URIS_EXPRESSION_OBJECT;
        }
        if (CALENDARS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Calendars(processingContext.getLocale());
        }
        if (DATES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Dates(processingContext.getLocale());
        }
        if (BOOLS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return BOOLS_EXPRESSION_OBJECT;
        }
        if (NUMBERS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Numbers(processingContext.getLocale());
        }
        if (OBJECTS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return OBJECTS_EXPRESSION_OBJECT;
        }
        if (STRINGS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Strings(processingContext.getLocale());
        }
        if (ARRAYS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return ARRAYS_EXPRESSION_OBJECT;
        }
        if (LISTS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return LISTS_EXPRESSION_OBJECT;
        }
        if (SETS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return SETS_EXPRESSION_OBJECT;
        }
        if (MAPS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return MAPS_EXPRESSION_OBJECT;
        }
        if (AGGREGATES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return AGGREGATES_EXPRESSION_OBJECT;
        }
        if (MESSAGES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext instanceof ITemplateProcessingContext) {
                return new Messages((ITemplateProcessingContext) processingContext);
            }
            return null;
        }
        if (IDS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext instanceof ITemplateProcessingContext) {
                return new Ids((ITemplateProcessingContext) processingContext);
            }
            return null;
        }
        if (EXECUTION_INFO_OBJECT_NAME.equals(expressionObjectName)) {
            if (processingContext instanceof ITemplateProcessingContext) {
                return new ExecutionInfo((ITemplateProcessingContext) processingContext);
            }
            return null;
        }

        return null;

    }


}
