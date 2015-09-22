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
package org.thymeleaf.spring3.expression;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * <p>
 *   Wrapper on {@link IVariablesMap} objects that makes them look like <tt>java.util.Map</tt> objects
 *   in order to be used at the root of SpEL expressions without the need to use custom property accessors
 *   in most scenarios.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class SPELVariablesMapWrapper implements Map {

    private static final Logger LOGGER = LoggerFactory.getLogger(SPELVariablesMapWrapper.class);

    private static final String REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME = "param";


    private final IVariablesMap variablesMap;
    private final IThymeleafEvaluationContext evaluationContext;



    SPELVariablesMapWrapper(final IVariablesMap variablesMap, final IThymeleafEvaluationContext evaluationContext) {
        super();
        this.variablesMap = variablesMap;
        this.evaluationContext = evaluationContext;
    }









    public int size() {
        throw new TemplateProcessingException(
                "Cannot call #size() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public boolean isEmpty() {
        throw new TemplateProcessingException(
                "Cannot call #isEmpty() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public boolean containsKey(final Object key) {
        if (this.evaluationContext.isVariableAccessRestricted()) {
            if (REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(key)) {
                throw new TemplateProcessingException(
                        "Access to variable \"" + key + "\" is forbidden in this context. Note some restrictions apply to " +
                        "variable access. For example, accessing request parameters is forbidden in preprocessing and " +
                        "unescaped expressions, and also in fragment inclusion specifications.");
            }
        }
        // We will be NOT calling this.variablesMap.containsVariable(key) as it could be very inefficient in web
        // environments (based on HttpServletRequest#getAttributeName()), so we will just consider that every possible
        // element exists in an IVariablesMap, and simply return null for those not found
        return this.variablesMap != null;
    }




    public boolean containsValue(final Object value) {
        throw new TemplateProcessingException(
                "Cannot call #containsValue(value) on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public Object get(final Object key) {

        if (this.variablesMap == null) {
            throw new TemplateProcessingException("Cannot read property on null target");
        }

        /*
         * NOTE we do not check here whether we are being asked for the 'locale', 'request', 'response', etc.
         * because there already are specific expression objects for the most important of them, which should
         * be used instead: #locale, #httpServletRequest, #httpSession, etc.
         * The variables maps should just be used as a map, without exposure of its more-internal methods...
         */

        // 'execInfo' translation from context variable to expression object - deprecated and to be removed in 3.1
        if ("execInfo".equals(key)) { // Quick check to avoid deprecated method call
            final Object execInfoResult = SPELVariablesMapPropertyAccessor.checkExecInfo(key.toString(), this.evaluationContext);
            if (execInfoResult != null) {
                return execInfoResult;
            }
        }

        return this.variablesMap.getVariable(key == null? null : key.toString());

    }




    public Object put(final Object key, final Object value) {
        throw new TemplateProcessingException(
                "Cannot call #put(key,value) on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public Object remove(final Object key) {
        throw new TemplateProcessingException(
                "Cannot call #remove(key) on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public void putAll(final Map m) {
        throw new TemplateProcessingException(
                "Cannot call #putAll(m) on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public void clear() {
        throw new TemplateProcessingException(
                "Cannot call #clear() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public Set keySet() {
        throw new TemplateProcessingException(
                "Cannot call #keySet() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public Collection values() {
        throw new TemplateProcessingException(
                "Cannot call #values() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }




    public Set<Entry> entrySet() {
        throw new TemplateProcessingException(
                "Cannot call #entrySet() on an " + IVariablesMap.class.getSimpleName() + " implementation");
    }


}
