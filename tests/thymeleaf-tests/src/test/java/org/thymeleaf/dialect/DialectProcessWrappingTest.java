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
package org.thymeleaf.dialect;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.dialectwrapping.Dialect01;
import org.thymeleaf.processor.cdatasection.ICDATASectionProcessor;
import org.thymeleaf.processor.comment.ICommentProcessor;
import org.thymeleaf.processor.doctype.IDocTypeProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor;
import org.thymeleaf.processor.text.ITextProcessor;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor;
import org.thymeleaf.templatemode.TemplateMode;


public class DialectProcessWrappingTest {


    public DialectProcessWrappingTest() {
        super();
    }
    
    
    
    
    @Test
    public void testDialectWrapping() throws Exception {

        final Dialect01 dialect01 = new Dialect01();
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setDialect(dialect01);

        final IEngineConfiguration config = templateEngine.getConfiguration();


        final List<IElementProcessor> elementProcessors = new ArrayList<IElementProcessor>(config.getElementProcessors(TemplateMode.HTML));
        final List<ICDATASectionProcessor> cdataSectionProcessors = new ArrayList<ICDATASectionProcessor>(config.getCDATASectionProcessors(TemplateMode.HTML));
        final List<ICommentProcessor> commentProcessors = new ArrayList<ICommentProcessor>(config.getCommentProcessors(TemplateMode.HTML));
        final List<IDocTypeProcessor> docTypeProcessors = new ArrayList<IDocTypeProcessor>(config.getDocTypeProcessors(TemplateMode.HTML));
        final List<IProcessingInstructionProcessor> processingInstructionProcessors = new ArrayList<IProcessingInstructionProcessor>(config.getProcessingInstructionProcessors(TemplateMode.HTML));
        final List<ITemplateBoundariesProcessor> templateBoundariesProcessors = new ArrayList<ITemplateBoundariesProcessor>(config.getTemplateBoundariesProcessors(TemplateMode.HTML));
        final List<ITextProcessor> textProcessors = new ArrayList<ITextProcessor>(config.getTextProcessors(TemplateMode.HTML));
        final List<IXMLDeclarationProcessor> xmlDeclarationProcessors = new ArrayList<IXMLDeclarationProcessor>(config.getXMLDeclarationProcessors(TemplateMode.HTML));


        Assertions.assertEquals(2, elementProcessors.size());
        Assertions.assertEquals(1, cdataSectionProcessors.size());
        Assertions.assertEquals(1, commentProcessors.size());
        Assertions.assertEquals(1, docTypeProcessors.size());
        Assertions.assertEquals(1, processingInstructionProcessors.size());
        Assertions.assertEquals(1, templateBoundariesProcessors.size());
        Assertions.assertEquals(1, textProcessors.size());
        Assertions.assertEquals(1, xmlDeclarationProcessors.size());

        // We will use the class names because the classes are package-protected
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$ElementModelProcessorWrapper", elementProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$ElementTagProcessorWrapper", elementProcessors.get(1).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$CDATASectionProcessorWrapper", cdataSectionProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$CommentProcessorWrapper", commentProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$DocTypeProcessorWrapper", docTypeProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$ProcessingInstructionProcessorWrapper", processingInstructionProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$TemplateBoundariesProcessorWrapper", templateBoundariesProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$TextProcessorWrapper", textProcessors.get(0).getClass().getName());
        Assertions.assertEquals("org.thymeleaf.util.ProcessorConfigurationUtils$XMLDeclarationProcessorWrapper", xmlDeclarationProcessors.get(0).getClass().getName());

        Assertions.assertEquals(100, elementProcessors.get(0).getPrecedence());
        Assertions.assertEquals(110, elementProcessors.get(1).getPrecedence());
        Assertions.assertEquals(100, cdataSectionProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, commentProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, docTypeProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, processingInstructionProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, templateBoundariesProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, textProcessors.get(0).getPrecedence());
        Assertions.assertEquals(100, xmlDeclarationProcessors.get(0).getPrecedence());

    }


}
