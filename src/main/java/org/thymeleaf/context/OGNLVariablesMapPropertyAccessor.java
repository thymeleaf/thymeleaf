/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.context;

import java.util.Map;

import ognl.MapPropertyAccessor;
import ognl.OgnlException;

/**
 * Extension of {@code MapPropertyAccessor} that handles getting of size
 * property. When there is entry with key "size" it is returned instead of
 * size property from {@code VariablesMap}. Otherwise this property accessor
 * works exactly same like {@code MapPropertyAccessor}.
 *
 * @author Michal Kreuzman
 * @see ognl.MapPropertyAccessor
 * @since 2.0
 */
final class OGNLVariablesMapPropertyAccessor extends MapPropertyAccessor {
    /*
     * This class supports OGNL-specific needs for the VariablesMap class, in order to avoid problems like
     * e.g. the existence of a context variable named 'size'.
     */

    private static final String RESERVED_SIZE_PROPERTY_NAME = "size";

    OGNLVariablesMapPropertyAccessor() {
        super();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getProperty(final Map context, final Object target, final Object name) throws OgnlException {

        if (!RESERVED_SIZE_PROPERTY_NAME.equals(name)) {
            return super.getProperty(context, target, name);
        }

        if (!(target instanceof VariablesMap)) {
            throw new IllegalStateException(
                    "Wrong target type. This property accessor is only usable for VariableMap class.");
        }

        final Map map = (Map) target;
        Object result = map.get(RESERVED_SIZE_PROPERTY_NAME);
        if (result == null) {
            result = Integer.valueOf(map.size());
        }
        return result;

    }

}
