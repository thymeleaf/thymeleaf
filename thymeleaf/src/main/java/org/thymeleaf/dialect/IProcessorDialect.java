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
package org.thymeleaf.dialect;

import java.util.Set;

import org.thymeleaf.processor.IProcessor;


/**
 * <p>
 *   Base interface for all dialects providing processors ({@link IProcessor} objects) to the template engine.
 * </p>
 * <p>
 *   Dialects of this kind can specify a {@code prefix} (see {@link #getPrefix()}) which will be considered the
 *   <em>default</em> prefix for that dialect. Users can however change the prefix to be used at the moment the
 *   dialect is added to the template engine.
 * </p>
 * <p>
 *   Also, prefix can be {@code null}, in which case the dialect's processors will be acting on
 *   attributes and elements without a namespace.
 * </p>
 * <p>
 *   The <em>dialect processor precedence</em> is a dialect-wide precedence value that allows the ordering
 *   of processors in different dialects depending on the dialect they come from, i.e. allowing that all the
 *   processors from a specific dialect are executed before or after all the processors of another one, whatever
 *   the individual precedence values of the processors in the dialects might be.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see IDialect
 * @see org.thymeleaf.standard.StandardDialect
 *
 * @since 3.0.0
 *
 */
public interface IProcessorDialect extends IDialect {

    public String getPrefix();
    public int getDialectProcessorPrecedence();
    public Set<IProcessor> getProcessors(final String dialectPrefix);

}
