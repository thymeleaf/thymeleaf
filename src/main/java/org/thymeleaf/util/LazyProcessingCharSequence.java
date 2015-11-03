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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;


/**
 * <p>
 *   Character sequence that performs a lazy evaluation of a {@link TemplateModel} by the template engine.
 * </p>
 * <p>
 *   It is used sometimes internally by the engine in order to avoid the creation of extra String objects in
 *   some scenarios (e.g. inliners).
 * </p>
 * <p>
 *   This is mostly an <strong>internal class</strong>, and its use is not recommended from user's code.
 * </p>
 * <p>
 *   This class is <strong>not</strong> thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class LazyProcessingCharSequence implements CharSequence {


    private final ITemplateContext context;
    private final TemplateModel templateModel;

    private String resolvedText = null;




    public LazyProcessingCharSequence(final ITemplateContext context, final TemplateModel templateModel) {

        super();

        if (context == null) {
            throw new IllegalArgumentException("Template Context is null, which is forbidden");
        }
        if (templateModel == null) {
            throw new IllegalArgumentException("Template Model is null, which is forbidden");
        }

        this.context = context;
        this.templateModel = templateModel;

    }




    private void resolveText() {

        if (this.resolvedText != null) {
            return;
        }

        final StringWriter stringWriter = new StringWriter();
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, stringWriter);
        this.resolvedText = stringWriter.toString();

    }




    public String getText() {
        if (this.resolvedText == null) {
            resolveText();
        }
        return this.resolvedText;
    }




    public int length() {
        if (this.resolvedText == null) {
            resolveText();
        }
        return this.resolvedText.length();
    }




    public char charAt(final int index) {
        if (this.resolvedText == null) {
            resolveText();
        }
        return this.resolvedText.charAt(index);
    }




    public CharSequence subSequence(final int beginIndex, final int endIndex) {
        if (this.resolvedText == null) {
            resolveText();
        }
        return this.resolvedText.subSequence(beginIndex, endIndex);
    }


    /**
     * <p>
     *     This method can avoid the need to create a {@link String} object containing all the contents in
     *     this character sequence just when we want to write it to a {@link Writer}.
     * </p>
     *
     * @param writer the writer to write the character sequence to.
     * @throws IOException if an input/output exception happens during writing
     */
    public void write(final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, writer);
    }




    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof LazyProcessingCharSequence)) {
            return false;
        }

        final LazyProcessingCharSequence that = (LazyProcessingCharSequence) o;

        return this.getText().equals(that.getText());

    }




    public int hashCode() {
        if (this.resolvedText == null) {
            resolveText();
        }
        return this.resolvedText.hashCode();
    }




    @Override
    public String toString() {
        return getText();
    }



}
