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
package org.thymeleaf.text;

/**
 * <p>
 *     Common interface for repositories of text (<tt>String</tt>) instances created by the parser or
 *     document model.
 * </p>
 * <p>
 *     Implementations of this interface must be <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface ITextRepository {

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument.
     * </p>
     *
     * @param text the text to be retrieved.
     * @return the stored text.
     */
    public String getText(final CharSequence text);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument, which is a subsequence of the
     *     {@link CharSequence} object starting in index <tt>beginIndex</tt> (inclusive) until index <tt>endIndex</tt>
     *     (exclusive).
     * </p>
     *
     * @param text the text to be retrieved.
     * @param beginIndex the starting index (inclusive).
     * @param endIndex the end index (exclusive).
     * @return the stored text.
     */
    public String getText(final CharSequence text, int beginIndex, int endIndex);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument (composed of more than one fragment).
     * </p>
     * <p>
     *     The aim of this method is to avoid the need to create instances of appended {@link String} or
     *     {@link CharSequence} objects, using the repository to find the unique version of the appended result.
     * </p>
     *
     * @param text0 the first fragment of the text to be retrieved.
     * @param text1 the second fragment of the text to be retrieved.
     * @return the stored text.
     */
    public String getText(final CharSequence text0, final CharSequence text1);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument (composed of more than one fragment).
     * </p>
     * <p>
     *     The aim of this method is to avoid the need to create instances of appended {@link String} or
     *     {@link CharSequence} objects, using the repository to find the unique version of the appended result.
     * </p>
     *
     * @param text0 the first fragment of the text to be retrieved.
     * @param text1 the second fragment of the text to be retrieved.
     * @param text2 the third fragment of the text to be retrieved.
     * @return the stored text.
     */
    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument (composed of more than one fragment).
     * </p>
     * <p>
     *     The aim of this method is to avoid the need to create instances of appended {@link String} or
     *     {@link CharSequence} objects, using the repository to find the unique version of the appended result.
     * </p>
     *
     * @param text0 the first fragment of the text to be retrieved.
     * @param text1 the second fragment of the text to be retrieved.
     * @param text2 the third fragment of the text to be retrieved.
     * @param text3 the fourth fragment of the text to be retrieved.
     * @return the stored text.
     */
    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2, final CharSequence text3);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument (composed of more than one fragment).
     * </p>
     * <p>
     *     The aim of this method is to avoid the need to create instances of appended {@link String} or
     *     {@link CharSequence} objects, using the repository to find the unique version of the appended result.
     * </p>
     *
     * @param text0 the first fragment of the text to be retrieved.
     * @param text1 the second fragment of the text to be retrieved.
     * @param text2 the third fragment of the text to be retrieved.
     * @param text3 the fourth fragment of the text to be retrieved.
     * @param text4 the fifth fragment of the text to be retrieved.
     * @return the stored text.
     */
    public String getText(final CharSequence text0, final CharSequence text1, final CharSequence text2, final CharSequence text3, final CharSequence text4);

    /**
     * <p>
     *     Retrieve the stored version of the text passed as argument.
     * </p>
     *
     * @param text the buffer containing the text to be retrieved.
     * @param offset tbe offset to be aplied to the text buffer.
     * @param len the length of the text in the specified buffer.
     * @return the stored text.
     */
    public String getText(final char[] text, final int offset, final int len);

}
