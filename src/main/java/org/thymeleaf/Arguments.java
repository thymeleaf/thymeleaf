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
package org.thymeleaf;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.dom.Document;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluationContext;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Objects of this class contain all the required arguments for template
 *   processing.
 * </p>
 * <p>
 *   These objects are created internally by the Template Engine in order
 *   to provide processors with all the information
 *   they might need for performing their tasks.
 * </p>
 * <p>
 *   Arguments include many attributes, among which some should be used internally
 *   only, and never by developers of attribute/element processors. Public
 *   attributes are:
 * </p>
 * <ul>
 *   <li>The Template Engine configuration ({@link Configuration}): {@link #getConfiguration()}</li>
 *   <li>The template name: {@link #getTemplateName()}</li>
 *   <li>The Context ({@link IContext}): {@link #getContext()}</li>
 *   <li>The current map of ID Counts (used for adding a unique index to repeated <tt>id</tt> attributes): {@link #getIdCounts()}</li>
 *   <li>The Expression roots:
 *       <ul>
 *         <li>For normal (non selected) evaluation (<tt>${...}</tt> in standard dialect): {@link #getExpressionEvaluationRoot()}</li>
 *         <li>For selected evaluation (<tt>*{...}</tt> in standard dialect): {@link #getExpressionSelectionEvaluationRoot()}</li>
 *       </ul>
 *   </li>
 *   <li>Information about whether processing non-element nodes is allowed at a specific point of a template
 *       execution or not (default is to process only elements).</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Arguments {

    /**
     * @deprecated Use {@link ExpressionEvaluationContext.SELECTION_TARGET_LOCAL_VARIABLE_NAME}
     *             instead. 
     */
    @Deprecated
    public static final String SELECTION_TARGET_LOCAL_VARIABLE_NAME = "%%{SELECTION_TARGET}%%";
    
    
    private final TemplateProcessingParameters templateProcessingParameters;
    private final Configuration configuration;
    private final Document document;
    private final TemplateResolution templateResolution;
    private final TemplateRepository templateRepository;
    
    private final ExpressionEvaluationContext expressionEvaluationContext;
    private final Map<String,Integer> idCounts;
    
    private final boolean processOnlyElementNodes;
    private final Map<String, Object> baseContextVariables;

    

    /**
     * <p>
     *   Create a new Arguments instance.
     * </p>
     * <p>
     *   <b>Mainly for internal use</b>. Should not be called directly except for testing purposes
     *   or  when processing a template (e.g. a fragment) using the {@link TemplateEngine}
     *   from a element/attribute processor.
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @param templateResolution the template resolution object
     * @param templateRepository the template repository in use
     * @param templateParser the template parser
     * @param context the context
     */
    public Arguments(
            final TemplateProcessingParameters templateProcessingParameters,
            final TemplateResolution templateResolution,
            final TemplateRepository templateRepository,
            final Document document) {
        
        super();
        
        Validate.notNull(templateProcessingParameters, "Template processing parameters cannot be null");
        Validate.notNull(templateResolution, "Template resolution cannot be null");
        Validate.notNull(templateRepository, "Template repository cannot be null");
        // Document CAN be null, if it has been filtered and nothing has been selected as a result.
        
        this.templateProcessingParameters = templateProcessingParameters;
        this.configuration = this.templateProcessingParameters.getConfiguration();
        this.templateResolution = templateResolution;
        this.templateRepository = templateRepository;
        this.document = document;
        this.expressionEvaluationContext =
                new ExpressionEvaluationContext(this.templateProcessingParameters.getContext());
        this.idCounts = new HashMap<String,Integer>();
        
        this.processOnlyElementNodes = true;
        this.baseContextVariables = 
            ExpressionEvaluatorObjects.computeEvaluationVariablesForArguments(this);
        
    }
    


    private Arguments(
            final TemplateProcessingParameters templateProcessingParameters,
            final TemplateResolution templateResolution,
            final TemplateRepository templateRepository,
            final Document document,
            final ExpressionEvaluationContext expressionEvaluationContext,
            final Map<String,Integer> idCounts,
            final boolean processOnlyElementNodes) {
        
        super();
        
        this.templateProcessingParameters = templateProcessingParameters;
        this.configuration = this.templateProcessingParameters.getConfiguration();
        this.templateResolution = templateResolution;
        this.templateRepository = templateRepository;
        this.document = document;
        this.expressionEvaluationContext = expressionEvaluationContext;
        this.idCounts = idCounts;
        
        this.processOnlyElementNodes = processOnlyElementNodes;
        this.baseContextVariables = 
                ExpressionEvaluatorObjects.computeEvaluationVariablesForArguments(this);
        
    }
    


    

    
    
    /**
     * <p>
     *   Returns the Template Processing Parameters used for resolving
     *   and parsing the template being processed.
     * </p>
     * 
     * @return the template processing parameters
     */
    public TemplateProcessingParameters getTemplateProcessingParameters() {
        return this.templateProcessingParameters;
    }



    /**
     * <p>
     *   Returns the Template Engine configuration being used for
     *   processing templates.
     * </p>
     * 
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    /**
     * <p>
     *   Returns the template resolution object corresponding to the
     *   template currently being processed.
     * </p>
     * 
     * @return the Template Resolution object
     */
    public TemplateResolution getTemplateResolution() {
        return this.templateResolution;
    }
    
    /**
     * <p>
     *   Returns the template repository in use.
     * </p>
     * 
     * @return the Template Repository object
     */
    public TemplateRepository getTemplateRepository() {
        return this.templateRepository;
    }
    
    /**
     * <p>
     *   Returns the parsed Document DOM object.
     * </p>
     * 
     * @return the Document object
     */
    public Document getDocument() {
        return this.document;
    }
    
    /**
     * <p>
     *   Returns the name of the template currently being processed.
     * </p>
     * 
     * @return the template name
     */
    public String getTemplateName() {
        return this.templateResolution.getTemplateName();
    }

    
    
    /**
     * <p>
     *   Returns the current context specified for template processing.
     * </p>
     * 
     * @return the current context
     */
    public IContext getContext() {
        return this.expressionEvaluationContext.getContext();
    }
    
    
    /**
     * <p>
     *   Returns whether local variables have currently been specified or not.
     *   (e.g. <tt>th:with</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there are local variables, false if not
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will
     *             be removed in 2.1.x
     */
    @Deprecated
    public boolean hasLocalVariables() {
        return this.expressionEvaluationContext.hasLocalVariables();
    }
    
    
    /**
     * <p>
     *   Returns the expression evaluation context. 
     * </p>
     * 
     * @return true if there are local variables, false if not
     * 
     * @since 2.0.9
     * 
     */
    public ExpressionEvaluationContext getExpressionEvaluationContext() {
        return this.expressionEvaluationContext;
    }

    
    
    
    /**
     * <p>
     *   Returns the value of a local variable.
     * </p>
     * 
     * @param variableName the name of the local variable to be returned
     * @return the value of the variable, or null if the variable does not exist (or has null value)
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     * 
     */
    @Deprecated
    public Object getLocalVariable(final String variableName) {
        return this.expressionEvaluationContext.getLocalVariable(variableName);
    }
    
    
    /**
     * <p>
     *   Returns whether a specific local variable is defined or not.
     * </p>
     * 
     * @return true if the variable is currently defined, false if not.
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public boolean hasLocalVariable(final String variableName) {
        return this.expressionEvaluationContext.hasLocalVariable(variableName);
    }
    
    
    /**
     * <p>
     *   Returns the real inner map of local variables. This
     *   method should not be called directly.
     * </p>
     * 
     * @return the local variables map, which could be null if no variables are defined
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public HashMap<String,Object> unsafeGetLocalVariables() {
        return this.expressionEvaluationContext.unsafeGetLocalVariables();
    }

    
    /**
     * <p>
     *   Returns a safe copy of the map of local variables.
     * </p>
     * 
     * @return the local variables
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public Map<String,Object> getLocalVariables() {
        return this.expressionEvaluationContext.getLocalVariables();
    }


    /**
     * <p>
     *   Returns whether only element nodes should be processed (as opposed
     *   to texts, CDATAs, comments, etc.). Default is true.
     * </p>
     * 
     * @return whether only element nodes will be processed
     */
    public boolean getProcessOnlyElementNodes() {
        return this.processOnlyElementNodes;
    }
    

    /**
     * <p>
     *   Returns whether there currently is a selection going on
     *   (e.g. <tt>th:object</tt> in standard dialect). 
     * </p>
     * 
     * @return true if there is a selection currently established, false if not
     * 
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public boolean hasSelectionTarget() {
        return this.expressionEvaluationContext.hasSelectionTarget();
    }
    
    
    /**
     * <p>
     *   Returns the <i>selection target object</i>, and raises an
     *   exception if there isn't any.
     * </p>
     * <p>
     *   Meant for internal use.
     * </p>
     * 
     * @return the selection target object
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public Object getSelectionTarget() {
        return this.expressionEvaluationContext.getSelectionTarget();
    }

    
    /**
     * <p>
     *   Returns the map of <i>ID counts</i>.
     * </p>
     * <p>
     *   These numbers are used to avoid conflicts among elements with the same <tt>id</tt>
     *   attribute; for example elements being repeated as a part of a <tt>th:each</tt> iteration.
     * </p>
     * 
     * @return the current map of ID counts
     */
    public Map<String,Integer> getIdCounts() {
        return this.idCounts;
    }

    
    /**
     * <p>
     *   Returns the execution attributes.
     * </p>
     * 
     * @return the current map of execution attributes
     */
    public Map<String,Object> getExecutionAttributes() {
        return this.configuration.getExecutionAttributes();
    }

    
    /**
     * <p>
     *   Returns the execution attribute with the specified name.
     * </p>
     * 
     * @param attributeName the name of the attribute to be retrieved
     * @return the value of the attribute
     */
    public Object getExecutionAttribute(final String attributeName) {
        return this.configuration.getExecutionAttributes().get(attributeName);
    }
    
    
    /**
     * <p>
     *   Returns the current evaluation root. This is the object on which expressions
     *   (normal expressions, like those specified in the standard dialect with
     *   <tt>${...}</tt>) are executed.
     * </p>
     * 
     * @return the expression evaluation root
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public Object getExpressionEvaluationRoot() {
        return this.expressionEvaluationContext.getExpressionEvaluationRoot();
    }

    
    /**
     * <p>
     *   Returns the current selection evaluation root. This is the object on which selection expressions
     *   (like those specified in the standard dialect with <tt>*{...}</tt>) are executed.
     * </p>
     * 
     * @return the selection evaluation root
     * @deprecated use {@link #getExpressionEvaluationContext()} instead. Will be
     *             removed in 2.1.x
     */
    @Deprecated
    public Object getExpressionSelectionEvaluationRoot() {
        return this.expressionEvaluationContext.getExpressionSelectionEvaluationRoot();
    }
    

    /**
     * <p>
     *   Returns the map of base variables that should be made available to every expression
     *   evaluation operation (whenever variable evaluation is available).
     * </p>
     * 
     * @since 2.0.0
     * @return the map of variables (a new object, mutable, safe to use as a context variables base)
     */
    public Map<String,Object> getBaseContextVariables() {
        final Map<String,Object> variables = new HashMap<String, Object>();
        variables.putAll(this.baseContextVariables);
        return variables;
    }
    
    
    
    
    /**
     * <p>
     *   Returns a new index (ID count) for a specific
     *   value of the <tt>id</tt> attribute, and increments
     *   the count.
     * </p>
     * 
     * @param id the ID for which the count will be computed
     * @return the new count, ready to be used
     */
    public Integer getAndIncrementIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = Integer.valueOf(1);
        }
        this.idCounts.put(id, Integer.valueOf(count.intValue() + 1));
        return count;
    }
    
    
    /**
     * <p>
     *   Returns the index (ID count) for a specific
     *   value of the <tt>id</tt> attribute without incrementing
     *   the count.
     * </p>
     * 
     * @param id the ID for which the count will be retrieved
     * @return the current count
     */
    public Integer getNextIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = Integer.valueOf(1);
        }
        return count;
    }
    
    
    /**
     * <p>
     *   Returns the last index (ID count) returned for a specific
     *   value of the <tt>id</tt> attribute (without incrementing
     *   the count).
     * </p>
     * 
     * @param id the ID for which the last count will be retrieved
     * @return the count
     */
    public Integer getPreviousIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            throw new TemplateProcessingException(
                    "Cannot obtain previous ID count for ID \"" + id + "\"");
        }
        return Integer.valueOf(count.intValue() - 1);
    }
    
    
    
    /**
     * <p>
     *   Creates a new Arguments object by adding some new local variables 
     *   to the existing map (the rest of the attributes are copied verbatim).
     * </p>
     * 
     * @param newVariables the new variables
     * @return the new Arguments object
     */
    public Arguments addLocalVariables(final Map<String,Object> newVariables) {
        final ExpressionEvaluationContext newExpressionEvaluationContext = 
                this.expressionEvaluationContext.addLocalVariables(newVariables);
        final Arguments arguments = 
            new Arguments(this.templateProcessingParameters, this.templateResolution, 
                    this.templateRepository, this.document, newExpressionEvaluationContext, 
                    this.idCounts, this.processOnlyElementNodes);
        return arguments;
    }

    

    /**
     * <p>
     *   Creates a new Arguments object by setting a new value for the <tt>processOnlyElementNodes</tt> flag.
     * </p>
     * 
     * @param shouldProcessOnlyElementNodes whether only element nodes should be processed from this moment in template execution
     * @return the new Arguments object
     */
    public Arguments setProcessOnlyElementNodes(final boolean shouldProcessOnlyElementNodes) {
        final Arguments arguments = 
            new Arguments(this.templateProcessingParameters, this.templateResolution, 
                    this.templateRepository, this.document, this.expressionEvaluationContext, 
                    this.idCounts, shouldProcessOnlyElementNodes);
        return arguments;
    }

    

    /**
     * <p>
     *   Creates a new Arguments object by setting new local variables and a new value for the <tt>processOnlyElementNodes</tt> flag.
     * </p>
     * 
     * @param newVariables the new local variables
     * @param shouldProcessOnlyElementNodes whether only element nodes should be processed from this moment in template execution
     * @return the new Arguments object
     */
    public Arguments addLocalVariablesAndProcessOnlyElementNodes(final Map<String,Object> newVariables, final boolean shouldProcessOnlyElementNodes) {
        final ExpressionEvaluationContext newExpressionEvaluationContext = 
                this.expressionEvaluationContext.addLocalVariables(newVariables);
        final Arguments arguments = 
            new Arguments(this.templateProcessingParameters, this.templateResolution, 
                    this.templateRepository, this.document, newExpressionEvaluationContext, 
                    this.idCounts, shouldProcessOnlyElementNodes);
        return arguments;
    }

    
    
    
}
