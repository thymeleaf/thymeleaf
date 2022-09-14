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
package org.thymeleaf.templateengine.templateboundaries.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.postprocessor.PostProcessor;
import org.thymeleaf.templatemode.TemplateMode;


public class TemplateBoundariesDialect extends AbstractDialect implements IPostProcessorDialect {


    public TemplateBoundariesDialect() {
        super("TemplateBoundaries");
    }



    public int getDialectPostProcessorPrecedence() {
        return 1000;
    }

    public Set<IPostProcessor> getPostProcessors() {
        final Set<IPostProcessor> postProcessors = new HashSet<IPostProcessor>();
        postProcessors.add(new PostProcessor(TemplateMode.HTML, BoundaryPostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.XML, BoundaryPostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.TEXT, BoundaryPostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.CSS, BoundaryPostProcessor.class, 1000));
        postProcessors.add(new PostProcessor(TemplateMode.JAVASCRIPT, BoundaryPostProcessor.class, 1000));
        return postProcessors;
    }


    public static final class BoundaryPostProcessor extends AbstractTemplateHandler {


        private boolean start = false;
        private boolean end = false;

        @Override
        public void handleTemplateStart(final ITemplateStart templateStart) {

            if (this.start) {
                throw new RuntimeException("Template has more than one 'template start' event after processing!");
            }
            this.start = true;

            super.handleTemplateStart(templateStart);

        }

        @Override
        public void handleTemplateEnd(final ITemplateEnd templateEnd) {

            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("Template has more than one 'template end' event after processing!");
            }
            this.end = true;

            super.handleTemplateEnd(templateEnd);

        }

        @Override
        public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleXMLDeclaration(xmlDeclaration);
        }

        @Override
        public void handleDocType(final IDocType docType) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleDocType(docType);
        }

        @Override
        public void handleCDATASection(final ICDATASection cdataSection) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleCDATASection(cdataSection);
        }

        @Override
        public void handleComment(final IComment comment) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleComment(comment);
        }

        @Override
        public void handleText(final IText text) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleText(text);
        }

        @Override
        public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleStandaloneElement(standaloneElementTag);
        }

        @Override
        public void handleOpenElement(final IOpenElementTag openElementTag) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleOpenElement(openElementTag);
        }

        @Override
        public void handleCloseElement(final ICloseElementTag closeElementTag) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleCloseElement(closeElementTag);
        }

        @Override
        public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
            if (!this.start) {
                throw new RuntimeException("No 'template start' event has been issued for this template");
            }
            if (this.end) {
                throw new RuntimeException("A 'template end' event has already been issued for this template");
            }
            super.handleProcessingInstruction(processingInstruction);
        }

    }


    
}
