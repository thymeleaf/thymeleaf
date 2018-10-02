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

import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Expression Object for performing boolean operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #bools}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Bools {
    



    public Bools() {
        super();
    }



    public Boolean isTrue(final Object target) {
        return Boolean.valueOf(EvaluationUtils.evaluateAsBoolean(target));
    }


    public Boolean[] arrayIsTrue(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isTrue(target[i]);
        }
        return result;
    }
    
    public List<Boolean> listIsTrue(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>(target.size() + 2);
        for (final Object element : target) {
            result.add(isTrue(element));
        }
        return result;
    }
    
    public Set<Boolean> setIsTrue(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>(target.size() + 2);
        for (final Object element : target) {
            result.add(isTrue(element));
        }
        return result;
    }

    
    
    
    public Boolean isFalse(final Object target) {
        return Boolean.valueOf(!EvaluationUtils.evaluateAsBoolean(target));
    }
    
    public Boolean[] arrayIsFalse(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isFalse(target[i]);
        }
        return result;
    }
    
    public List<Boolean> listIsFalse(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>(target.size() + 2);
        for (final Object element : target) {
            result.add(isFalse(element));
        }
        return result;
    }
    
    public Set<Boolean> setIsFalse(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>(target.size() + 2);
        for (final Object element : target) {
            result.add(isFalse(element));
        }
        return result;
    }
    
    
    
    
    
    
    public Boolean arrayAnd(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object aTarget : target) {
            if (!isTrue(aTarget).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
    
    public Boolean listAnd(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object element : target) {
            if (!isTrue(element).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
    
    public Boolean setAnd(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object element : target) {
            if (!isTrue(element).booleanValue()) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    
    
    
    
    public Boolean arrayOr(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object aTarget : target) {
            if (isTrue(aTarget).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    public Boolean listOr(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object element : target) {
            if (isTrue(element).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    public Boolean setOr(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        for (final Object element : target) {
            if (isTrue(element).booleanValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }



}
