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
package org.thymeleaf.aurora.standard;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.aurora.dialect.IProcessorDialect;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class AbstractProcessorDialect extends AbstractDialect implements IProcessorDialect {


    private final String prefix;
    private final Set<IProcessor> processors;


    protected AbstractProcessorDialect(final String name, final String prefix, final Set<IProcessor> processors) {

        super(name);

        // Prefix can be null
        Validate.notNull(processors, "Processor set cannot be null");

        this.prefix = prefix;
        this.processors = Collections.unmodifiableSet(new LinkedHashSet<IProcessor>(processors));

    }


    public String getPrefix() {
        return this.prefix;
    }

    public Set<IProcessor> getProcessors() {
        return this.processors;
    }

}
