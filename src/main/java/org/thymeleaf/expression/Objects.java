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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Expression Object for performing operations related to general object management inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #objects}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Objects {
    
    

    public <T> T nullSafe(final T target, final T defaultValue) {
        return ObjectUtils.nullSafe(target, defaultValue);
    }

    public <T> T[] arrayNullSafe(final T[] target, final T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        final T[] result = target.clone();
        for (int i = 0; i < target.length; i++) {
            result[i] = nullSafe(target[i], defaultValue);
        }
        return result;
    }

    public <T> List<T> listNullSafe(final List<T> target, final T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        final List<T> result = new ArrayList<T>(target.size() + 2);
        for (final T element : target) {
            result.add(nullSafe(element, defaultValue));
        }
        return result;
    }

    public <T> Set<T> setNullSafe(final Set<T> target, final T defaultValue) {
        Validate.notNull(target, "Target cannot be null");
        final Set<T> result = new LinkedHashSet<T>(target.size() + 2);
        for (final T element : target) {
            result.add(nullSafe(element, defaultValue));
        }
        return result;
    }
    
    
    
    public Objects() {
        super();
    }
    
}
