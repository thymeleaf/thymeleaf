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
package org.thymeleaf.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Base abstract class for {@link IExpressionObjects} implementations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class ExpressionObjects implements IExpressionObjects {

    /*
     * Making room for 3 expression objects should be enough for most cases, given these maps will be
     * created for each IProcessingContext (i.e. for each template processing request)
     */
    private static final int EXPRESSION_OBJECT_MAP_DEFAULT_SIZE = 3;

    private final IExpressionContext context;
    private final IExpressionObjectFactory expressionObjectFactory;
    private final Set<String> expressionObjectNames;

    private Map<String,Object> objects;



    public ExpressionObjects(
            final IExpressionContext context,
            final IExpressionObjectFactory expressionObjectFactory) {

        super();

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(expressionObjectFactory, "Expression Object Factory cannot be null");

        this.context = context;
        this.expressionObjectFactory = expressionObjectFactory;
        this.expressionObjectNames = this.expressionObjectFactory.getAllExpressionObjectNames();
    }





    public int size() {
        return this.expressionObjectNames.size();
    }


    public boolean containsObject(final String name) {
        return this.expressionObjectNames.contains(name);
    }


    public Set<String> getObjectNames() {
        return this.expressionObjectNames;
    }


    public Object getObject(final String name) {

        /*
         * First, a quick attempt to resolve from the object cache
         */
        if (this.objects != null && this.objects.containsKey(name)) {
            return this.objects.get(name);
        }

        /*
         * If the object is not provided by the factory, simply return null
         */
        if (!this.expressionObjectNames.contains(name)) {
            return null;
        }

        /*
         * Have the factory build the object
         */
        final Object object = this.expressionObjectFactory.buildObject(this.context, name);

        /*
         * If the object is not cacheable, we will just return it
         */
        if (!this.expressionObjectFactory.isCacheable(name)) {
            return object;
        }

        /*
         * The object is cacheable, so we will need to use the objects map in order to perform such caching, and
         * first of all we must ensure it (the cache) exists
         */
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(EXPRESSION_OBJECT_MAP_DEFAULT_SIZE);
        }

        /*
         * Put into cache and return
         */
        this.objects.put(name, object);
        return object;

    }


}
