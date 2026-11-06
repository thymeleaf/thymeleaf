/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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


import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsSanitizeHtmlFormattingTest {

    private static final String[] ALLOWED = {
            "b", "strong", "i", "em", "u", "s", "del", "ins", "mark", "small", "sub", "sup", "span" };
    private static final String[] DROPPED_CONTENT = {
            "script", "style", "textarea", "title", "template", "iframe", "object", "noscript", "nostyle", "noembed",
            "noframes", "frame", "frameset", "svg", "math", "xmp", "listing", "plaintext" };
    private static final String[] OTHER = { "a", "p", "div", "font", "h1", "table", "td", "form", "label", "x" };

    private static final Pattern OUTPUT_TOKEN = Pattern.compile(
            "<br />|<(/?)(b|strong|i|em|u|s|del|ins|mark|small|sub|sup|span)( class=\"[A-Za-z0-9_-]+(?: [A-Za-z0-9_-]+)*\")?>" +
            "|&(?:lt|gt|amp|quot|#39);|[^<>&\"']");


    private static String sanitize(final Object target) {
        return StringUtils.sanitizeHtmlFormatting(target);
    }

    // Returns null if the output only contains the elements, attributes, references and characters allowed
    private static String checkOutput(final String output) {
        final Matcher matcher = OUTPUT_TOKEN.matcher(output);
        final Deque<String> openElements = new ArrayDeque<String>();
        int pos = 0;
        while (pos < output.length()) {
            if (!matcher.find(pos) || matcher.start() != pos) {
                return "unexpected output at position " + pos;
            }
            if (matcher.group(2) != null) {
                if (matcher.group(1).isEmpty()) {
                    openElements.push(matcher.group(2));
                    if (openElements.size() > 10) {
                        return "more than 10 nested elements";
                    }
                    if (matcher.group(3) != null && !"span".equals(matcher.group(2))) {
                        return "class attribute in " + matcher.group(2);
                    }
                } else if (matcher.group(3) != null || openElements.isEmpty() || !openElements.pop().equals(matcher.group(2))) {
                    return "unbalanced " + matcher.group(2);
                }
            }
            pos = matcher.end();
        }
        if (!openElements.isEmpty()) {
            return "unclosed " + openElements;
        }
        int i = 0;
        while (i < output.length()) {
            final int codePoint = output.codePointAt(i);
            if ((codePoint < 0x20 && codePoint != '\t' && codePoint != '\n' && codePoint != '\r')
                    || (codePoint >= 0xD800 && codePoint <= 0xDFFF) || (codePoint >= 0xFDD0 && codePoint <= 0xFDEF)
                    || (codePoint & 0xFFFE) == 0xFFFE) {
                return "invalid char " + Integer.toHexString(codePoint);
            }
            i += Character.charCount(codePoint);
        }
        if (!output.equals(sanitize(output))) {
            return "not stable when sanitized again";
        }
        return null;
    }

    private static void assertValidOutput(final String input) {
        final String output = sanitize(input);
        final String problem = checkOutput(output);
        Assertions.assertNull(problem, problem + " for input [" + input + "] -> [" + output + "]");
    }


    @Test
    public void testControlCharsAfterElementName() {
        for (char c = 0; c <= 0xA0; c++) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                continue;
            }
            final String input = "<b" + c + ">x</b>";
            final String output = sanitize(input);
            Assertions.assertNull(checkOutput(output));
            // Unparseable markup is output as text
            Assertions.assertTrue(
                    "<b>x</b>".equals(output) || "x".equals(output) || "<b></b>x".equals(output)
                            || "<b>&gt;x</b>".equals(output) || escape(input).equals(output),
                    Integer.toHexString(c) + ": " + output);
            final String dropped = sanitize("<script" + c + ">X</script>Y");
            Assertions.assertNull(checkOutput(dropped));
            Assertions.assertFalse(dropped.contains("<"), Integer.toHexString(c) + ": " + dropped);
        }
    }

    @Test
    public void testControlCharsInCloseTags() {
        for (char c = 0; c <= 0xA0; c++) {
            final String output = sanitize("<b>x</b" + c + ">y");
            Assertions.assertNull(checkOutput(output));
            Assertions.assertTrue(output.contains("x") && output.contains("y"), Integer.toHexString(c) + ": " + output);
            assertValidOutput("<script>X</script" + c + ">Y");
            assertValidOutput("<svg><b>X</b></svg" + c + ">Y");
        }
    }

    @Test
    public void testControlCharsBeforeElementName() {
        for (char c = 0; c <= 0xA0; c++) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '!' || c == '?' || c == '/' || c == '<') {
                continue;
            }
            final String output = sanitize("<" + c + "b>x</b>");
            Assertions.assertNull(checkOutput(output));
            Assertions.assertFalse(output.contains("<b>"), Integer.toHexString(c) + ": " + output);
            Assertions.assertTrue(output.startsWith("&lt;"), Integer.toHexString(c) + ": " + output);
        }
        Assertions.assertEquals("&lt;<b>x</b>", sanitize("<<b>x</b>"));
        for (char c = 0x80; c < 0x3000; c++) {
            Assertions.assertTrue(sanitize("<" + c + "b>x").startsWith("&lt;"), Integer.toHexString(c));
        }
    }

    @Test
    public void testControlCharsInAttributes() {
        for (char c = 0; c <= 0xA0; c++) {
            assertValidOutput("<span class=\"a" + c + "b\">x</span>");
            assertValidOutput("<span" + c + "class=\"a\">x</span>");
            assertValidOutput("<span class" + c + "=\"a\">x</span>");
            assertValidOutput("<span class=" + c + "\"a\">x</span>");
            assertValidOutput("<b onclick=\"a" + c + "\" class=\"" + c + "\">x</b>");
        }
        Assertions.assertEquals("<span>x</span>", sanitize("<span class=\"a\u000Bb\">x</span>"));
        Assertions.assertEquals("<span>x</span>", sanitize("<span class=\"a\u0000b\">x</span>"));
        Assertions.assertEquals("<span>x</span>", sanitize("<span class=\"a\u00A0b\">x</span>"));
        Assertions.assertEquals("<span class=\"a b\">x</span>", sanitize("<span class=\"a&#10;b\">x</span>"));
        Assertions.assertEquals("<span class=\"a b\">x</span>", sanitize("<span class=\"a\tb\">x</span>"));
        Assertions.assertEquals("<span class=\"ab\">x</span>", sanitize("<span class=\"&#97;b\">x</span>"));
        Assertions.assertEquals("<span>x</span>", sanitize("<span class=\"a&quot; onclick=&quot;b\">x</span>"));
    }

    @Test
    public void testControlCharsAsReferences() {
        Assertions.assertEquals("ok", sanitize("&#1;&#x1F;&#xFFFE;&#xFFFF;&#xFDD0;&#x1FFFE;ok"));
        Assertions.assertEquals("\t\n\r", sanitize("&#9;&#10;&#13;"));
        Assertions.assertNull(checkOutput(sanitize("&#0;&#xD800;&#xDFFF;&#x110000;&#xFFFFFFFF;")));
        Assertions.assertEquals("&lt;b&gt;&lt;/b&gt;", sanitize("&#60;b&#62;&#x3C;/b&#x3E;"));
        Assertions.assertEquals("&lt;script&gt;", sanitize("&lt;script&gt;"));
        Assertions.assertEquals("&amp;lt;b&amp;gt;", sanitize("&amp;lt;b&amp;gt;"));
        for (char c = 0; c <= 0xA0; c++) {
            assertValidOutput("&#" + (int) c + ";&#x" + Integer.toHexString(c) + ";" + c + "&" + c + "lt;");
        }
    }

    @Test
    public void testLessThanNotStartingMarkup() {
        Assertions.assertEquals("Age must be &lt;18 or <b>more</b>", sanitize("Age must be <18 or <b>more</b>"));
        Assertions.assertEquals("1&lt;2&lt;3", sanitize("1<2<3"));
        Assertions.assertEquals("x&lt;-y &lt;= z &lt;&gt;", sanitize("x<-y <= z <>"));
        Assertions.assertEquals("&lt; b&gt;x", sanitize("< b>x"));
        Assertions.assertEquals("a&lt;", sanitize("a<"));
        Assertions.assertEquals("a&lt;/", sanitize("a</"));
        Assertions.assertEquals("&lt;/3", sanitize("</3"));
        Assertions.assertEquals("&lt;\u00E9&gt;", sanitize("<\u00E9>"));
        Assertions.assertEquals("\uFF1Cb\uFF1Ex", sanitize("\uFF1Cb\uFF1Ex"));
    }

    @Test
    public void testLookalikeElementNames() {
        // As in the HTML tokenizer, '<' followed by a non-ASCII letter is text
        Assertions.assertEquals("&lt;\u017Fcript&gt;x&lt;/\u017Fcript&gt;", sanitize("<\u017Fcript>x</\u017Fcript>"));
        Assertions.assertEquals("&lt;\u212Abd&gt;x&lt;/\u212Abd&gt;", sanitize("<\u212Abd>x</\u212Abd>"));
        Assertions.assertEquals("x", sanitize("<scr\u0130pt>x"));
        Assertions.assertEquals("x", sanitize("<sc\u0000ript>x"));
        // The parser ends names at some non-ASCII whitespace too: only the canonical element can be output
        for (final char c : new char[] { '\u00A0', '\u0085', '\u1680', '\u2000', '\u2028', '\u2029', '\u3000', '\uFEFF' }) {
            final String output = sanitize("<b" + c + ">x</b>");
            Assertions.assertTrue("x".equals(output) || "<b>x</b>".equals(output), Integer.toHexString(c) + ": " + output);
            Assertions.assertFalse(sanitize("<script" + c + ">X</script>Y").contains("<"), Integer.toHexString(c));
        }
        Assertions.assertEquals("x", sanitize("<svg:b>x</svg:b>"));
    }

    @Test
    public void testDroppedContentElements() {
        for (final String name : DROPPED_CONTENT) {
            Assertions.assertEquals("ab", sanitize("a<" + name + "><b>X</b>X</" + name + ">b"), name);
            Assertions.assertEquals("ab", sanitize("a<" + name.toUpperCase() + " class=\"x\">X</" + name.toUpperCase() + ">b"), name);
            Assertions.assertEquals("a<b>b</b>", sanitize("a<" + name + "/><b>b</b>"), name);
        }
        for (final String name : OTHER) {
            Assertions.assertEquals("aXb", sanitize("a<" + name + ">X</" + name + ">b"), name);
        }
        // Raw text elements only end at their exact closing tag: anything else drops the rest of the content
        Assertions.assertFalse(sanitize("<script>X</SCRIPT>Y").contains("X"));
        Assertions.assertFalse(sanitize("<script>X</script >Y").contains("X"));
        Assertions.assertFalse(sanitize("<style>X</style\t>Y").contains("X"));
    }

    @Test
    public void testUnparseableMarkup() {
        Assertions.assertEquals("&lt;script&quot;&gt;X&lt;/script&gt;", sanitize("<script\">X</script>"));
        Assertions.assertEquals("&lt;b title=&#39;x&gt;y&lt;/b&gt;", sanitize("<b title='x>y</b>"));
    }

    @Test
    public void testSanitizeHtmlFormattingText() {
        Assertions.assertNull(sanitize(null));
        Assertions.assertEquals("", sanitize(""));
        Assertions.assertEquals("12", sanitize(Integer.valueOf(12)));
        Assertions.assertEquals("plain text", sanitize("plain text"));
        Assertions.assertEquals("x &lt; y &gt; z &quot;q&quot; &#39;a&#39;",
                sanitize("x < y > z \"q\" 'a'"));
        Assertions.assertEquals("C\u00F3digo &amp; AT&amp;T",
                sanitize("C&oacute;digo &amp; AT&T"));
        Assertions.assertEquals("&lt;b&gt;", sanitize("&lt;b&gt;"));
        Assertions.assertEquals("abc\tok\n", sanitize("a\u0000b\u0001c\tok\n"));
        Assertions.assertEquals("ab", sanitize("a\uD800b"));
        Assertions.assertEquals("ab", sanitize("a\uFFFEb\uFDD0"));
        Assertions.assertEquals("a\uD83D\uDE00b", sanitize("a\uD83D\uDE00b"));
    }

    @Test
    public void testSanitizeHtmlFormattingElements() {
        Assertions.assertEquals("Please enter a <b>valid</b> <em>value</em>",
                sanitize("Please enter a <b>valid</b> <em>value</em>"));
        Assertions.assertEquals(
                "<b>1</b><strong>2</strong><i>3</i><em>4</em><u>5</u><s>6</s><del>7</del><ins>8</ins>" +
                "<mark>9</mark><small>10</small><sub>11</sub><sup>12</sup><span>13</span>",
                sanitize(
                        "<b>1</b><strong>2</strong><i>3</i><em>4</em><u>5</u><s>6</s><del>7</del><ins>8</ins>" +
                        "<mark>9</mark><small>10</small><sub>11</sub><sup>12</sup><span>13</span>"));
        Assertions.assertEquals("<b>Bold</b> <i>it</i>", sanitize("<B>Bold</B> <I>it</i>"));
        Assertions.assertEquals("a<br />b<br />cd", sanitize("a<br>b<br/>c</br>d"));
        Assertions.assertEquals("<b></b>empty", sanitize("<b/>empty"));
        Assertions.assertEquals("link para",
                sanitize("<a href=\"x\">link</a> <p>para</p>"));
        Assertions.assertEquals("ns custom div", sanitize(
                "<svg:b>ns</svg:b> <my-el>custom</my-el> <div>div</div>"));
    }

    @Test
    public void testSanitizeHtmlFormattingAttributes() {
        Assertions.assertEquals("<b>b</b> <i>i</i>", sanitize(
                "<b onmouseover=\"x()\">b</b> <i style=\"color: red\" class=\"c\">i</i>"));
        Assertions.assertEquals("<span class=\"warn big\">x</span>",
                sanitize("<span class=\"  warn\n big \">x</span>"));
        Assertions.assertEquals("<span class=\"a_1 b-2\">x</span>",
                sanitize("<span CLASS='a_1 b-2'>x</span>"));
        Assertions.assertEquals("<span class=\"ok\">x</span>",
                sanitize("<span style=\"color: red\" class=ok onclick=\"x()\">x</span>"));
        Assertions.assertEquals("<span>x</span> <span>y</span> <span>z</span>",
                sanitize(
                        "<span class=\"bad;x\">x</span> <span class=\"a&quot; onclick=&quot;b\">y</span> <span class=\"\">z</span>"));
        Assertions.assertEquals("<span class=\"first\">x</span>",
                sanitize("<span class=\"first\" class=\"second\">x</span>"));
    }

    @Test
    public void testSanitizeHtmlFormattingDroppedContent() {
        Assertions.assertEquals("after after2 after3 after4",
                sanitize(
                        "<script>x(1)</script>after <style>p { }</style>after2 " +
                        "<textarea><b>t</b></textarea>after3 <SCRIPT type=\"x\">y</SCRIPT>after4"));
        Assertions.assertEquals("still", sanitize("<svg><iframe></svg>still"));
        Assertions.assertEquals("a", sanitize("a<svg><svg></svg><b>x</b></svg>"));
        Assertions.assertEquals("a", sanitize("a<script>x"));
        Assertions.assertEquals("ab", sanitize("a<!-- <b>c</b> -->b"));
        Assertions.assertEquals("ab", sanitize("a<![CDATA[ <b>c</b> ]]>b"));
        Assertions.assertEquals("ab", sanitize("<!DOCTYPE html>a<?pi x?>b"));
    }

    @Test
    public void testSanitizeHtmlFormattingBalancing() {
        Assertions.assertEquals("<b><i>unclosed</i></b>", sanitize("<b><i>unclosed"));
        Assertions.assertEquals("stray <b>xy</b>", sanitize("</i>stray <b>x</i>y</b>"));
        Assertions.assertEquals("<b><i>x</i></b>y", sanitize("<b><i>x</b>y</i>"));
        Assertions.assertEquals("<b><b>x</b>y</b>", sanitize("<b><b>x</b>y</b>"));
    }

    @Test
    public void testSanitizeHtmlFormattingDepth() {
        final StringBuilder input = new StringBuilder();
        final StringBuilder expected = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            input.append("<b>");
        }
        for (int i = 0; i < 10; i++) {
            expected.append("<b>");
        }
        input.append("deep");
        expected.append("deep");
        for (int i = 0; i < 12; i++) {
            input.append("</b>");
        }
        for (int i = 0; i < 10; i++) {
            expected.append("</b>");
        }
        input.append("end");
        expected.append("end");
        Assertions.assertEquals(expected.toString(), sanitize(input.toString()));
        // Closing tags of elements not output due to depth must not close output elements
        Assertions.assertEquals(
                "<i><b><b><b><b><b><b><b><b><b>x</b>y</b></b></b></b></b></b></b></b></i>z",
                sanitize(
                        "<i><b><b><b><b><b><b><b><b><b><b>x</b></b>y</b></b></b></b></b></b></b></b></i>z"));
    }


    // Generates random markup from fragments, including control and lookalike chars, and checks the output
    @Test
    public void testRandomMarkup() {
        final String[] names = {
                "b", "B", "strong", "i", "em", "u", "s", "del", "ins", "mark", "small", "sub", "sup", "span", "SPAN",
                "br", "BR", "script", "SCRIPT", "ScRiPt", "style", "textarea", "title", "template", "iframe", "object",
                "noscript", "nostyle", "noembed", "noframes", "frame", "frameset", "svg", "math", "xmp", "listing",
                "plaintext", "a", "img", "p", "div", "table", "td", "select", "option", "form", "input", "svg:b",
                "my-el", "\u017Fcript", "scr\u0130pt", "b\u0000", "x", "h1", "sc\u0000ript" };
        final String[] fragments = {
                "<", "</", ">", "/>", "<!--", "-->", "--!>", "<![CDATA[", "]]>", "<?", "?>", "<!DOCTYPE html>", "<!",
                "=", "\"", "'", "`", " class=", "class=\"ok\"", " class='a b'", " class=x", " style=\"color:red\"",
                " onclick=\"x()\"", " href=\"javascript:x\"", " src=x onerror=y", "&", "&lt;", "&gt;", "&amp;", "&quot;",
                "&#39;", "&#60;", "&#x3C;", "&#x3c", "&lt", "&#0;", "&#1;", "&#xD800;", "&#xFFFE;", "&#9;", "&#10;",
                "&oacute;", "&notin;", "text", " ", "\t", "\n", "\r", "\f", "\u000B", "\u0000", "\u0001", "\u001F",
                "\u007F", "\u0085", "\u00A0", "\u2028", "\u2029", "\uFEFF", "\uFF1C", "\uFF1E", "\uD800", "\uDC00",
                "\u1F600", "\uFFFE", "\uFDD0", "<3", "< 3", "a<b", "</ b>", "</>", "<b", "<script", ">x<", "\\", "/" };
        final Random random = new Random(1L);
        for (int i = 0; i < 30000; i++) {
            final StringBuilder input = new StringBuilder();
            final int count = random.nextInt(25);
            for (int j = 0; j < count; j++) {
                if (random.nextInt(10) < 3) {
                    input.append('<').append(random.nextInt(4) == 0 ? "/" : "").append(names[random.nextInt(names.length)]);
                    if (random.nextBoolean()) {
                        input.append(fragments[random.nextInt(fragments.length)]);
                    }
                    input.append(random.nextInt(5) == 0 ? "/>" : ">");
                } else {
                    input.append(fragments[random.nextInt(fragments.length)]);
                }
            }
            assertValidOutput(input.toString());
        }
    }

    // Generates random well-formed markup together with its exact expected output, and compares them
    @Test
    public void testRandomTrees() {
        final Random random = new Random(1L);
        for (int i = 0; i < 20000; i++) {
            final StringBuilder input = new StringBuilder();
            final StringBuilder expected = new StringBuilder();
            generateTree(random, input, expected, 0, 0, false, (i % 2 == 0) ? 8 + random.nextInt(9) : -1);
            Assertions.assertEquals(expected.toString(), sanitize(input.toString()), input.toString());
        }
    }

    private static void generateTree(
            final Random random, final StringBuilder input, final StringBuilder expected,
            final int level, final int outputDepth, final boolean dropped, final int chainLength) {
        final String[] texts = {
                "hello", "a < b", "x > y", "Tom & Jerry", "\"quoted\"", "it's", "C\u00F3digo", "tab\there", "line\nfeed",
                "cr\r\nlf", "\u00A0nbsp", "\u1F600", "100%", "a=b", "{0}" };
        final boolean chain = chainLength > 0 && level < chainLength;
        final int children = chain ? 1 + random.nextInt(2) : random.nextInt(level < 4 ? 4 : 2);
        for (int i = 0; i < children; i++) {
            final int kind =
                    chain ? (i == 0 ? (random.nextInt(10) < 8 ? 5 + random.nextInt(3) : 8 + random.nextInt(2)) : random.nextInt(5))
                          : random.nextInt(10);
            if (kind < 4 || level > 18) {
                final String text = texts[random.nextInt(texts.length)];
                input.append(randomlyEscape(random, text));
                if (!dropped) {
                    expected.append(escape(text));
                }
            } else if (kind < 5) {
                input.append('<').append(randomCase(random, "br")).append(random.nextBoolean() ? ">" : "/>");
                if (!dropped) {
                    expected.append("<br />");
                }
            } else if (kind < 8) {
                final String name = ALLOWED[random.nextInt(ALLOWED.length)];
                String classValue = null;
                final StringBuilder attributes = new StringBuilder(randomAttribute(random));
                if ("span".equals(name) && random.nextInt(3) == 0) {
                    classValue = random.nextBoolean() ? "warn" : "a-1 b_2";
                    attributes.append(" class=\"")
                            .append(random.nextBoolean() ? classValue : " " + classValue.replace(" ", "\n ") + " ").append('"');
                }
                input.append('<').append(randomCase(random, name)).append(attributes).append('>');
                final boolean output = !dropped && outputDepth < 10;
                if (output) {
                    expected.append('<').append(name).append(classValue == null ? "" : " class=\"" + classValue + "\"").append('>');
                }
                generateTree(random, input, expected, level + 1, output ? outputDepth + 1 : outputDepth, dropped, chainLength);
                input.append("</").append(randomCase(random, name)).append('>');
                if (output) {
                    expected.append("</").append(name).append('>');
                }
            } else if (kind < 9) {
                final String name = OTHER[random.nextInt(OTHER.length)];
                input.append('<').append(randomCase(random, name)).append(randomAttribute(random)).append('>');
                generateTree(random, input, expected, level + 1, outputDepth, dropped, chainLength);
                input.append("</").append(randomCase(random, name)).append('>');
            } else if (!dropped) {
                final String name = DROPPED_CONTENT[random.nextInt(DROPPED_CONTENT.length)];
                final boolean rawText = "script".equals(name) || "style".equals(name);
                input.append('<').append(rawText ? name : randomCase(random, name)).append(randomAttribute(random)).append('>');
                if (rawText) {
                    input.append(random.nextBoolean() ? "if (a < b && c) { x(\"</b>\"); }" : "p { color: red; } <b>");
                } else {
                    generateTree(random, input, new StringBuilder(), level + 1, outputDepth, true, chainLength);
                }
                input.append("</").append(rawText ? name : randomCase(random, name)).append('>');
            }
        }
    }

    private static String randomCase(final Random random, final String name) {
        final StringBuilder result = new StringBuilder();
        for (final char c : name.toCharArray()) {
            result.append(random.nextBoolean() ? Character.toUpperCase(c) : c);
        }
        return result.toString();
    }

    private static String randomAttribute(final Random random) {
        final String[] attributes = { "\n", "\t", " title=\"x > y\"", " onclick='a()'", " style=\"color:red\"", " data-x=1", "", "" };
        return attributes[random.nextInt(attributes.length)];
    }

    private static String randomlyEscape(final Random random, final String text) {
        final StringBuilder result = new StringBuilder();
        for (final char c : text.toCharArray()) {
            switch (c) {
                case '<': result.append(random.nextBoolean() ? "&lt;" : "&#60;"); break;
                case '&': result.append(random.nextBoolean() ? "&amp;" : "&#x26;"); break;
                case '>': result.append(random.nextBoolean() ? ">" : "&gt;"); break;
                case '"': result.append(random.nextBoolean() ? "\"" : "&quot;"); break;
                case '\u00F3': result.append(random.nextBoolean() ? "\u00F3" : "&oacute;"); break;
                default: result.append(c);
            }
        }
        return result.toString();
    }

    private static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }


    private static String repeat(final String text, final int count) {
        final StringBuilder strBuilder = new StringBuilder(text.length() * count);
        for (int i = 0; i < count; i++) {
            strBuilder.append(text);
        }
        return strBuilder.toString();
    }


    // Processing time must grow linearly with the size of the input, whatever its structure
    @Test
    public void testLargeInputs() {
        final int n = 200000;
        final String[] inputs = {
                repeat("<b>", n) + repeat("</i>", n),
                repeat("<b><i>", n) + repeat("</s>", n),
                repeat("<b>", n) + repeat("</b>", n),
                repeat("<i><b>", n) + repeat("</i>", n),
                repeat("<span class=\"a\">", n) + repeat("</b></span>", n),
                repeat("<div><p>", n) + repeat("</p></div></b>", n),
                "<!--" + repeat("a", 5 * n),
                "<b class=\"" + repeat("a", 5 * n),
                "<script>" + repeat("<b>", n),
                repeat("< <1 <!x> </b> <br> &lt; &", n) };
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            for (final String input : inputs) {
                final String output = sanitize(input);
                Assertions.assertNotNull(output);
            }
        });
        Assertions.assertEquals(repeat("<b>", 10) + repeat("</b>", 10), sanitize(repeat("<b>", n) + repeat("</i>", n)));
        Assertions.assertEquals(repeat("<b><i>", 5) + repeat("</i></b>", 5), sanitize(repeat("<b><i>", n) + repeat("</s>", n)));
    }


    // The sanitizer shares its parser, so results must not depend on concurrent use
    @Test
    public void testConcurrentUse() throws Exception {
        final String[] fragments = {
                "<b>", "</b>", "<i>", "</i>", "<span class=\"a b\">", "</span>", "<br>", "<script>", "</script>",
                "<p>", "</p>", "<!--", "-->", "text", "&amp;", "&lt;", "<", "\"", "<b\">", "\u0000", "\u00E9" };
        final Random random = new Random(2L);
        final List<String> inputs = new ArrayList<String>();
        final List<String> expected = new ArrayList<String>();
        for (int i = 0; i < 2000; i++) {
            final StringBuilder input = new StringBuilder();
            final int count = 1 + random.nextInt(200);
            for (int j = 0; j < count; j++) {
                input.append(fragments[random.nextInt(fragments.length)]);
            }
            inputs.add(input.toString());
            expected.add(sanitize(input.toString()));
        }
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            final List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
            for (int t = 0; t < 16; t++) {
                final int offset = t;
                results.add(executor.submit(() -> {
                    for (int k = 0; k < 20; k++) {
                        for (int i = 0; i < inputs.size(); i++) {
                            final int index = (i * 7 + offset * 31 + k) % inputs.size();
                            if (!expected.get(index).equals(sanitize(inputs.get(index)))) {
                                return Boolean.FALSE;
                            }
                        }
                    }
                    return Boolean.TRUE;
                }));
            }
            for (final Future<Boolean> result : results) {
                Assertions.assertTrue(result.get().booleanValue());
            }
        } finally {
            executor.shutdownNow();
        }
    }

}
