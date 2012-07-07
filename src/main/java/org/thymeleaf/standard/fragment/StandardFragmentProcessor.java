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

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.ElementAndAttributeNameFragmentSpec;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class StandardFragmentProcessor {

    
    

    /**
     * @deprecated Use {@link #computeStandardFragmentSpec(Configuration, ExpressionEvaluationContext, String, String, String)}
     *             instead. Will be removed in 2.1.x
     */
    @Deprecated
    public static final FragmentAndTarget computeStandardFragmentSpec(
            final Arguments arguments, final String standardFragmentSpec,  
            final String targetElementName, final String targetAttributeName) {
        return computeStandardFragmentSpec(
                arguments.getConfiguration(), arguments, standardFragmentSpec, 
                targetElementName, targetAttributeName);
    }
    

    
    
    public static final FragmentAndTarget computeStandardFragmentSpec(
            final Configuration configuration, final IProcessingContext processingContext, 
            final String standardFragmentSpec, final String targetElementName, final String targetAttributeName) {
        
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notEmpty(standardFragmentSpec, "Fragment Spec cannot be null");
        // Target element and attribute names can be null
        
        final FragmentSelection fragmentSelection =
            StandardExpressionProcessor.parseFragmentSelection(configuration, processingContext, standardFragmentSpec);
        
        final Object templateNameObject = 
            StandardExpressionProcessor.executeExpression(configuration, processingContext, fragmentSelection.getTemplateName());
        if (templateNameObject == null) {
            throw new TemplateProcessingException(
                    "Evaluation of template name from spec \"" + standardFragmentSpec + "\" " + 
                    "returned null.");
        }
        
        final String templateName = templateNameObject.toString();
        
        if (fragmentSelection.hasFragmentSelector()) {

            final Object fragmentSelectorObject = 
                StandardExpressionProcessor.executeExpression(configuration, processingContext, fragmentSelection.getFragmentSelector());
            if (fragmentSelectorObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of fragment selector from spec \"" + standardFragmentSpec + "\" " + 
                        "returned null.");
            }

            final String fragmentSelector = fragmentSelectorObject.toString();
            
            if (fragmentSelection.isXPath()) {
                
                final IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentSelector);
                return new FragmentAndTarget(templateName, fragmentSpec);
                
            }
            
            final IFragmentSpec fragmentSpec = 
                    new ElementAndAttributeNameFragmentSpec(
                            targetElementName, targetAttributeName, fragmentSelector);
            return new FragmentAndTarget(templateName, fragmentSpec);
            
        }
        
        final IFragmentSpec fragmentSpec = WholeFragmentSpec.INSTANCE;
        return new FragmentAndTarget(templateName, fragmentSpec);
        
    }


    
    
    
    
    private StandardFragmentProcessor() {
        super();
    }

    
}

