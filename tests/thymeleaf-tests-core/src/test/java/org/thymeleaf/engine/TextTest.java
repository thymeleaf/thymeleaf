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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IText;


public final class TextTest {


    @Test
    public void test() {

        Text c1 = new Text("hello", "template", 10, 3);
        Assertions.assertEquals("hello", extractText(c1));
        final String c1all = c1.getText();
        Assertions.assertEquals("hello", c1all);
        Assertions.assertSame(c1all, c1.getText());
        Assertions.assertEquals("template", c1.getTemplateName());
        Assertions.assertEquals(10, c1.getLine());
        Assertions.assertEquals(3, c1.getCol());

        final String c1c0 = " something\nhere ";
        c1 = new Text(c1c0);
        Assertions.assertSame(c1c0, c1.getText());
        Assertions.assertNull(c1.getTemplateName());
        Assertions.assertEquals(-1, c1.getLine());
        Assertions.assertEquals(-1, c1.getCol());

    }


    @Test
    public void testSubsection() {

        Text c1 = new Text("something");

        Assertions.assertEquals("thing", c1.subSequence(4, 9));
        Assertions.assertEquals("some", c1.subSequence(0, 4));


        c1 = new Text("something", "test", 1, 1);

        Assertions.assertEquals("thing", c1.subSequence(4, 9));
        Assertions.assertEquals("some", c1.subSequence(0, 4));

    }


    @Test
    public void testContentFlags() {
        testFlags("", false, false);
        testFlags(" ", true, false);
        testFlags("   ", true, false);
        testFlags("\n", true, false);
        testFlags("\n  \t", true, false);
        testFlags("\n  [asd]", false, false);
        testFlags("\n  asdasdasd 23123 [ [asd ]]", false, false);
        testFlags("\n  asdasdasd 23123 [[asd ]]", false, true);
        testFlags("\n  asdasdasd 23123 [[asd ]]    [[asd]]", false, true);
        testFlags("\n  asdasdasd 23123  [ [asd ]]    [[asd] ]", false, false);
        testFlags("[[asd]]", false, true);
        testFlags("[[asd]", false, false);
        testFlags("[asd]]", false, false);
        testFlags("]]", false, false);
        testFlags("[[", false, false);
        testFlags("[[asd]]asd", false, true);
        testFlags("asd[[asd]]", false, true);
        testFlags("asd[[asd]]asd", false, true);
        testFlags("\n  (asd)", false, false);
        testFlags("\n  asdasdasd 23123 [ (asd )]", false, false);
        testFlags("\n  asdasdasd 23123 [(asd )]", false, true);
        testFlags("\n  asdasdasd 23123 [(asd )]    [(asd)]", false, true);
        testFlags("\n  asdasdasd 23123  [ (asd )]    [(asd) ]", false, false);
        testFlags("[(asd)]", false, true);
        testFlags("[(asd)", false, false);
        testFlags("[asd)]", false, false);
        testFlags(")]", false, false);
        testFlags("[(", false, false);
        testFlags("[(asd)]asd", false, true);
        testFlags("asd[(asd)]", false, true);
        testFlags("asd[(asd)]asd", false, true);
        testFlags("\n  (asd)", false, false);
        testFlags("\n  asdasdasd 23123 [ (asd ]]", false, false);
        testFlags("\n  asdasdasd 23123 [[asd )]", false, false);
        testFlags("\n  asdasdasd 23123 [(asd ]]    [[asd)]", false, false); // Intertwined inlines are not supported
        testFlags("\n  asdasdasd 23123  [ (asd ]]    [(asd) ]", false, false);
        testFlags("[(asd]]", false, false);
        testFlags("[(asd]", false, false);
        testFlags("(asd)]", false, false);
        testFlags("[(asd]]asd", false, false);
        testFlags("asd[[asd)]", false, false);
        testFlags("asd[(asd]])asd", false, false);
    }


    private static String extractText(final Text text) {

        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            strBuilder.append(text.charAt(i));
        }
        return strBuilder.toString();

    }


    private static void testFlags(final String text, final boolean whitespace, final boolean inlineable) {

        Text t1 = new Text(text);

        if (whitespace) {
            Assertions.assertTrue(t1.isWhitespace());
        } else {
            Assertions.assertFalse(t1.isWhitespace());
        }
        if (inlineable) {
            Assertions.assertTrue(t1.isInlineable());
        } else {
            Assertions.assertFalse(t1.isInlineable());
        }

        t1 = new Text(text, "test", 1, 1);

        if (whitespace) {
            Assertions.assertTrue(t1.isWhitespace());
        } else {
            Assertions.assertFalse(t1.isWhitespace());
        }
        if (inlineable) {
            Assertions.assertTrue(t1.isInlineable());
        } else {
            Assertions.assertFalse(t1.isInlineable());
        }

        t1 = new Text(text);
        // By using the wrappers we avoid the utils methods calling the engine implementations (which are already tested above)
        boolean bWhitespace1 = EngineEventUtils.isWhitespace(new TextWrapper(t1));
        boolean bInlineable1 = EngineEventUtils.isInlineable(new TextWrapper(t1));
        if (whitespace) {
            Assertions.assertTrue(bWhitespace1);
        } else {
            Assertions.assertFalse(bWhitespace1);
        }
        if (inlineable) {
            Assertions.assertTrue(bInlineable1);
        } else {
            Assertions.assertFalse(bInlineable1);
        }

    }


    private static final class TextWrapper implements IText {

        private final Text delegate;

        TextWrapper(final Text delegate) {
            super();
            this.delegate = delegate;
        }

        public static Text asEngineText(final IText text) {
            return Text.asEngineText(text);
        }

        public String getText() {
            return delegate.getText();
        }

        public int length() {
            return delegate.length();
        }

        public char charAt(final int index) {
            return delegate.charAt(index);
        }

        public CharSequence subSequence(final int start, final int end) {
            return delegate.subSequence(start, end);
        }

        public void accept(final IModelVisitor visitor) {
            delegate.accept(visitor);
        }

        public void write(final Writer writer) throws IOException {
            delegate.write(writer);
        }

        public String getTemplateName() {
            return delegate.getTemplateName();
        }

        public int getLine() {
            return delegate.getLine();
        }

        public int getCol() {
            return delegate.getCol();
        }

        public boolean hasLocation() {
            return delegate.hasLocation();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }


}