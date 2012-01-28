/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.processor.ProcessorAndContext;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.util.IdentityCounter;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class Node implements Serializable {

    private static final long serialVersionUID = 3082306990735650683L;
    
    private static final int DEFAULT_NODE_LOCAL_VARIABLES_MAP_SIZE = 3;

    private final String documentName;
    private final Integer lineNumber;
    
    private final boolean shouldConsiderAsElementForProcessing;
    
    protected NestableNode parent;
    private boolean skippable;
    private boolean precomputed;
    private boolean recomputeProcessorsAfterEachExecution;
    private boolean recomputeProcessorsImmediately;
    
    private HashMap<String,Object> nodeLocalVariables;

    private ArrayList<ProcessorAndContext> processors;

    
    public static String normalizeName(final String name) {
        if (name == null) {
            return null;
        }
        return name.toLowerCase();
    }

    
    public static String applyDialectPrefix(final String name, final String dialectPrefix) {
        if (name == null) {
            return null;
        }
        if (dialectPrefix == null || dialectPrefix.trim().equals("")) {
            return name;
        }
        return dialectPrefix + ":" + name;
    }
    
    
    
    protected Node(final String documentName, final Integer lineNumber) {
        super();
        this.documentName = documentName;
        this.lineNumber = lineNumber;
        this.skippable = false;
        this.precomputed = false;
        this.recomputeProcessorsAfterEachExecution = false;
        this.recomputeProcessorsImmediately = false;
        this.nodeLocalVariables = null;
        this.processors = null;
        this.shouldConsiderAsElementForProcessing = (this instanceof Element || this instanceof Document);
    }


    
    public String getDocumentName() {
        return this.documentName;
    }
    

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    
    
    public final boolean hasParent() {
        return this.parent != null;
    }
    
    public final NestableNode getParent() {
        return this.parent;
    }
    
    public final void setParent(final NestableNode parent) {
        this.parent = parent;
    }
    

    public final boolean getRecomputeProcessorsAfterEachExecution() {
        return this.recomputeProcessorsAfterEachExecution;
    }


    public final void setRecomputeProcessorsAfterEachExecution(final boolean recomputeProcessorsAfterEachExecution) {
        this.recomputeProcessorsAfterEachExecution = recomputeProcessorsAfterEachExecution;
    }


    
    public final boolean getRecomputeProcessorsImmediately() {
        return this.recomputeProcessorsImmediately;
    }


    public final void setRecomputeProcessorsImmediately(final boolean recomputeProcessorsImmediately) {
        this.recomputeProcessorsImmediately = recomputeProcessorsImmediately;
    }

    
    

    public final boolean isSkippable() {
        return this.skippable;
    }
    
    
    public final void setSkippable(final boolean skippable) {
        this.skippable = skippable;
        if (!skippable && hasParent()) {
            // If this node is marked as non-skippable, set its parent as
            // non-skippable too.
            if (this.parent.isSkippable()) {
                this.parent.setSkippable(false);
            }
        }
        doAdditionalSkippableComputing(skippable);
    }
    
    abstract void doAdditionalSkippableComputing(final boolean isSkippable);


    protected boolean isDetached() {
        if (this instanceof Document) {
            return false;
        }
        return !hasParent();
    }
    
    
    final boolean isPrecomputed() {
        return this.precomputed;
    }
    
    final void setPrecomputed(final boolean precomputed) {
        this.precomputed = precomputed;
    }

    
    
    
    
    public final boolean hasNodeLocalVariables() {
        return this.nodeLocalVariables != null && this.nodeLocalVariables.size() > 0;
    }
    
    public final Map<String,Object> getNodeLocalVariables() {
        return this.nodeLocalVariables;
    }

    public final void addNodeLocalVariable(final String name, final Object value) {
        if (this.nodeLocalVariables == null) {
            this.nodeLocalVariables = new HashMap<String, Object>(DEFAULT_NODE_LOCAL_VARIABLES_MAP_SIZE);
        }
        this.nodeLocalVariables.put(name,  value);
    }

    public final void addNodeLocalVariables(final Map<String,Object> variables) {
        if (variables != null) {
            for (final Map.Entry<String,Object> variablesEntry : variables.entrySet()) {
                addNodeLocalVariable(variablesEntry.getKey(), variablesEntry.getValue());
            }
        }
    }

    final void setNodeLocalVariables(final Map<String,Object> variables) {
        if (variables != null) {
            this.nodeLocalVariables = new HashMap<String,Object>(variables);
        } else { 
            this.nodeLocalVariables = null;
        }
    }



    
    final void precomputeNode(final Configuration configuration) {

        if (!isPrecomputed()) {

            /*
             * Compute the processors that are applicable to this node
             */
            this.processors = configuration.computeProcessorsForNode(this);

            
            /*
             * Set skippability
             */
            if (this.processors == null || this.processors.size() == 0) {
                // We only set this specific node as skippable. If we executed
                // "setSkippable", the whole tree would be set as skippable, which
                // is unnecessary due to the fact that we are going to precompute
                // all of this node's children in a moment.
                // Also, note that if any of this node's children has processors
                // (and therefore sets itself as "non-skippable"), it will also
                // set its parent as non-skippable, overriding this action.
                this.skippable = true;
            } else {
                // This time we execute "setSkippable" so that all parents at all
                // levels are also set to "false"
                setSkippable(false);
            }

            
            /*
             * Set the "precomputed" flag to true 
             */
            setPrecomputed(true);

        }
        
        
        /*
         * Let subclasses add their own preprocessing
         */
        doAdditionalPrecomputeNode(configuration);
     
    }
    
    
    abstract void doAdditionalPrecomputeNode(final Configuration configuration);

    
    
    
    
    void processNode(final Arguments arguments, final boolean processOnlyElementNodes) {

        if (!this.shouldConsiderAsElementForProcessing && processOnlyElementNodes) {
            return;
        }
        
        if (this.recomputeProcessorsImmediately || this.recomputeProcessorsAfterEachExecution) {
            precomputeNode(arguments.getConfiguration());
            this.recomputeProcessorsImmediately = false;
        }
        
        if (!isPrecomputed()) {
            precomputeNode(arguments.getConfiguration());
        }

        if (!isSkippable()) {
                        
            /*
             *  If there are local variables at the node, add them to the ones at the
             *  Arguments object.
             */
            Arguments executionArguments =
                    (this.nodeLocalVariables != null && this.nodeLocalVariables.size() > 0?
                            arguments.addLocalVariables(this.nodeLocalVariables) : arguments);
            
            /* 
             * If the Arguments object has local variables, synchronize the node-local
             * variables map.
             */
            if (executionArguments.hasLocalVariables()) {
                setNodeLocalVariables(executionArguments.getLocalVariables());
            }
            
            /*
             * Perform the actual processing
             */
            if (!isDetached() && this.processors != null && this.processors.size() > 0) {
                
                final IdentityCounter<ProcessorAndContext> alreadyExecuted = new IdentityCounter<ProcessorAndContext>(3);
                Arguments processingArguments = executionArguments;

                while (!isDetached() && processingArguments != null) {
                    
                    // This way of executing processors allows processors to perform updates
                    // that might change which processors should be applied (for example, by
                    // adding or removing attributes)
                    processingArguments = 
                            applyNextProcessor(processingArguments, this, alreadyExecuted);
                    
                    if (processingArguments != null) {
                        // if we didn't reach the end of processor executions, update
                        // the Arguments object being used for processing
                        executionArguments = processingArguments;
                    }
                    
                    if (this.recomputeProcessorsImmediately || this.recomputeProcessorsAfterEachExecution) {
                        precomputeNode(arguments.getConfiguration());
                        this.recomputeProcessorsImmediately = false;
                    }
                    
                }
                
            }
            
            doAdditionalProcess(executionArguments, processOnlyElementNodes);
            
        }
    
    }
    
    
    
    
    
    private static final Arguments applyNextProcessor(final Arguments arguments, final Node node, final IdentityCounter<ProcessorAndContext> alreadyExecuted) {

        if (!node.isDetached() && node.processors != null && node.processors.size() > 0) {

            for (final ProcessorAndContext processor : node.processors) {
                
                if (!alreadyExecuted.isAlreadyCounted(processor)) {
                    
                    Arguments executionArguments = arguments;

                    final ProcessorResult attrProcessorResult = 
                            processor.getProcessor().process(executionArguments, processor.getContext(), node);
                    
                    // The execution arguments need to be updated as instructed by the processor
                    // (for example, for adding local variables)
                    executionArguments = attrProcessorResult.computeNewArguments(executionArguments);
                    
                    // If we have added local variables, we should update the node's map for them in
                    // order to keep them synchronized
                    if (attrProcessorResult.hasLocalVariables()) {
                        node.setNodeLocalVariables(executionArguments.getLocalVariables());
                    }
                    
                    // Make sure this specific processor instance is not executed again
                    alreadyExecuted.count(processor);
                    
                    return executionArguments;
                    
                }
                
            }
            
        }

        // Either there are no processors, or all of them have already been processed
        return null;
        
    }
    
    
    
    abstract void doAdditionalProcess(final Arguments arguments, final boolean processOnlyElementNodes);
    
    

    
    public final Node cloneNode(final NestableNode newParent, final boolean cloneProcessors) {
        final Node clone = createClonedInstance(newParent, cloneProcessors);
        cloneNodeInternals(clone, newParent, cloneProcessors);
        return clone;
    }
    
    

    abstract Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors);
    
    
    final void cloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        doCloneNodeInternals(node, newParent, cloneProcessors);
        if (cloneProcessors) {
            node.processors = this.processors;
            node.skippable = this.skippable;
            node.precomputed = this.precomputed;
        } else {
            node.processors = null;
            node.skippable = false;
            node.precomputed = false;
        }
        node.parent = newParent;
        if (this.nodeLocalVariables != null) {
            node.nodeLocalVariables = new HashMap<String, Object>(this.nodeLocalVariables);
        }
    }

    
    abstract void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors);
    
    
    
    public abstract void visit(final DOMVisitor visitor);

    
    

}
