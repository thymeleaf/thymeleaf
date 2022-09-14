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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public final class ProcessingInstructionTest {


    @Test
    public void test() {

        final String procInstr1 = "<?something someother and other and other?>";
        final String procInstr2 = "<?anything-else someother and other and other?>";
        final String procInstr3 = "<?anything-else nothing here?>";
        final String procInstr4 = "<?anything-else?>";

        final String target1 = "something";
        final String target2 = "anything-else";

        final String content1 = "someother and other and other";
        final String content2 = "nothing here";
        final String content3 = null;


        ProcessingInstruction d1 = new ProcessingInstruction(
                procInstr1,
                target1,
                content1,
                "template", 11, 4);

        Assertions.assertSame(procInstr1, d1.getProcessingInstruction());
        Assertions.assertSame(target1, d1.getTarget());
        Assertions.assertSame(content1, d1.getContent());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(11, d1.getLine());
        Assertions.assertEquals(4, d1.getCol());

        d1 = new ProcessingInstruction(
                procInstr1,
                target1,
                content1,
                "template", 10, 3);

        Assertions.assertSame(procInstr1, d1.getProcessingInstruction());
        Assertions.assertSame(target1, d1.getTarget());
        Assertions.assertSame(content1, d1.getContent());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(10, d1.getLine());
        Assertions.assertEquals(3, d1.getCol());

        d1 = new ProcessingInstruction(
                target2,
                d1.getContent());

        Assertions.assertEquals(procInstr2, d1.getProcessingInstruction());
        Assertions.assertSame(target2, d1.getTarget());
        Assertions.assertSame(content1, d1.getContent());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new ProcessingInstruction(
                d1.getTarget(),
                content2);

        Assertions.assertEquals(procInstr3, d1.getProcessingInstruction());
        Assertions.assertSame(target2, d1.getTarget());
        Assertions.assertSame(content2, d1.getContent());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new ProcessingInstruction(
                d1.getTarget(),
                content3);

        Assertions.assertEquals(procInstr4, d1.getProcessingInstruction());
        Assertions.assertSame(target2, d1.getTarget());
        Assertions.assertNull(d1.getContent());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());



        ProcessingInstruction d2 =
                new ProcessingInstruction(
                    target1,
                    content1);

        Assertions.assertEquals(procInstr1, d2.getProcessingInstruction());
        Assertions.assertSame(target1, d2.getTarget());
        Assertions.assertSame(content1, d2.getContent());
        Assertions.assertNull(d2.getTemplateName());
        Assertions.assertEquals(-1, d2.getLine());
        Assertions.assertEquals(-1, d2.getCol());


        ProcessingInstruction d3 =
                new ProcessingInstruction(
                        target2,
                        null);

        Assertions.assertEquals(procInstr4, d3.getProcessingInstruction());
        Assertions.assertSame(target2, d3.getTarget());
        Assertions.assertNull(d3.getContent());
        Assertions.assertNull(d3.getTemplateName());
        Assertions.assertEquals(-1, d3.getLine());
        Assertions.assertEquals(-1, d3.getCol());


    }



    
}
