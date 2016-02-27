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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class ProcessingInstruction extends AbstractTemplateEvent implements IProcessingInstruction, IEngineTemplateEvent {

    private final ITextRepository textRepository;

    private String processingInstruction;
    private String target;
    private String content;


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     *
     * The 'processingInstruction' property, which is computed from the other properties, should be computed lazily
     * in order to avoid unnecessary creation of Strings which would use more memory than needed.
     */


    // Meant to be called only from the template handler adapter
    ProcessingInstruction(final ITextRepository textRepository) {
        super();
        this.textRepository = textRepository;
    }



    // Meant to be called only from the model factory
    ProcessingInstruction(
            final ITextRepository textRepository,
            final String target,
            final String content) {
        super();
        this.textRepository = textRepository;
        initializeFromProcessingInstruction(target, content);
    }




    public String getTarget() {
        return this.target;
    }

    public String getContent() {
        return this.content;
    }




    public String getProcessingInstruction() {

        if (this.processingInstruction == null) {

            final StringBuilder strBuilder = new StringBuilder();

            strBuilder.append("<?");
            strBuilder.append(this.target);
            if (this.content != null) {
                strBuilder.append(' ');
                strBuilder.append(this.content);
            }
            strBuilder.append("?>");

            this.processingInstruction = this.textRepository.getText(strBuilder);

        }

        return this.processingInstruction;

    }




    public void setTarget(final String target) {
        initializeFromProcessingInstruction(target, this.content);
    }

    public void setContent(final String content) {
        initializeFromProcessingInstruction(this.target, content);
    }




    // Meant to be called only from within the engine - removes the need to validate compute the 'processingInstruction' field
    void reset(final String processingInstruction,
               final String target,
               final String content,
               final String templateName, final int line, final int col) {

        super.resetTemplateEvent(templateName, line, col);

        this.target = target;
        this.content = content;

        this.processingInstruction = processingInstruction;

    }



    private void initializeFromProcessingInstruction(
            final String target,
            final String content) {

        if (target == null || target.trim().length() == 0) {
            throw new IllegalArgumentException("Processing Instruction target cannot be null or empty");
        }

        super.resetTemplateEvent(null, -1, -1);

        this.target = target;
        this.content = content;

        this.processingInstruction = null;

    }





    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write(getProcessingInstruction());
    }






    public ProcessingInstruction cloneEvent() {
        final ProcessingInstruction clone = new ProcessingInstruction(this.textRepository);
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final ProcessingInstruction original) {

        super.resetAsCloneOfTemplateEvent(original);
        this.processingInstruction = original.processingInstruction;
        this.target = original.target;
        this.content = original.content;

    }


    // Meant to be called only from within the engine
    static ProcessingInstruction asEngineProcessingInstruction(
            final IEngineConfiguration configuration, final IProcessingInstruction processingInstruction, final boolean cloneAlways) {

        if (processingInstruction instanceof ProcessingInstruction) {
            if (cloneAlways) {
                return ((ProcessingInstruction) processingInstruction).cloneEvent();
            }
            return (ProcessingInstruction) processingInstruction;
        }

        final ProcessingInstruction newInstance = new ProcessingInstruction(configuration.getTextRepository());
        newInstance.processingInstruction = processingInstruction.getProcessingInstruction();
        newInstance.target = processingInstruction.getTarget();
        newInstance.content = processingInstruction.getContent();
        newInstance.resetTemplateEvent(processingInstruction.getTemplateName(), processingInstruction.getLine(), processingInstruction.getCol());
        return newInstance;

    }





    @Override
    public final String toString() {
        return getProcessingInstruction();
    }


}
