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

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.aurora.dialect.AbstractProcessorDialect;
import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.standard.processor.StandardClassProcessor;
import org.thymeleaf.aurora.standard.processor.StandardDefaultAttributesProcessor;
import org.thymeleaf.aurora.standard.processor.StandardIncludeProcessor;
import org.thymeleaf.aurora.standard.processor.StandardRemoveProcessor;
import org.thymeleaf.aurora.standard.processor.StandardReplaceProcessor;
import org.thymeleaf.aurora.standard.processor.StandardTextProcessor;
import org.thymeleaf.aurora.standard.processor.StandardWithProcessor;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardDialect extends AbstractProcessorDialect {

    private static final String NAME = "Standard";
    private static final String PREFIX = "th";


    public StandardDialect() {
        super(NAME, PREFIX, buildProcessorSet());
    }


    private static Set<IProcessor> buildProcessorSet() {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new StandardTextProcessor());
        processors.add(new StandardRemoveProcessor());
        processors.add(new StandardClassProcessor());
        processors.add(new StandardDefaultAttributesProcessor());
        processors.add(new StandardIncludeProcessor());
        processors.add(new StandardReplaceProcessor());
        processors.add(new StandardWithProcessor());
        return processors;
    }

}
