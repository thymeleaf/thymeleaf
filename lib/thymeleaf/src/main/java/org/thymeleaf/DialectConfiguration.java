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
package org.thymeleaf;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Configuration class for a specific {@link org.thymeleaf.dialect.IDialect}. Objects of this class
 *   specify a dialect to be used at a template engine, along with the prefix to be applied to it.
 * </p>
 * <p>
 *   When a dialect is specified WITH a prefix, this means we want that dialect's processors to match on
 *   attributes and elements that have such prefix in their names. This configured prefix will override the
 *   default prefix specified by the dialect instance itself. If the specified prefix is null, this will mean
 *   the processors will apply on elements/attributes with no prefix.
 * </p>
 * <p>
 *   When a dialect is specified WITHOUT a prefix, this means we will just use the default prefix returned by
 *   the dialect instance itself, if it applies (i.e. if it implements
 *   {@link IProcessorDialect}).
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class DialectConfiguration {

    private final boolean prefixSpecified;
    private final String prefix;
    private final IDialect dialect;



    public DialectConfiguration(final IDialect dialect) {
        super();
        // Prefix CAN be null
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefixSpecified = false;
        this.prefix = null;
        this.dialect = dialect;
    }

    public DialectConfiguration(final String prefix, final IDialect dialect) {
        super();
        // Prefix CAN be null - that will mean the dialect's processors will apply to elements/attributes without prefix
        Validate.notNull(dialect, "Dialect cannot be null");
        this.prefixSpecified = true;
        this.prefix = prefix;
        this.dialect = dialect;
    }


    
    public IDialect getDialect() {
        return this.dialect;
    }
    
    
    
    public String getPrefix() {
        return this.prefix;
    }


    public boolean isPrefixSpecified() {
        return this.prefixSpecified;
    }
    
}
