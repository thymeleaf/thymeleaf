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
package org.thymeleaf.standard.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.expression.Aggregates;
import org.thymeleaf.expression.Arrays;
import org.thymeleaf.expression.Bools;
import org.thymeleaf.expression.Calendars;
import org.thymeleaf.expression.Conversions;
import org.thymeleaf.expression.Dates;
import org.thymeleaf.expression.ExecutionInfo;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.expression.Ids;
import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;
import org.thymeleaf.expression.Messages;
import org.thymeleaf.expression.Numbers;
import org.thymeleaf.expression.Objects;
import org.thymeleaf.expression.Sets;
import org.thymeleaf.expression.Strings;
import org.thymeleaf.expression.Temporals;
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
    public static final String ROOT_EXPRESSION_OBJECT_NAME = "root";
    public static final String VARIABLES_EXPRESSION_OBJECT_NAME = "vars";
    public static final String SELECTION_TARGET_EXPRESSION_OBJECT_NAME = "object";
    public static final String LOCALE_EXPRESSION_OBJECT_NAME = "locale";


    /*
     * These are no longer available, but their names are still used for raising an explanatory exception
     */
    public static final String REQUEST_EXPRESSION_OBJECT_NAME = "request";
    public static final String RESPONSE_EXPRESSION_OBJECT_NAME = "response";
    public static final String SESSION_EXPRESSION_OBJECT_NAME = "session";
    public static final String SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME = "servletContext";


    public static final String CONVERSIONS_EXPRESSION_OBJECT_NAME = "conversions";
    public static final String URIS_EXPRESSION_OBJECT_NAME = "uris";

    /**
     * @since 3.1.0
     */
    public static final String TEMPORALS_EXPRESSION_OBJECT_NAME = "temporals";

    public static final String CALENDARS_EXPRESSION_OBJECT_NAME = "calendars";
    public static final String DATES_EXPRESSION_OBJECT_NAME = "dates";
    public static final String BOOLS_EXPRESSION_OBJECT_NAME = "bools";
    public static final String NUMBERS_EXPRESSION_OBJECT_NAME = "numbers";
    public static final String OBJECTS_EXPRESSION_OBJECT_NAME = "objects";
    public static final String STRINGS_EXPRESSION_OBJECT_NAME = "strings";
    public static final String ARRAYS_EXPRESSION_OBJECT_NAME = "arrays";
    public static final String LISTS_EXPRESSION_OBJECT_NAME = "lists";
    public static final String SETS_EXPRESSION_OBJECT_NAME = "sets";
    public static final String MAPS_EXPRESSION_OBJECT_NAME = "maps";
    public static final String AGGREGATES_EXPRESSION_OBJECT_NAME = "aggregates";
    public static final String MESSAGES_EXPRESSION_OBJECT_NAME = "messages";
    public static final String IDS_EXPRESSION_OBJECT_NAME = "ids";

    public static final String EXECUTION_INFO_OBJECT_NAME = "execInfo";





    protected static final Set<String> ALL_EXPRESSION_OBJECT_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(java.util.Arrays.asList(
                    new String[]{
                            CONTEXT_EXPRESSION_OBJECT_NAME,
                            ROOT_EXPRESSION_OBJECT_NAME,
                            VARIABLES_EXPRESSION_OBJECT_NAME,
                            SELECTION_TARGET_EXPRESSION_OBJECT_NAME,
                            LOCALE_EXPRESSION_OBJECT_NAME,
                            CONVERSIONS_EXPRESSION_OBJECT_NAME,
                            URIS_EXPRESSION_OBJECT_NAME,
                            TEMPORALS_EXPRESSION_OBJECT_NAME,
                            CALENDARS_EXPRESSION_OBJECT_NAME,
                            DATES_EXPRESSION_OBJECT_NAME,
                            BOOLS_EXPRESSION_OBJECT_NAME,
                            NUMBERS_EXPRESSION_OBJECT_NAME,
                            OBJECTS_EXPRESSION_OBJECT_NAME,
                            STRINGS_EXPRESSION_OBJECT_NAME,
                            ARRAYS_EXPRESSION_OBJECT_NAME,
                            LISTS_EXPRESSION_OBJECT_NAME,
                            SETS_EXPRESSION_OBJECT_NAME,
                            MAPS_EXPRESSION_OBJECT_NAME,
                            AGGREGATES_EXPRESSION_OBJECT_NAME,
                            MESSAGES_EXPRESSION_OBJECT_NAME,
                            IDS_EXPRESSION_OBJECT_NAME,
                            EXECUTION_INFO_OBJECT_NAME,
                            REQUEST_EXPRESSION_OBJECT_NAME,
                            RESPONSE_EXPRESSION_OBJECT_NAME,
                            SESSION_EXPRESSION_OBJECT_NAME,
                            SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME
                    }
            )));



    private static final Uris URIS_EXPRESSION_OBJECT = new Uris();
    private static final Bools BOOLS_EXPRESSION_OBJECT = new Bools();
    private static final Objects OBJECTS_EXPRESSION_OBJECT = new Objects();
    private static final Arrays ARRAYS_EXPRESSION_OBJECT = new Arrays();
    private static final Lists LISTS_EXPRESSION_OBJECT = new Lists();
    private static final Sets SETS_EXPRESSION_OBJECT = new Sets();
    private static final Maps MAPS_EXPRESSION_OBJECT = new Maps();
    private static final Aggregates AGGREGATES_EXPRESSION_OBJECT = new Aggregates();






    public StandardExpressionObjectFactory() {
        super();
    }




    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }



    public boolean isCacheable(final String expressionObjectName) {
        // Only #object is non-cacheable (template-scope)
        return (expressionObjectName != null && !expressionObjectName.equals(SELECTION_TARGET_EXPRESSION_OBJECT_NAME));
    }



    public Object buildObject(
            final IExpressionContext context,
            final String expressionObjectName) {

        if (SELECTION_TARGET_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context instanceof ITemplateContext) {
                final ITemplateContext templateContext = (ITemplateContext) context;
                if (templateContext.hasSelectionTarget()) {
                    return templateContext.getSelectionTarget();
                }
            }
            return context;
        }

        if (ROOT_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return context;
        }
        if (VARIABLES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return context;
        }
        if (CONTEXT_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return context;
        }
        if (LOCALE_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return context.getLocale();
        }
        if (REQUEST_EXPRESSION_OBJECT_NAME.equals(expressionObjectName) ||
                SESSION_EXPRESSION_OBJECT_NAME.equals(expressionObjectName) ||
                SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME.equals(expressionObjectName) ||
                RESPONSE_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            throw new IllegalArgumentException(
                    String.format(
                            "The '%s','%s','%s' and '%s' expression utility objects are no longer available " +
                            "by default for template expressions and their use is not recommended. In cases where " +
                            "they are really needed, they should be manually added as context variables.",
                            REQUEST_EXPRESSION_OBJECT_NAME, SESSION_EXPRESSION_OBJECT_NAME,
                            SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME, RESPONSE_EXPRESSION_OBJECT_NAME));
        }
        if (CONVERSIONS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Conversions(context);
        }
        if (URIS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return URIS_EXPRESSION_OBJECT;
        }
        if (CALENDARS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Calendars(context.getLocale());
        }
        if (TEMPORALS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Temporals(context.getLocale());
        }
        if (DATES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Dates(context.getLocale());
        }
        if (BOOLS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return BOOLS_EXPRESSION_OBJECT;
        }
        if (NUMBERS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Numbers(context.getLocale());
        }
        if (OBJECTS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return OBJECTS_EXPRESSION_OBJECT;
        }
        if (STRINGS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Strings(context.getLocale());
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
            if (context instanceof ITemplateContext) {
                return new Messages((ITemplateContext) context);
            }
            return null;
        }
        if (IDS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context instanceof ITemplateContext) {
                return new Ids((ITemplateContext) context);
            }
            return null;
        }
        if (EXECUTION_INFO_OBJECT_NAME.equals(expressionObjectName)) {
            if (context instanceof ITemplateContext) {
                return new ExecutionInfo((ITemplateContext) context);
            }
            return null;
        }

        return null;

    }


}
