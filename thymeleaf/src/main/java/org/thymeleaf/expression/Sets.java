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
package org.thymeleaf.expression;

import java.util.Collection;
import java.util.Set;

import org.thymeleaf.util.SetUtils;


/**
 * <p>
 *   Expression Object for performing set operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #sets}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Sets {
    

    public Set<?> toSet(final Object target) {
        return SetUtils.toSet(target);
    }

    
    public int size(final Set<?> target) {
        return SetUtils.size(target);
    }
    
    
    public boolean isEmpty(final Set<?> target) {
        return SetUtils.isEmpty(target);
    }
    
    
    public boolean contains(final Set<?> target, final Object element) {
        return SetUtils.contains(target, element);
    }
    
    
    public boolean containsAll(final Set<?> target, final Object[] elements) {
        return SetUtils.containsAll(target, elements);
    }
    
    
    public boolean containsAll(final Set<?> target, final Collection<?> elements) {
        return SetUtils.containsAll(target, elements);
    }    
    
    
    
    
    public Sets() {
        super();
    }
    
}
