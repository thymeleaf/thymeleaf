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
package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class FragmentSignature implements Serializable {

    private static final long serialVersionUID = 6847640942405961705L;

    private static final char FRAGMENT_SIGNATURE_PARAMETERS_START = '(';
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_END = ')';


    private final String fragmentName;
    private final List<String> parameterNames;



    private FragmentSignature(final String fragmentName, final List<String> parameterNames) {
        super();
        Validate.notEmpty(fragmentName, "Fragment name cannot be null or empty");
        this.fragmentName = fragmentName;
        this.parameterNames = parameterNames;
    }

    
    
    public String getFragmentName() {
        return this.fragmentName;
    }

    public List<String> getParameterNames() {
        return this.parameterNames;
    }

    
    public String getStringRepresentation() {
        if (this.parameterNames == null || this.parameterNames.size() == 0) {
            return this.fragmentName;
        }
        return this.fragmentName + " " +
                FRAGMENT_SIGNATURE_PARAMETERS_START +
                StringUtils.join(this.parameterNames, ",") +
                FRAGMENT_SIGNATURE_PARAMETERS_END;
    }

    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
    
    
    static FragmentSignature parse(final String input) {
        
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final int parameterStart =
                input.lastIndexOf(FRAGMENT_SIGNATURE_PARAMETERS_START);
        final int parameterEnd =
                input.lastIndexOf(FRAGMENT_SIGNATURE_PARAMETERS_END);

        if (parameterStart != -1 && parameterStart >= parameterEnd) {
            return null;
        }

        final String fragmentName =
                (parameterStart == -1?
                        input.trim() : input.substring(0, parameterStart).trim());

        final String parameters =
                (parameterStart == -1?
                        null : input.substring(parameterStart + 1, input.length() - 1));

        final List<String> parameterNames;
        if (parameters != null) {
            final String[] parameterArray = StringUtils.split(parameters, ",");
            if (parameterArray.length == 0) {
                parameterNames = null;
            } else {
                parameterNames = new ArrayList<String>(parameterArray.length + 2);
                for (final String parameter : parameterArray) {
                    parameterNames.add(parameter.trim());
                }
            }
        } else {
            parameterNames = null;
        }

        return new FragmentSignature(fragmentName, parameterNames);

    }



}
