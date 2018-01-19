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

import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IModelVisitor;

/*
 * Engine implementation of IComment.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class Comment extends AbstractTextualTemplateEvent implements IComment {

    // Comment nodes do not exist in text parsing, so we are safe expliciting markup structures here
    private static final String COMMENT_PREFIX = "<!--";
    private static final String COMMENT_SUFFIX = "-->";


    final String prefix;
    final String suffix;

    private volatile String computedCommentStr = null;



    Comment(final CharSequence content) {
        this(COMMENT_PREFIX, content, COMMENT_SUFFIX);
    }


    Comment(final String prefix, final CharSequence content, final String suffix) {
        super(content);
        this.prefix = prefix;
        this.suffix = suffix;
    }


    Comment(final CharSequence content, final String templateName, final int line, final int col) {
        this(COMMENT_PREFIX, content, COMMENT_SUFFIX, templateName, line, col);
    }


    Comment(final String prefix, final CharSequence content, final String suffix, final String templateName, final int line, final int col) {
        super(content, templateName, line, col);
        this.prefix = prefix;
        this.suffix = suffix;
    }






    public String getComment() {
        String c = this.computedCommentStr;
        if (c == null) {
            this.computedCommentStr = c = this.prefix + getContentText() + this.suffix;
        }
        return c;
    }


    public String getContent() {
        return getContentText();
    }




    public int length() {
        return this.prefix.length() + getContentLength() + this.suffix.length();
    }


    public char charAt(final int index) {
        if (index < this.prefix.length()) {
            return this.prefix.charAt(index);
        }
        final int prefixedContentLen = this.prefix.length() + getContentLength();
        if (index >= prefixedContentLen) {
            return this.suffix.charAt(index - prefixedContentLen);
        }
        return charAtContent(index - this.prefix.length());
    }


    public CharSequence subSequence(final int start, final int end) {
        // First we will try to avoid computing the complete String
        if (start >= this.prefix.length() && end < (this.prefix.length() + getContentLength())) {
            return contentSubSequence((start - this.prefix.length()), (end - this.prefix.length()));
        }
        return getComment().subSequence(start, end);
    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        writer.write(this.prefix);
        writeContent(writer);
        writer.write(this.suffix);
    }




    // Meant to be called only from within the engine
    static Comment asEngineComment(final IComment comment) {
        if (comment instanceof Comment) {
            return (Comment) comment;
        }
        return new Comment(comment.getContent(), comment.getTemplateName(), comment.getLine(), comment.getCol());
    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleComment(this);
    }




    @Override
    public String toString() {
        return getComment();
    }


}
