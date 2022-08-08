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
package org.thymeleaf.standard.serializer;

import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Gregory Fouquet
 *
 */
public class StandardJavaScriptSerializerTest {

    private static final String VALUE0 = "</script>&#22;";


    public static enum SimpleEnum {
        FIRST(true);
        private final boolean odd;

        private SimpleEnum(boolean odd) {
            this.odd = odd;
        }

        public boolean isOdd() {
            return odd;
        }
    }


    public static enum AnonymousEnum {
        FIRST {
            @Override
            public boolean isOdd() {
                return true;
            }
        };
        public abstract boolean isOdd();
    }

    public StandardJavaScriptSerializerTest() {
        super();
    }



    @Test
    public void testPrintTestEnumDefaultJS01() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(false);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(SimpleEnum.FIRST, stringWriter);
        Assertions.assertEquals("\"FIRST\"", stringWriter.toString());

    }


    @Test
    public void testPrintTestEnumJacksonJS01() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(true);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(SimpleEnum.FIRST, stringWriter);
        Assertions.assertEquals("\"FIRST\"", stringWriter.toString());

    }


    @Test
    public void testPrintAnonymousEnumDefaultJS01() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(false);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(AnonymousEnum.FIRST, stringWriter);
        Assertions.assertEquals("\"FIRST\"", stringWriter.toString());

    }


    @Test
    public void testPrintAnonymousEnumJacksonJS01() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(true);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(AnonymousEnum.FIRST, stringWriter);
        Assertions.assertEquals("\"FIRST\"", stringWriter.toString());

    }




    @Test
    public void testPrintTestEnumDefaultJS02() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(false);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(VALUE0, stringWriter);
        Assertions.assertEquals("\"<\\/script>\\u0026#22;\"", stringWriter.toString());

    }


    @Test
    public void testPrintTestEnumJacksonJS02() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(true);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(VALUE0, stringWriter);
        Assertions.assertEquals("\"<\\/script>\\u0026#22;\"", stringWriter.toString());

    }


    @Test
    public void testPrintAnonymousEnumDefaultJS02() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(false);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(VALUE0, stringWriter);
        Assertions.assertEquals("\"<\\/script>\\u0026#22;\"", stringWriter.toString());

    }


    @Test
    public void testPrintAnonymousEnumJacksonJS02() {

        final IStandardJavaScriptSerializer serializer = new StandardJavaScriptSerializer(true);

        final StringWriter stringWriter = new StringWriter();
        serializer.serializeValue(VALUE0, stringWriter);
        Assertions.assertEquals("\"<\\/script>\\u0026#22;\"", stringWriter.toString());

    }




}