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
package org.thymeleaf.fragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Fragment specification that extracts a specific element from a Node tree
 *   by searching for an attribute containing a fragment signature in the form defined
 *   by {@link org.thymeleaf.standard.expression.FragmentSignature}.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @since 2.1.0
 */
public final class FragmentSignatureAttributeFragmentSpec implements IFragmentSpec {

    private final String attributeName;
    private final String fragmentName;
    private final boolean returnOnlyChildren;

    private final Map<String,Object> parameterValues;



    /**
     * <p>
     *   Create a fragment spec specifying the name of the attribute in which we expect to find a fragment signature,
     *   which fragment name has to match the one we specify as a method argument.
     * </p>
     *
     * @param attributeName the attribute name to look for. This is a required argument.
     * @param fragmentName the name of the fragment we expect to find in the fragment signature.
     */
    public FragmentSignatureAttributeFragmentSpec(
            final String attributeName,
            final String fragmentName) {
        this(attributeName,fragmentName, null, false);
    }


    /**
     * <p>
     *   Create a fragment spec specifying the name of the attribute in which we expect to find a fragment signature,
     *   which fragment name has to match the one we specify as a method argument.
     * </p>
     * <p>
     *   This constructor allows the specification of a series of fragment parameters, which will be applied
     *   as local variables to the extracted nodes.
     * </p>
     *
     * @param attributeName the attribute name to look for. This is a required argument.
     * @param fragmentName the name of the fragment we expect to find in the fragment signature.
     * @param parameterValues the fragment parameters, which will be applied as local variables to the nodes
     *                        returned as extraction result. Might be null if no parameters are applied.
     */
    public FragmentSignatureAttributeFragmentSpec(
            final String attributeName,
            final String fragmentName,
            final Map<String, Object> parameterValues) {
        this(attributeName, fragmentName, parameterValues, false);
    }



    /**
     * <p>
     *   Create a fragment spec specifying the name of the attribute in which we expect to find a fragment signature,
     *   which fragment name has to match the one we specify as a method argument.
     * </p>
     * <p>
     *   If <tt>returnOnlyChildren</tt> is true, the element with the specified name
     *   and/or containing the specified attribute will be discarded, and only its/their
     *   children will be returned.
     * </p>
     *
     * @param attributeName the attribute name to look for. This is a required argument.
     * @param fragmentName the name of the fragment we expect to find in the fragment signature.
     * @param returnOnlyChildren whether the selected elements should be returned (false),
     *        or only their children (true).
     */
    public FragmentSignatureAttributeFragmentSpec(
            final String attributeName,
            final String fragmentName,
            final boolean returnOnlyChildren) {
        this(attributeName, fragmentName, null, returnOnlyChildren);
    }


    /**
     * <p>
     *   Create a fragment spec specifying the name of the attribute in which we expect to find a fragment signature,
     *   which fragment name has to match the one we specify as a method argument.
     * </p>
     * <p>
     *   This constructor allows the specification of a series of fragment parameters, which will be applied
     *   as local variables to the extracted nodes.
     * </p>
     * <p>
     *   If <tt>returnOnlyChildren</tt> is true, the element with the specified name
     *   and/or containing the specified attribute will be discarded, and only its/their
     *   children will be returned.
     * </p>
     *
     * @param attributeName the attribute name to look for. This is a required argument.
     * @param fragmentName the name of the fragment we expect to find in the fragment signature.
     * @param parameterValues the fragment parameters, which will be applied as local variables to the nodes
     *                        returned as extraction result. Might be null if no parameters are applied.
     * @param returnOnlyChildren whether the selected elements should be returned (false),
     *        or only their children (true).
     */
    public FragmentSignatureAttributeFragmentSpec(
            final String attributeName,
            final String fragmentName,
            final Map<String, Object> parameterValues,
            final boolean returnOnlyChildren) {
        
        super();

        Validate.notEmpty(attributeName, "Attribute name cannot be null");
        Validate.notEmpty(fragmentName, "Fragment name cannot be null");

        this.attributeName = attributeName;
        this.fragmentName = fragmentName;
        this.parameterValues = parameterValues;
        this.returnOnlyChildren = returnOnlyChildren;

    }

    
    /**
     * <p>
     *   Returns the attribute name. This is the attribute in which we expect to find a fragment
     *   signature. This field cannot be null.
     * </p>
     *
     * @return the attribute name.
     */
    public String getAttributeName() {
        return this.attributeName;
    }


    /**
     * <p>
     *   Returns the name of the fragment we are looking for. This name must match the one specified at the
     *   fragment signature we are looking for. This field cannot be null.
     * </p>
     *
     * @return the name of the fragment we are looking for.
     */
    public String getFragmentName() {
        return this.fragmentName;
    }
    
    
    /**
     * <p>
     *   Returns whether this spec should only return the children of the selected nodes
     *   (<tt>true</tt>) or the selected nodes themselves (<tt>false</tt>, default).
     * </p>
     * 
     * @return whether this spec should only return the children of the selected nodes
     *         or not (default: false).
     */
    public boolean isReturnOnlyChildren() {
        return this.returnOnlyChildren;
    }


    /**
     * <p>
     *   Returns the map of parameter values that will be applied as local variables to the extracted nodes.
     * </p>
     *
     * @return the map of parameters.
     */
    public Map<String,Object> getParameterValues() {
        return Collections.unmodifiableMap(this.parameterValues);
    }


    /**
     * <p>
     *   Returns whether this fragment specifies parameter values, to be set as local variables into the extracted
     *   nodes.
     * </p>
     *
     * @return true if the fragment spec specifies parameters, false if not
     */
    public boolean hasParameterValues() {
        return this.parameterValues != null && this.parameterValues.size() > 0;
    }


    
    
    
    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        
        final Extraction extraction = extractFragment(nodes, this.attributeName, this.fragmentName);

        if (extraction == null) {
            return Collections.emptyList();
        }

        final Node extractionNode = extraction.getNode();
        final FragmentSignature fragmentSignature = extraction.getFragmentSignature();

        final Map<String,Object> processedParameters = processParameters(this.parameterValues, fragmentSignature);

        if (!this.returnOnlyChildren) {
            final List<Node> extractionNodes = Collections.singletonList(extractionNode);
            applyParameters(extractionNodes, processedParameters);
            return extractionNodes;
        }

        if (!(extractionNode instanceof NestableNode)) {
            throw new TemplateProcessingException(
                    "Cannot correctly retrieve children of node selected by fragment signature specified" +
                    "in attribute named \"" + this.attributeName + "\", with fragment name \"" +
                    this.fragmentName + "\". Node is not a nestable " +
                    "node (" + extractionNode.getClass().getSimpleName() + ").");
        }

        final List<Node> extractionNodes = ((NestableNode)extractionNode).getChildren();
        applyParameters(extractionNodes, processedParameters);
        return extractionNodes;
        
    }



    private static void applyParameters(final List<Node> nodes, final Map<String,Object> parameterValues) {
        for (final Node node : nodes) {
            node.setAllNodeLocalVariables(parameterValues);
        }
    }



    private static Map<String,Object> processParameters(
            final Map<String,Object> specifiedParameters, final FragmentSignature fragmentSignature) {

        if (specifiedParameters == null || specifiedParameters.size() == 0) {

            if (fragmentSignature.hasParameters()) {
                // Fragment signature requires parameters, but we haven't specified them!
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                        "declares parameters, but fragment selection did not specify any parameters.");
            }

            return null;

        }

        final boolean syntheticParameters =
                FragmentSelection.parameterNamesAreSynthetic(specifiedParameters.keySet());

        if (syntheticParameters && !fragmentSignature.hasParameters()) {
            throw new TemplateProcessingException(
                    "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                    "declares no parameters, but fragment selection did specify parameters in a synthetic manner " +
                    "(without names), which is not correct due to the fact parameters cannot be assigned names " +
                    "unless signature specifies these names.");
        }

        if (syntheticParameters) {
            // No need to match parameter names, just apply the ones from the signature

            final List<String> parameterNames = fragmentSignature.getParameterNames();

            if (parameterNames.size() != specifiedParameters.size()) {
                throw new TemplateProcessingException(
                        "Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() +  "\" " +
                        "declares " + parameterNames.size() + " parameters, but fragment selection specifies " +
                        specifiedParameters.size() + " parameters. Fragment selection does not correctly match.");
            }

            final Map<String,Object> processedParameters = new HashMap<String, Object>(parameterNames.size() + 1, 1.0f);
            int index = 0;
            for (final String parameterName : parameterNames) {
                final String syntheticParameterName =
                        FragmentSelection.getSyntheticParameterNameForIndex(index++);
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




    private static Extraction extractFragment(
            final List<Node> rootNodes, final String attributeName, final String fragmentName) {

        final String normalizedAttributeName = Node.normalizeName(attributeName);

        for (final Node rootNode : rootNodes) {
            final Extraction extraction = extractFragmentFromNode(rootNode, normalizedAttributeName, fragmentName);
            if (extraction != null) {
                return extraction;
            }
        }
        return null;

    }



    private static Extraction extractFragmentFromNode(
            final Node node, final String normalizedAttributeName, final String fragmentName) {

        if (node instanceof NestableNode) {

            final NestableNode nestableNode = (NestableNode) node;

            if (nestableNode instanceof NestableAttributeHolderNode) {
                final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode) nestableNode;
                if (attributeHolderNode.hasNormalizedAttribute(normalizedAttributeName)) {
                    final String elementAttrValue = attributeHolderNode.getAttributeValue(normalizedAttributeName);
                    if (elementAttrValue != null) {
                        final FragmentSignature fragmentSignature = FragmentSignature.parse(elementAttrValue);
                        if (fragmentSignature != null) {
                            final String signatureFragmentName = fragmentSignature.getFragmentName();
                            if (fragmentName.equals(signatureFragmentName)) {
                                return new Extraction(node, fragmentSignature);
                            }
                        }
                    }
                }
            }

            /*
             * If element does not match itself, try children
             */
            final List<Node> children = nestableNode.getChildren();
            for (final Node child : children) {
                final Extraction childResult =
                        extractFragmentFromNode(child, normalizedAttributeName, fragmentName);
                if (childResult != null) {
                    return childResult;
                }
            }

        }

        return null;

    }








    @Override
    public String toString() {
        return "(FRAGMENT SIGNATURE: " + this.attributeName +"=\"" + this.fragmentName + "\")";
    }





    private final static class Extraction {

        private Node node;
        private FragmentSignature fragmentSignature;

        Extraction(final Node node, final FragmentSignature fragmentSignature) {
            super();
            this.node = node;
            this.fragmentSignature = fragmentSignature;
        }

        private Node getNode() {
            return this.node;
        }

        private FragmentSignature getFragmentSignature() {
            return this.fragmentSignature;
        }

    }

    
}

