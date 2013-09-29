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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Represents a fragment signature, including both a name and an (optional) sequence of parameter names to be
 *   applied. Typically the result of parsing a <tt>th:fragment</tt> attribute.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class FragmentSignature implements IStandardExpressionFragmentSignatureStructure, Serializable {

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


    public boolean hasParameters() {
        return this.parameterNames != null && this.parameterNames.size() > 0;
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
                StringUtils.join(this.parameterNames, ',') +
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





    /**
     * <p>
     *   Processes a set of parameters that have been specified for a fragment with the current fragment signature.
     * </p>
     * <p>
     *   This processing matches the specified parameters against the ones in the signature, allowing the specified
     *   ones (usually coming from a fragment selection like <tt>th:include</tt>) to be nameless, so that their values
     *   are matched to their corresponding variable name during this parameter processing operation.
     * </p>
     * <p>
     *   The resulting processed parameters are typically applied as local variables to the nodes of a
     *   selected fragment.
     * </p>
     *
     * @param specifiedParameters the set of specified parameters
     * @return the processed set of parameters, ready to be applied as local variables to the fragment's nodes.
     */
    public Map<String,Object> processParameters(final Map<String, Object> specifiedParameters) {

        if (specifiedParameters == null || specifiedParameters.size() == 0) {

            if (hasParameters()) {
                // Fragment signature requires parameters, but we haven't specified them!
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + getStringRepresentation() +  "\" " +
                                "declares parameters, but fragment selection did not specify any parameters.");
            }

            return null;

        }

        final boolean parametersAreSynthetic =
                FragmentSelection.parameterNamesAreSynthetic(specifiedParameters.keySet());

        if (parametersAreSynthetic && !hasParameters()) {
            throw new TemplateProcessingException(
                    "Cannot resolve fragment. Signature \"" + getStringRepresentation() +  "\" " +
                            "declares no parameters, but fragment selection did specify parameters in a synthetic manner " +
                            "(without names), which is not correct due to the fact parameters cannot be assigned names " +
                            "unless signature specifies these names.");
        }

        if (parametersAreSynthetic) {
            // No need to match parameter names, just apply the ones from the signature

            final List<String> signatureParameterNames = getParameterNames();

            if (signatureParameterNames.size() != specifiedParameters.size()) {
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + getStringRepresentation() +  "\" " +
                                "declares " + signatureParameterNames.size() + " parameters, but fragment selection specifies " +
                                specifiedParameters.size() + " parameters. Fragment selection does not correctly match.");
            }

            final Map<String,Object> processedParameters = new HashMap<String, Object>(signatureParameterNames.size() + 1, 1.0f);
            int index = 0;
            for (final String parameterName : signatureParameterNames) {
                final String syntheticParameterName =
                        FragmentSelection.getSyntheticParameterNameForIndex(index++);
                final Object parameterValue = specifiedParameters.get(syntheticParameterName);
                processedParameters.put(parameterName, parameterValue);
            }

            return processedParameters;

        }

        if (!hasParameters()) {
            // Parameters in fragment selection are not synthetic, and fragment signature has no parameters,
            // so we just use the "specified parameters".
            return specifiedParameters;
        }

        // Parameters are not synthetic and signature does specify parameters, so their names should match (all
        // the parameters specified at the fragment signature should be specified at the fragment selection,
        // though fragment selection can specify more parameters, not present at the signature.

        final List<String> parameterNames = getParameterNames();
        for (final String parameterName : parameterNames) {
            if (!specifiedParameters.containsKey(parameterName)) {
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + getStringRepresentation() +  "\" " +
                                "declares parameter \"" + parameterName + "\", which is not specified at the fragment " +
                                "selection.");
            }
        }

        return specifiedParameters;

    }




}
