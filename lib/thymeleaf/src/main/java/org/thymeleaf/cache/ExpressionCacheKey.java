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
    private final String expression0;
    private final String expression1;
    private final int h;


    public ExpressionCacheKey(final String type, final String expression0) {
        this(type, expression0, null);
    }

    public ExpressionCacheKey(final String type, final String expression0, final String expression1) {

        super();

        Validate.notNull(type, "Type cannot be null");
        Validate.notNull(expression0, "Expression cannot be null");

        this.type = type;
        this.expression0 = expression0;
        this.expression1 = expression1;

        // This being a cache key, its equals and hashCode methods will potentially execute many
        // times, so this could help performance
        this.h = computeHashCode();

    }

    public String getType() {
        return this.type;
    }

    public String getExpression0() {
        return this.expression0;
    }

    public String getExpression1() {
        return this.expression1;
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
        if (!this.expression0.equals(that.expression0)) {
            return false;
        }
        return this.expression1 != null ? this.expression1.equals(that.expression1) : that.expression1 == null;

    }


    @Override
    public int hashCode() {
        return this.h;
    }


    private int computeHashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.expression0.hashCode();
        result = 31 * result + (this.expression1 != null ? this.expression1.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.type);
        strBuilder.append('|');
        strBuilder.append(this.expression0);
        if (this.expression1 != null) {
            strBuilder.append('|');
            strBuilder.append(this.expression1);
        }
        return strBuilder.toString();
    }

}
