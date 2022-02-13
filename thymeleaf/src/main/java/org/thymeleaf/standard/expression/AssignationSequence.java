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

import java.io.Serializable;
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


    private final List<Assignation> assignations;
    
    
    
    AssignationSequence(final List<Assignation> assignations) {
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
                sb.append(',');
                sb.append(this.assignations.get(i));
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
    

}

