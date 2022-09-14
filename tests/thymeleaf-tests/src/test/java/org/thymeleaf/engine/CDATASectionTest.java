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
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IModelVisitor;


public final class CDATASectionTest {



    @Test
    public void test() {

        CDATASection c1 =
                new CDATASection("hello","testtemplate", 10, 3);
        Assertions.assertEquals("<![CDATA[hello]]>", extractText(c1));
        final String c1all = c1.getCDATASection();
        final String c1content = c1.getContent();
        Assertions.assertEquals("<![CDATA[hello]]>", c1all);
        Assertions.assertEquals("hello", c1content);
        Assertions.assertSame(c1all, c1.getCDATASection());
        Assertions.assertSame(c1content, c1.getContent());
        Assertions.assertEquals("testtemplate", c1.getTemplateName());
        Assertions.assertEquals(10, c1.getLine());
        Assertions.assertEquals(3, c1.getCol());

        final String c1c0 = " something\nhere ";
        c1 = new CDATASection(c1c0);
        Assertions.assertSame(c1c0, c1.getContent());
        Assertions.assertEquals("<![CDATA[ something\nhere ]]>", c1.getCDATASection());
        Assertions.assertNull(c1.getTemplateName());
        Assertions.assertEquals(-1, c1.getLine());
        Assertions.assertEquals(-1, c1.getCol());

        c1 = new CDATASection("<![cdata[",c1c0,CDATASection.CDATA_SUFFIX, "testtemplate", 11, 4);
        Assertions.assertEquals("<![cdata[ something\nhere ]]>", c1.getCDATASection());
        Assertions.assertEquals(c1c0, c1.getContent());
        Assertions.assertEquals("testtemplate", c1.getTemplateName());
        Assertions.assertEquals(11, c1.getLine());
        Assertions.assertEquals(4, c1.getCol());

    }





    @Test
    public void testSubsection() {

        CDATASection c1 = new CDATASection("something");

        Assertions.assertEquals("CDATA", c1.subSequence(3, 8));
        Assertions.assertEquals("some", c1.subSequence(9, 13));


        c1 = new CDATASection("something", "test", 1, 1);

        Assertions.assertEquals("CDATA", c1.subSequence(3, 8));
        Assertions.assertEquals("some", c1.subSequence(9, 13));

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




    private static String extractText(final CDATASection cdataSection) {

        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < cdataSection.length(); i++) {
            strBuilder.append(cdataSection.charAt(i));
        }
        return strBuilder.toString();

    }



    private static void testFlags(final String text, final boolean whitespace, final boolean inlineable) {

        CDATASection t1 = new CDATASection(text);

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

        t1 = new CDATASection("<![cdata[", text, "  ]]>", "test", 1, 1);

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

        t1 = new CDATASection(text);
        // By using the wrappers we avoid the utils methods calling the engine implementations (which are already tested above)
        boolean bWhitespace1 = EngineEventUtils.isWhitespace(new CDATASectionWrapper(t1));
        boolean bInlineable1 = EngineEventUtils.isInlineable(new CDATASectionWrapper(t1));
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



    private static final class CDATASectionWrapper implements ICDATASection {

        private final CDATASection delegate;

        CDATASectionWrapper(final CDATASection delegate) {
            super();
            this.delegate = delegate;
        }

        public static CDATASection asEngineCDATASection(final ICDATASection cdataSection) {
            return CDATASection.asEngineCDATASection(cdataSection);
        }

        public String getCDATASection() {
            return delegate.getCDATASection();
        }

        public String getContent() {
            return delegate.getContent();
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

        public boolean hasLocation() {
            return delegate.hasLocation();
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

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

}
