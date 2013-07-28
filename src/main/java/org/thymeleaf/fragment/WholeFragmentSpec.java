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
import java.util.List;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.FragmentSelection;


/**
 * <p>
 *   Implementation of {@link IFragmentSpec} that simply returns 
 *   <i>whole templates</i>, this is the same nodes used as input with
 *   no modification, traversing or selection of any kind.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class WholeFragmentSpec implements IFragmentSpec {
    
    /**
     * <p>
     *   Singleton instance.
     * </p>
     * <p>
     *   This instance is <b>thread-safe</b>.
     * </p>
     */
    public static final WholeFragmentSpec INSTANCE = new WholeFragmentSpec();



    private final Map<String,Object> parameterValues;



    /**
     * <p>
     *   Creates a new instance. In most cases {@link #INSTANCE} can be used
     *   instead of creating new objects of this class.
     * </p>
     */
    public WholeFragmentSpec() {
        this(null);
    }


    /**
     * <p>
     *   Creates a new instance. In cases when there are no parameters, {@link #INSTANCE} can be used
     *   instead of creating new objects of this class.
     * </p>
     * <p>
     *   This constructor allows the specification of a series of fragment parameters, which will be applied
     *   as local variables to the extracted nodes.
     * </p>
     *
     * @param parameterValues the fragment parameters, which will be applied as local variables to the nodes
     *                        returned as extraction result. Might be null if no parameters are applied.
     *
     * @since 2.1.0
     */
    public WholeFragmentSpec(final Map<String,Object> parameterValues) {

        super();

        this.parameterValues = parameterValues;

        if (this.parameterValues != null && this.parameterValues.size() > 0) {
            if (FragmentSelection.parameterNamesAreSynthetic(this.parameterValues.keySet())) {
                throw new TemplateProcessingException(
                        "Cannot process fragment selection parameters " + this.parameterValues.toString() + ", " +
                                "as they are specified for a whole-template fragment selector, " +
                                "but using synthetic (non-named) parameter is only allowed for fragment-signature-based " +
                                "(e.g. 'th:fragment') selection");
            }
        }

    }



    /**
     * <p>
     *   Returns the map of parameter values that will be applied as local variables to the extracted nodes.
     * </p>
     *
     * @return the map of parameters.
     * @since 2.1.0
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
     * @since 2.1.0
     */
    public boolean hasParameterValues() {
        return this.parameterValues != null && this.parameterValues.size() > 0;
    }




    
    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        applyParameters(nodes, this.parameterValues);
        return nodes;
    }



    private static void applyParameters(final List<Node> nodes, final Map<String,Object> parameterValues) {
        for (final Node node : nodes) {
            node.setAllNodeLocalVariables(parameterValues);
        }
    }


    
    @Override
    public String toString() {
        return "(WHOLE TEMPLATE)";
    }
    
}

