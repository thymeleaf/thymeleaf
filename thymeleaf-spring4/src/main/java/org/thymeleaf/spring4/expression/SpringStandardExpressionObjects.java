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
package org.thymeleaf.spring4.expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.StandardExpressionObjects;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of the {@link IExpressionObjects} interface containing all the expression objects exposed by
 *   the SpringStandard Dialect.
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
public class SpringStandardExpressionObjects extends StandardExpressionObjects {

    /*
     * Any new objects added here should also be added to the "ALL_EXPRESSION_OBJECT_NAMES" Set below.
     */
    public static final String FIELDS_EXPRESSION_OBJECT_NAME = "fields";
    public static final String THEMES_EXPRESSION_OBJECT_NAME = "themes";
    public static final String MVC_EXPRESSION_OBJECT_NAME = "mvc";


    public static final Set<String> ALL_EXPRESSION_OBJECT_NAMES;


    private final IProcessingContext processingContext;



    static {

        final Set<String> allExpressionObjectNames = new HashSet<String>();
        allExpressionObjectNames.addAll(StandardExpressionObjects.ALL_EXPRESSION_OBJECT_NAMES);
        allExpressionObjectNames.add(FIELDS_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(THEMES_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(MVC_EXPRESSION_OBJECT_NAME);

        ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(allExpressionObjectNames);

    }




    public SpringStandardExpressionObjects(final IProcessingContext processingContext) {
        super(processingContext);
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
         * First we need to check whether we are being asked for a
         * cacheable object that is already cached (in the objects map).
         */
        if (super.containsObject(name)) {
            return super.getObject(name);
        }

        /*
         * It might be a cacheable object that we need to create, so we have to check one by one
         */
        if (MVC_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Mvc());
            return super.getObject(name);
        }
        if (THEMES_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Themes(this.processingContext));
            return super.getObject(name);
        }
        if (FIELDS_EXPRESSION_OBJECT_NAME.equals(name)) {
            super.put(name, new Fields(this.processingContext));
            return super.getObject(name);
        }

        return super.getObject(name);

    }


}
