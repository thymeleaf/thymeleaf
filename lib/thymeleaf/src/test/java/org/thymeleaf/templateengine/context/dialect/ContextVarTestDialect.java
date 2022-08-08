/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.context.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

public class ContextVarTestDialect extends AbstractDialect implements IProcessorDialect {



    public ContextVarTestDialect() {
        super("ContextVarTestDialect");
    }

    public String getPrefix() {
        return "ctxvar";
    }


    public int getDialectProcessorPrecedence() {
        return StandardDialect.PROCESSOR_PRECEDENCE;
    }

    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new SetVarAttributeTagProcessor(dialectPrefix));
        processors.add(new WriteVarAttributeTagProcessor(dialectPrefix));
        processors.add(new ModelAttributeTagProcessor(dialectPrefix));
        processors.add(new AttrModelAttributeTagProcessor(dialectPrefix));
        processors.add(new Attr2ModelAttributeTagProcessor(dialectPrefix));
        return processors;
    }


}
