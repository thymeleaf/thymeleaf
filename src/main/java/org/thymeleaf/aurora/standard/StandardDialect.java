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
import org.thymeleaf.aurora.standard.processor.StandardClassTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardEachTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardIncludeTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardInlineTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardInliningTextProcessor;
import org.thymeleaf.aurora.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardReplaceTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.aurora.standard.processor.StandardWithTagProcessor;

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
        processors.add(new StandardTextTagProcessor());
        processors.add(new StandardRemoveTagProcessor());
        processors.add(new StandardClassTagProcessor());
        processors.add(new StandardDefaultAttributesTagProcessor());
        processors.add(new StandardIncludeTagProcessor());
        processors.add(new StandardReplaceTagProcessor());
        processors.add(new StandardWithTagProcessor());
        processors.add(new StandardEachTagProcessor());
        processors.add(new StandardObjectTagProcessor());
        processors.add(new StandardInlineTagProcessor());
        processors.add(new StandardInliningTextProcessor());
        return processors;
    }

}
