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
package org.thymeleaf.processor;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.util.Validate;





/**
 * <p>
 *   Models the context in which a node is matched.
 * </p>
 * <p>
 *   These objects are only applied when the node is an {@link org.thymeleaf.dom.Element}.
 *   This is needed because a processor's applicability specifies an element or attribute name,
 *   but dialects might be configured with any prefix, and this will affect matching capabilities
 *   because final names will be composed of both prefix and name.
 * </p>
 * <p>
 *   Matching contexts help matchers know the circumstances in which a node is trying to match,
 *   specifically the prefix being applied to the dialect.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class ProcessorMatchingContext {
    
    private final IDialect dialect;
    private final String dialectPrefix;
    
    
    public ProcessorMatchingContext(final IDialect dialect, final String dialectPrefix) {
        super();
        Validate.notNull(dialect, "Dialect cannot be null");
        Validate.notNull(dialectPrefix, "Dialect Prefix cannot be null");
        this.dialect = dialect;
        this.dialectPrefix = dialectPrefix;
    }

    public IDialect getDialect() {
        return this.dialect;
    }

    public String getDialectPrefix() {
        return this.dialectPrefix;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.dialect == null) ? 0 : this.dialect.hashCode());
        result = prime * result
                + ((this.dialectPrefix == null) ? 0 : this.dialectPrefix.hashCode());
        return result;
    }

    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessorMatchingContext other = (ProcessorMatchingContext) obj;
        if (this.dialect == null) {
            if (other.dialect != null) {
                return false;
            }
        } else if (!this.dialect.equals(other.dialect)) {
            return false;
        }
        if (this.dialectPrefix == null) {
            if (other.dialectPrefix != null) {
                return false;
            }
        } else if (!this.dialectPrefix.equals(other.dialectPrefix)) {
            return false;
        }
        return true;
    }
    
}
