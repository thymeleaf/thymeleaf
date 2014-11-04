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
package org.thymeleaf.aurora.engine;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ProcessingInstruction {

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


    // Meant to be called only from within the engine
    ProcessingInstruction() {

        super();

        this.target = null;
        this.content = null;

        this.processingInstruction = null;

    }



    public ProcessingInstruction(
            final String target,
            final String content) {
        super();
        initializeFromProcessingInstruction(target, content);
    }




    public String getTarget() {
        return this.target;
    }

    public String getContent() {
        return this.content;
    }




    public String getXmlDeclaration() {

        if (this.target == null) {
            // Should never happen, but just in case
            return null;
        }

        if (this.processingInstruction == null) {

            final StringBuilder strBuilder = new StringBuilder(70);
            strBuilder.append("<?");
            strBuilder.append(this.target);
            if (this.content != null) {
                strBuilder.append(' ');
                strBuilder.append(this.content);
            }
            strBuilder.append("?>");

            this.processingInstruction = strBuilder.toString();

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
    void setProcessingInstruction(
            final String processingInstruction,
            final String target,
            final String content) {

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

        this.target = target;
        this.content = content;

        this.processingInstruction = null;

    }


}
