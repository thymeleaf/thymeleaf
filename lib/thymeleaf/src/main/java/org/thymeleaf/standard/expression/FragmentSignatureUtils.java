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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class FragmentSignatureUtils {


    private static final char FRAGMENT_SIGNATURE_PARAMETERS_START = '(';
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_END = ')';








    public static FragmentSignature parseFragmentSignature(final IEngineConfiguration configuration, final String input) {

        Validate.notNull(configuration, "Configuration cannot be null");
        // Processing context CAN (and many times will, in fact) be null! - no variables can be used in signatures.
        Validate.notNull(input, "Input cannot be null");

        // No need to preprocess, also no need to have a context, because fragment signatures are
        // token-only based (no expressions allowed).

        if (configuration != null) {
            final FragmentSignature cachedFragmentSignature =
                    ExpressionCache.getFragmentSignatureFromCache(configuration, input);
            if (cachedFragmentSignature != null) {
                return cachedFragmentSignature;
            }
        }

        final FragmentSignature fragmentSignature =
                FragmentSignatureUtils.internalParseFragmentSignature(input.trim());

        if (fragmentSignature == null) {
            throw new TemplateProcessingException("Could not parse as fragment signature: \"" + input + "\"");
        }

        if (configuration != null) {
            ExpressionCache.putFragmentSignatureIntoCache(configuration, input, fragmentSignature);
        }

        return fragmentSignature;

    }





    static FragmentSignature internalParseFragmentSignature(final String input) {
        
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
     *   Processes a set of parameters that have been specified for a fragment signature.
     * </p>
     * <p>
     *   This processing matches the specified parameters against the ones in the signature, allowing the specified
     *   ones (usually coming from a fragment selection like {@code th:include}) to be nameless, so that their values
     *   are matched to their corresponding variable name during this parameter processing operation.
     * </p>
     * <p>
     *   The resulting processed parameters are typically applied as local variables to the nodes of a
     *   selected fragment.
     * </p>
     *
     * @param fragmentSignature the signature parameters should be processed against
     * @param specifiedParameters the set of specified parameters
     * @param parametersAreSynthetic whether the parameter names in the specifiedParameters map are synthetic or not
     * @return the processed set of parameters, ready to be applied as local variables to the fragment's nodes.
     */
    public static Map<String,Object> processParameters(
            final FragmentSignature fragmentSignature,
            final Map<String, Object> specifiedParameters, final boolean parametersAreSynthetic) {

        Validate.notNull(fragmentSignature, "Fragment signature cannot be null");

        if (specifiedParameters == null || specifiedParameters.size() == 0) {

            if (fragmentSignature.hasParameters()) {
                // Fragment signature requires parameters, but we haven't specified them!
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                                "declares parameters, but fragment selection did not specify any parameters.");
            }

            return null;

        }

        if (parametersAreSynthetic && !fragmentSignature.hasParameters()) {
            throw new TemplateProcessingException(
                    "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                            "declares no parameters, but fragment selection did specify parameters in a synthetic manner " +
                            "(without names), which is not correct due to the fact parameters cannot be assigned names " +
                            "unless signature specifies these names.");
        }

        if (parametersAreSynthetic) {
            // No need to match parameter names, just apply the ones from the signature

            final List<String> signatureParameterNames = fragmentSignature.getParameterNames();

            if (signatureParameterNames.size() != specifiedParameters.size()) {
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                                "declares " + signatureParameterNames.size() + " parameters, but fragment selection specifies " +
                                specifiedParameters.size() + " parameters. Fragment selection does not correctly match.");
            }

            final Map<String,Object> processedParameters = new HashMap<String, Object>(signatureParameterNames.size() + 1, 1.0f);
            int index = 0;
            for (final String parameterName : signatureParameterNames) {
                final String syntheticParameterName = getSyntheticParameterNameForIndex(index++);
                final Object parameterValue = specifiedParameters.get(syntheticParameterName);
                processedParameters.put(parameterName, parameterValue);
            }

            return processedParameters;

        }

        if (!fragmentSignature.hasParameters()) {
            // Parameters in fragment selection are not synthetic, and fragment signature has no parameters,
            // so we just use the "specified parameters".
            return specifiedParameters;
        }

        // Parameters are not synthetic and signature does specify parameters, so their names should match (all
        // the parameters specified at the fragment signature should be specified at the fragment selection,
        // though fragment selection can specify more parameters, not present at the signature.

        final List<String> parameterNames = fragmentSignature.getParameterNames();
        for (final String parameterName : parameterNames) {
            if (!specifiedParameters.containsKey(parameterName)) {
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                                "declares parameter \"" + parameterName + "\", which is not specified at the fragment " +
                                "selection.");
            }
        }

        return specifiedParameters;

    }



    static String getSyntheticParameterNameForIndex(final int i) {
        return FragmentExpression.UNNAMED_PARAMETERS_PREFIX + i;
    }




    private FragmentSignatureUtils() {
        super();
    }

}
