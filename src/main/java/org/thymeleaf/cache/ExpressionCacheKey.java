/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.cache;

import java.io.Serializable;

import org.thymeleaf.util.Validate;


/**
 * <p>
 *   This class models objects used as keys in the Expression Cache.
 * </p>
 * <p>
 *   Objects of this class <strong>should only be created from inside the engine</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 */
public final class ExpressionCacheKey implements Serializable {

    private static final long serialVersionUID = 872451230923L;

    private final String type;
    private final String expression;
    private final int h;


    public ExpressionCacheKey(final String type, final String expression) {

        super();

        Validate.notNull(type, "Type cannot be null");
        Validate.notNull(expression, "Expression cannot be null");

        this.type = type;
        this.expression = expression;

        // This being a cache key, its equals and hashCode methods will potentially execute many
        // times, so this could help performance
        this.h = computeHashCode();

    }

    public String getType() {
        return this.type;
    }

    public String getExpression() {
        return this.expression;
    }


    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof ExpressionCacheKey)) {
            return false;
        }

        final ExpressionCacheKey that = (ExpressionCacheKey) o;

        if (this.h != that.h) { // fail fast
            return false;
        }

        if (!this.type.equals(that.type)) {
            return false;
        }
        return this.expression.equals(that.expression);

    }


    @Override
    public int hashCode() {
        return this.h;
    }


    private int computeHashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.expression.hashCode();
        return result;
    }




    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.type);
        strBuilder.append('|');
        strBuilder.append(this.expression);
        return strBuilder.toString();
    }

}
