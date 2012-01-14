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
package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class AssignationSequence implements Iterable<Assignation>, Serializable {

    
    private static final long serialVersionUID = -4915282307441011014L;


    private static final char SEQUENCE_SEPARATOR_CHAR = ',';
    
    
    private final List<Assignation> assignations;
    
    
    
    private AssignationSequence(final List<Assignation> assignations) {
        super();
        Validate.notNull(assignations, "Assignation list cannot be null");
        Validate.containsNoNulls(assignations, "Assignation list cannot contain any nulls");
        this.assignations = Collections.unmodifiableList(assignations);
    }

    
    public List<Assignation> getAssignations() {
        return this.assignations;
    }
  
    public int size() {
        return this.assignations.size();
    }
    
    public Iterator<Assignation> iterator() {
        return this.assignations.iterator();
    }

    
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        if (this.assignations.size() > 0) {
            sb.append(this.assignations.get(0));
            for (int i = 1; i < this.assignations.size(); i++) {
                sb.append(SEQUENCE_SEPARATOR_CHAR);
                sb.append(this.assignations.get(i));
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
    
    
    
    static AssignationSequence parse(final String input) {
        
        if (input == null || input.trim().equals("")) {
            return null;
        }

        List<ExpressionParsingNode> result = 
            SimpleExpression.decomposeSimpleExpressionsExceptNumberLiterals(input);

        if (result == null) {
            return null;
        }
        
        result = composeSequence(result, 0);
        
        if (result == null || !result.get(0).isAssignationSequence()) {
            return null;
        }
        
        return result.get(0).getAssignationSequence();
        
    }
    
    
    
    
    private static List<ExpressionParsingNode> composeSequence(
            final List<ExpressionParsingNode> inputExprs, final int inputIndex) {

        if (inputExprs == null || inputExprs.size() == 0 || inputIndex >= inputExprs.size()) {
            return null;
        }

        final String input = inputExprs.get(inputIndex).getInput();

        
        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>();
        int currentIndex = inputExprs.size();
        
        final List<Integer> assignationIndexes = new ArrayList<Integer>();
        
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            if (c == SEQUENCE_SEPARATOR_CHAR) {
                // end assignation
                if (fragments.size() > 0) {
                    inputWithPlaceholders.append(SEQUENCE_SEPARATOR_CHAR);
                }
                assignationIndexes.add(Integer.valueOf(currentIndex));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                inputWithPlaceholders.append(String.valueOf(currentIndex++));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                fragments.add(new ExpressionParsingNode(fragment.toString()));
                fragment = new StringBuilder();
                
            } else {
                
                fragment.append(c);
               
            }
            
            
        }

        if (fragments.size() > 0) {
            inputWithPlaceholders.append(SEQUENCE_SEPARATOR_CHAR);
        }
        assignationIndexes.add(Integer.valueOf(currentIndex));
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        inputWithPlaceholders.append(String.valueOf(currentIndex++));
        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
        fragments.add(new ExpressionParsingNode(fragment.toString()));

        
        List<ExpressionParsingNode> result = inputExprs;
        result.set(inputIndex, new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);

        final List<Assignation> assignations = new ArrayList<Assignation>();
        for (final Integer assignationIndex : assignationIndexes) {
            final int assignationIdx = assignationIndex.intValue();
            result = Assignation.composeAssignation(result, assignationIdx);
            if (result == null) {
                return null;
            }
            final ExpressionParsingNode assignationNode = result.get(assignationIdx);
            if (!assignationNode.isAssignation()) {
                return null;
            }
            assignations.add(assignationNode.getAssignation());
        }
        
        
        final AssignationSequence assignationSequence = new AssignationSequence(assignations);
        result.set(inputIndex, new ExpressionParsingNode(assignationSequence));
        
        return result; 
        
    }
    
    
}

