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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IProcessingInstruction;

/*
 * Engine implementation of IProcessingInstruction.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class ProcessingInstruction extends AbstractTemplateEvent implements IProcessingInstruction, IEngineTemplateEvent {

    private final String target;
    private final String content;

    private final String processingInstruction;




    ProcessingInstruction(
            final String target,
            final String content) {
        super();
        this.target = target;
        this.content = content;
        this.processingInstruction = computeProcessingInstruction();
    }


    ProcessingInstruction(
            final String processingInstruction,
            final String target,
            final String content,
            final String templateName, final int line, final int col) {
        super(templateName, line, col);
        this.target = target;
        this.content = content;
        this.processingInstruction = (processingInstruction != null? processingInstruction : computeProcessingInstruction());
    }




    public String getTarget() {
        return this.target;
    }

    public String getContent() {
        return this.content;
    }

    public String getProcessingInstruction() {
        return this.processingInstruction;
    }



    private String computeProcessingInstruction() {

        final StringBuilder strBuilder = new StringBuilder(100);

        strBuilder.append("<?");
        strBuilder.append(this.target);
        if (this.content != null) {
            strBuilder.append(' ');
            strBuilder.append(this.content);
        }
        strBuilder.append("?>");

        return strBuilder.toString();

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        writer.write(this.processingInstruction);
    }




    static ProcessingInstruction asEngineProcessingInstruction(final IProcessingInstruction processingInstruction) {

        if (processingInstruction instanceof ProcessingInstruction) {
            return (ProcessingInstruction) processingInstruction;
        }

        return new ProcessingInstruction(
                null,
                processingInstruction.getTarget(), processingInstruction.getContent(),
                processingInstruction.getTemplateName(), processingInstruction.getLine(), processingInstruction.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleProcessingInstruction(this);
    }




    @Override
    public String toString() {
        return getProcessingInstruction();
    }


}
