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
package org.thymeleaf.spring5.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/**
 * <p>
 *   Builds the expression objects to be used by SpringStandard dialects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringStandardExpressionObjectFactory extends StandardExpressionObjectFactory {

    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" See below.
     */
    public static final String FIELDS_EXPRESSION_OBJECT_NAME = "fields";
    public static final String THEMES_EXPRESSION_OBJECT_NAME = "themes";
    public static final String MVC_EXPRESSION_OBJECT_NAME = "mvc";
    public static final String REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME = "requestdatavalues";


    public static final Set<String> ALL_EXPRESSION_OBJECT_NAMES;

    private static final Mvc MVC_EXPRESSION_OBJECT = new Mvc();




    static {

        final Set<String> allExpressionObjectNames = new LinkedHashSet<String>();
        allExpressionObjectNames.addAll(StandardExpressionObjectFactory.ALL_EXPRESSION_OBJECT_NAMES);
        allExpressionObjectNames.add(FIELDS_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(THEMES_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(MVC_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME);

        ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(allExpressionObjectNames);

    }




    public SpringStandardExpressionObjectFactory() {
        super();
    }





    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }



    public Object buildObject(final IExpressionContext context, final String expressionObjectName) {


        if (MVC_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return MVC_EXPRESSION_OBJECT;
        }
        if (THEMES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Themes(context);
        }
        if (FIELDS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Fields(context);
        }
        if (REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context instanceof ITemplateContext) {
                return new RequestDataValues((ITemplateContext)context);
            }
            return null;
        }

        return super.buildObject(context, expressionObjectName);

    }


}
