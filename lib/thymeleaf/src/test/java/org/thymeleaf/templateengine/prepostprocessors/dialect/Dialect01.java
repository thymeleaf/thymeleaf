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
package org.thymeleaf.templateengine.prepostprocessors.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.postprocessor.PostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.preprocessor.PreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class Dialect01 extends AbstractDialect implements IPreProcessorDialect, IPostProcessorDialect {


    public Dialect01() {
        super("Dialect01");
    }



    @Override
    public int getDialectPreProcessorPrecedence() {
        return 100;
    }


    @Override
    public Set<IPreProcessor> getPreProcessors() {
        final Set<IPreProcessor> preProcessors = new HashSet<IPreProcessor>();
        preProcessors.add(new PreProcessor(TemplateMode.HTML, Dialect01PreProcessor.class, 1000));
        preProcessors.add(new PreProcessor(TemplateMode.XML, Dialect01PreProcessor.class, 1000));
        preProcessors.add(new PreProcessor(TemplateMode.TEXT, Dialect01PreProcessor.class, 1000));
        preProcessors.add(new PreProcessor(TemplateMode.JAVASCRIPT, Dialect01PreProcessor.class, 1000));
        preProcessors.add(new PreProcessor(TemplateMode.CSS, Dialect01PreProcessor.class, 1000));
        return preProcessors;
    }

    @Override
    public int getDialectPostProcessorPrecedence() {
        return 100;
    }

    @Override
    public Set<IPostProcessor> getPostProcessors() {
        final Set<IPostProcessor> postProcessors = new HashSet<IPostProcessor>();
        postProcessors.add(new PostProcessor(TemplateMode.HTML, Dialect01PostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.XML, Dialect01PostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.TEXT, Dialect01PostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.JAVASCRIPT, Dialect01PostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.CSS, Dialect01PostProcessor.class, 1000));
        return postProcessors;
    }

}
