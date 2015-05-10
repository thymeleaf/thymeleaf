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
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.expression.AbstractExpressionObjects;
import org.thymeleaf.expression.Aggregates;
import org.thymeleaf.expression.Arrays;
import org.thymeleaf.expression.Bools;
import org.thymeleaf.expression.Calendars;
import org.thymeleaf.expression.Conversions;
import org.thymeleaf.expression.Dates;
import org.thymeleaf.expression.ExecutionInfo;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.expression.Ids;
import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;
import org.thymeleaf.expression.Messages;
import org.thymeleaf.expression.Numbers;
import org.thymeleaf.expression.Objects;
import org.thymeleaf.expression.Sets;
import org.thymeleaf.expression.Strings;
import org.thymeleaf.expression.Uris;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of the {@link IExpressionObjects} interface containing all the expression objects exposed by
 *   the Standard Dialects.
 * </p>
 * <p>
 *   Note this implementation will try to create the expression objects lazily, only when they are really needed, in
 *   order to avoid too many instances of objects that will be later unused.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressionObjects extends AbstractExpressionObjects {

    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" Set below.
     */
    public static final String CONTEXT_EXPRESSION_OBJECT_NAME = "ctx";
    public static final String ROOT_EXPRESSION_OBJECT_NAME = "root";
    public static final String VARIABLES_EXPRESSION_OBJECT_NAME = "vars";
    public static final String SELECTION_TARGET_EXPRESSION_OBJECT_NAME = "object";
    public static final String LOCALE_EXPRESSION_OBJECT_NAME = "locale";

    public static final String HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME = "httpServletRequest";
    public static final String HTTP_SESSION_EXPRESSION_OBJECT_NAME = "httpSession";

    public static final String CONVERSIONS_EXPRESSION_OBJECT_NAME = "conversions";

    public static final String URIS_EXPRESSION_OBJECT_NAME = "uris";

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


    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES =
            Collections.unmodifiableSet(new HashSet<String>(java.util.Arrays.asList(
                    new String[] {
                            CONTEXT_EXPRESSION_OBJECT_NAME,
                            ROOT_EXPRESSION_OBJECT_NAME,
                            VARIABLES_EXPRESSION_OBJECT_NAME,
                            SELECTION_TARGET_EXPRESSION_OBJECT_NAME,
                            LOCALE_EXPRESSION_OBJECT_NAME,
                            HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME,
                            HTTP_SESSION_EXPRESSION_OBJECT_NAME,
                            CONVERSIONS_EXPRESSION_OBJECT_NAME,
                            URIS_EXPRESSION_OBJECT_NAME,
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
                            EXECUTION_INFO_OBJECT_NAME
                    }
            )));


    private final IProcessingContext processingContext;



    
    public StandardExpressionObjects(final IProcessingContext processingContext) {

        super();
        Validate.notNull(processingContext, "Processing Context cannot be null");
        this.processingContext = processingContext;

    }




    @Override
    public int size() {
        return ALL_EXPRESSION_OBJECT_NAMES.size();
    }




    @Override
    public boolean containsObject(final String name) {
        return ALL_EXPRESSION_OBJECT_NAMES.contains(name);
    }




    @Override
    public Set<String> getObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }




    @Override
    public Object getObject(final String name) {

        /*
         * First check those objects that are actually dependent on the state of the processing context, and
         * therefore will be computed each time.
         */
        if (SELECTION_TARGET_EXPRESSION_OBJECT_NAME.equals(name)) {
            if (this.processingContext.getVariablesMap().hasSelectionTarget()) {
                return this.processingContext.getVariablesMap().getSelectionTarget();
            }
            return this.processingContext.getVariablesMap();
        }

        /*
         * Once the non-cacheable objects have been checked, we need to check whether we are being asked for a
         * cacheable object that is already cached (in the objects map).
         */
        if (super.containsObject(name)) {
            return super.getObject(name);
        }

        /*
         * It might be a cacheable object that we need to create, so we have to check one by one
         */
        if (ROOT_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, this.processingContext.getVariablesMap());
            return super.getObject(name);
        }
        if (VARIABLES_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, this.processingContext.getVariablesMap());
            return super.getObject(name);
        }
        if (CONTEXT_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, this.processingContext);
            return super.getObject(name);
        }
        if (LOCALE_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, this.processingContext.getLocale());
            return super.getObject(name);
        }
        if (HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME.equals(name)) {
            if (this.processingContext.isWeb()) {
                super.put(name, ((IWebVariablesMap) this.processingContext.getVariablesMap()).getRequest());
                return super.getObject(name);
            }
            return null;
        }
        if (HTTP_SESSION_EXPRESSION_OBJECT_NAME.equals(name)) {
            if (this.processingContext.isWeb()) {
                super.put(name, ((IWebVariablesMap) this.processingContext.getVariablesMap()).getSession());
                return super.getObject(name);
            }
            return null;
        }
        if (CONVERSIONS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Conversions(this.processingContext));
            return super.getObject(name);
        }
        if (URIS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Uris());
            return super.getObject(name);
        }
        if (CALENDARS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Calendars(this.processingContext.getLocale()));
            return super.getObject(name);
        }
        if (DATES_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Dates(this.processingContext.getLocale()));
            return super.getObject(name);
        }
        if (BOOLS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Bools());
            return super.getObject(name);
        }
        if (NUMBERS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Numbers(this.processingContext.getLocale()));
            return super.getObject(name);
        }
        if (OBJECTS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Objects());
            return super.getObject(name);
        }
        if (STRINGS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Strings(this.processingContext.getLocale()));
            return super.getObject(name);
        }
        if (ARRAYS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Arrays());
            return super.getObject(name);
        }
        if (LISTS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Lists());
            return super.getObject(name);
        }
        if (SETS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Sets());
            return super.getObject(name);
        }
        if (MAPS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Maps());
            return super.getObject(name);
        }
        if (AGGREGATES_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Aggregates());
            return super.getObject(name);
        }
        if (MESSAGES_EXPRESSION_OBJECT_NAME.equals(name)) {
            if (this.processingContext instanceof ITemplateProcessingContext) {
                super.put(name, new Messages((ITemplateProcessingContext) this.processingContext));
                return super.getObject(name);
            }
            return null;
        }
        if (IDS_EXPRESSION_OBJECT_NAME.equals(name)) {
            if (this.processingContext instanceof ITemplateProcessingContext) {
                super.put(name, new Ids((ITemplateProcessingContext) this.processingContext));
                return super.getObject(name);
            }
            return null;
        }
        if (EXECUTION_INFO_OBJECT_NAME.equals(name)) {
            if (this.processingContext instanceof ITemplateProcessingContext) {
                super.put(name, new ExecutionInfo((ITemplateProcessingContext)this.processingContext));
                return super.getObject(name);
            }
            return null;
        }

        return null;

    }


}
