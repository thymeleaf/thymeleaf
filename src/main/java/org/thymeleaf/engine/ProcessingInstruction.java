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
package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ProcessingInstruction
            implements IProcessingInstruction, IEngineTemplateHandlerEvent {

    private final ITextRepository textRepository;

    private String processingInstruction;
    private String target;
    private String content;

    private int line;
    private int col;


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
               final int line, final int col) {

        this.target = target;
        this.content = content;

        this.processingInstruction = processingInstruction;

        this.line = line;
        this.col = col;

    }



    private void initializeFromProcessingInstruction(
            final String target,
            final String content) {

        if (target == null || target.trim().length() == 0) {
            throw new IllegalArgumentException("Processing Instruction target cannot be null or empty");
        }

        this.target = target;
        this.content = content;

        this.processingInstruction = null;

        this.line = -1;
        this.col = -1;

    }






    public boolean hasLocation() {
        return (this.line != -1 && this.col != -1);
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }





    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write(getProcessingInstruction());
    }



    public String toString() {
        return getProcessingInstruction();
    }






    public ProcessingInstruction cloneNode() {
        final ProcessingInstruction clone = new ProcessingInstruction(this.textRepository);
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final ProcessingInstruction original) {

        this.processingInstruction = original.processingInstruction;
        this.target = original.target;
        this.content = original.content;
        this.line = original.line;
        this.col = original.col;

    }


    // Meant to be called only from within the engine
    static ProcessingInstruction asEngineProcessingInstruction(
            final IEngineConfiguration configuration, final IProcessingInstruction processingInstruction, final boolean cloneAlways) {

        if (processingInstruction instanceof ProcessingInstruction) {
            if (cloneAlways) {
                return ((ProcessingInstruction) processingInstruction).cloneNode();
            }
            return (ProcessingInstruction) processingInstruction;
        }

        final ProcessingInstruction newInstance = new ProcessingInstruction(configuration.getTextRepository());
        newInstance.processingInstruction = processingInstruction.getProcessingInstruction();
        newInstance.target = processingInstruction.getTarget();
        newInstance.content = processingInstruction.getContent();
        newInstance.line = processingInstruction.getLine();
        newInstance.col = processingInstruction.getCol();
        return newInstance;

    }


}
