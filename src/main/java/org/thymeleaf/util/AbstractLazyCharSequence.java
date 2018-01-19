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
package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;


/**
 * <p>
 *   Abstract class for character sequences that perform lazy evaluation of their textual contents.
 * </p>
 * <p>
 *   Implementations of this class allow the possibility that their textual contents are actually computed
 *   during output writing (if possible), and therefore directly written to output buffers and never requiring
 *   being completely resolved in memory.
 * </p>
 * <p>
 *   This is mostly an <strong>internal class</strong>, and its use is not recommended from user's code.
 * </p>
 * <p>
 *   Children of this class are <strong>not</strong> thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public abstract class AbstractLazyCharSequence implements IWritableCharSequence {


    private String resolvedText = null;




    protected AbstractLazyCharSequence() {
        super();
    }



    protected abstract String resolveText();



    private String getText() {
        if (this.resolvedText == null) {
            this.resolvedText = resolveText();
        }
        return this.resolvedText;
    }




    public final int length() {
        return getText().length();
    }




    public final char charAt(final int index) {
        return getText().charAt(index);
    }




    public final CharSequence subSequence(final int beginIndex, final int endIndex) {
        return getText().subSequence(beginIndex, endIndex);
    }



    public final void write(final Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        if (this.resolvedText != null) {
            writer.write(this.resolvedText);
        } else {
            writeUnresolved(writer);
        }
    }


    protected abstract void writeUnresolved(final Writer writer) throws IOException;




    @Override
    public final boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractLazyCharSequence that = (AbstractLazyCharSequence) o;

        return this.getText().equals(that.getText());

    }


    public final int hashCode() {
        return getText().hashCode();
    }




    @Override
    public final String toString() {
        return getText();
    }



}
