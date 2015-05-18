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

import java.util.LinkedHashMap;
import java.util.Map;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.expression.IExpressionObjectsFactory;

/**
 * <p>
 *   Builds an instance of {@link StandardExpressionObjects} to be used by Standard dialects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardExpressionObjectsFactory implements IExpressionObjectsFactory {



    public StandardExpressionObjectsFactory() {
        super();
    }



    public IExpressionObjects buildExpressionObjects(final IProcessingContext processingContext) {
        return new StandardExpressionObjects(processingContext);
    }


    public Map<String, String> getObjectDefinitions() {

        final Map<String,String> definitions = new LinkedHashMap<String, String>(30);

        definitions.put(
                StandardExpressionObjects.CONTEXT_EXPRESSION_OBJECT_NAME,
                "Processing Context object");
        definitions.put(
                StandardExpressionObjects.ROOT_EXPRESSION_OBJECT_NAME,
                "Expression root object on which expressions are executed");
        definitions.put(
                StandardExpressionObjects.VARIABLES_EXPRESSION_OBJECT_NAME,
                "Variables object (equivalent to the expression root object)");
        definitions.put(
                StandardExpressionObjects.SELECTION_TARGET_EXPRESSION_OBJECT_NAME,
                "Selection target (if applies)");
        definitions.put(
                StandardExpressionObjects.LOCALE_EXPRESSION_OBJECT_NAME,
                "Locale being applied for executing a template");
        definitions.put(
                StandardExpressionObjects.HTTP_SERVLET_REQUEST_EXPRESSION_OBJECT_NAME,
                "HttpServletRequest object (only in web-enabled templates)");
        definitions.put(
                StandardExpressionObjects.HTTP_SESSION_EXPRESSION_OBJECT_NAME,
                "HttpSession object (only in web-enabled templates)");
        definitions.put(
                StandardExpressionObjects.CONVERSIONS_EXPRESSION_OBJECT_NAME,
                "Converter for reshaping objects into a different data type");
        definitions.put(
                StandardExpressionObjects.URIS_EXPRESSION_OBJECT_NAME,
                "URI-related operations");
        definitions.put(
                StandardExpressionObjects.CALENDARS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with java.util.Calendar objects");
        definitions.put(
                StandardExpressionObjects.DATES_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with java.util.Date objects");
        definitions.put(
                StandardExpressionObjects.BOOLS_EXPRESSION_OBJECT_NAME,
                "Boolean utilities");
        definitions.put(
                StandardExpressionObjects.NUMBERS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with numbers (formatting, etc.)");
        definitions.put(
                StandardExpressionObjects.OBJECTS_EXPRESSION_OBJECT_NAME,
                "General utilities for objects");
        definitions.put(
                StandardExpressionObjects.STRINGS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with Strings");
        definitions.put(
                StandardExpressionObjects.ARRAYS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with arrays");
        definitions.put(
                StandardExpressionObjects.LISTS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with List objects");
        definitions.put(
                StandardExpressionObjects.SETS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with Set objects");
        definitions.put(
                StandardExpressionObjects.MAPS_EXPRESSION_OBJECT_NAME,
                "Utilities for dealing with Map objects");
        definitions.put(
                StandardExpressionObjects.AGGREGATES_EXPRESSION_OBJECT_NAME,
                "Utilities for performing aggregation operations (sum, avg...)");
        definitions.put(
                StandardExpressionObjects.MESSAGES_EXPRESSION_OBJECT_NAME,
                "Message externalization/internationalization (i18n) utilities");
        definitions.put(
                StandardExpressionObjects.IDS_EXPRESSION_OBJECT_NAME,
                "Utilities for generating unique IDs for form fields");
        definitions.put(
                StandardExpressionObjects.EXECUTION_INFO_OBJECT_NAME,
                "Execution info: template name, current time, etc.");

        return definitions;

    }


}
