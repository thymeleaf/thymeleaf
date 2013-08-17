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
package org.thymeleaf.standard.fragment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Object modelling the result of resolving a standard fragment specification, after all its expressions
 *   have been evaluated, its parameters parsed, etc.
 * </p>
 * <p>
 *   Note that the specified template can be null, in which case the fragment will
 *   be considered to be executed on the current template (obtained from the
 *   IProcessingContext argument in
 *   {@link #extractFragment(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext,
 *      org.thymeleaf.TemplateRepository, java.lang.String)},
 *   which will therefore need to be an instance of
 *   {@link org.thymeleaf.Arguments}).
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class StandardFragment {

    private final String templateName;
    private final IFragmentSpec fragmentSpec;
    private final Map<String,Object> parameters;


    /**
     * <p>
     *   Create a new instance of this class.
     * </p>
     *
     * @param templateName the name of the template that will be resolved and parsed, null if
     *                     fragment is to be executed on the current template.
     * @param fragmentSpec the fragment spec that will be applied to the template, once parsed.
     * @param parameters the parameters to be applied to the fragment, when processed.
     */
    public StandardFragment(final String templateName, final IFragmentSpec fragmentSpec,
                            final Map<String, Object> parameters) {
        super();
        // templateName can be null if target template is the current one
        Validate.notNull(fragmentSpec, "Fragment spec cannot be null or empty");
        this.templateName = templateName;
        this.fragmentSpec = fragmentSpec;
        this.parameters = parameters;
    }



    /**
     * <p>
     *   Returns the name of the template that will be resolved and parsed.
     * </p>
     *
     * @return the template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }



    /**
     * <p>
     *   Returns the {@link org.thymeleaf.fragment.IFragmentSpec} that will be applied to the template.
     * </p>
     *
     * @return the fragment spec.
     */
    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }



    /**
     * <p>
     *   Returns the parameters that will be applied to the fragment.
     * </p>
     *
     * @return the map of parameters. May return null if no parameters exist.
     */
    public Map<String,Object> getParameters() {
        if (this.parameters == null) {
            return null;
        }
        return Collections.unmodifiableMap(this.parameters);
    }



    /**
     * <p>
     *   Read the specified template from {@link org.thymeleaf.TemplateRepository}, and then apply
     *   the {@link org.thymeleaf.fragment.IFragmentSpec} to the result of parsing it (the template).
     * </p>
     * <p>
     *   Fragment parameters will also be processed and applied as local variables of the returned nodes.
     * </p>
     * <p>
     *   In order to execute on the current template (templateName == null), the <tt>context</tt>
     *   argument will need to be an {@link org.thymeleaf.Arguments} object.
     * </p>
     *
     * @param configuration the configuration to be used for resolving the template and
     *        processing the fragment spec.
     * @param context the processing context to be used for resolving and parsing the template.
     * @param templateRepository the template repository to be asked for the template.
     * @param fragmentSignatureAttributeName the name of the attribute in which we could expect to find a
     *        fragment
     * @return the result of parsing + applying the fragment spec.
     */
    public List<Node> extractFragment(
            final Configuration configuration, final IProcessingContext context,
            final TemplateRepository templateRepository, final String fragmentSignatureAttributeName) {

        String targetTemplateName = getTemplateName();
        if (targetTemplateName == null) {
            if (context != null && context instanceof Arguments) {
                targetTemplateName = ((Arguments)context).getTemplateName();
            } else {
                throw new TemplateProcessingException(
                        "In order to extract fragment from current template (templateName == null), processing context " +
                        "must be a non-null instance of the Arguments class (but is: " +
                        (context == null? null : context.getClass().getName()) + ")");
            }
        }

        final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(configuration, targetTemplateName, context);
        
        final Template parsedFragmentTemplate = 
                templateRepository.getTemplate(fragmentTemplateProcessingParameters);
        
        final List<Node> nodes =
                this.fragmentSpec.extractFragment(configuration, parsedFragmentTemplate.getDocument().getChildren());

        /*
         * CHECK RETURNED NODES: if there is only one node, check whether it contains a fragment signature (normally,
         * a "th:fragment" attribute). If so, let the signature process the parameters before being applied. If no
         * signature is found, then just apply the parameters to every returning node.
         */

        if (nodes == null) {
            return null;
        }

        // Detach nodes from their parents, before returning them. This might help the GC.
        for (final Node node : nodes) {
            if (node.hasParent()) {
                node.getParent().clearChildren();
            }
        }

        // Check whether this is a node specifying a fragment signature. If it is, process its parameters.
        if (nodes.size() == 1 && fragmentSignatureAttributeName != null) {
            final Node node = nodes.get(0);
            if (node instanceof NestableAttributeHolderNode) {
                final NestableAttributeHolderNode attributeHolderNode = (NestableAttributeHolderNode)node;
                if (attributeHolderNode.hasNormalizedAttribute(fragmentSignatureAttributeName)) {
                    final String attributeValue = attributeHolderNode.getAttributeValue(fragmentSignatureAttributeName);
                    if (attributeValue != null) {
                        final FragmentSignature fragmentSignature =
                                StandardExpressionProcessor.parseFragmentSignature(configuration, attributeValue);
                        if (fragmentSignature != null) {
                            final Map<String,Object> processedParameters =
                                    fragmentSignature.processParameters(this.parameters);
                            applyParameters(nodes, processedParameters);
                            return nodes;
                        }
                    }
                }
            }
        }

        applyParameters(nodes, this.parameters);

        return nodes;

    }


    private static void applyParameters(final List<Node> nodes, final Map<String,Object> parameters) {
        for (final Node node : nodes) {
            node.setAllNodeLocalVariables(parameters);
        }
    }


}

