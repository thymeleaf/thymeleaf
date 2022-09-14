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
package org.thymeleaf.dialect;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.dialectordering.Dialect01;
import org.thymeleaf.dialect.dialectordering.Dialect02;
import org.thymeleaf.dialect.dialectordering.Dialect03;
import org.thymeleaf.dialect.dialectordering.Dialect04;
import org.thymeleaf.dialect.dialectordering.Div1Processor;
import org.thymeleaf.dialect.dialectordering.Div2Processor;
import org.thymeleaf.dialect.dialectordering.Div4Processor;
import org.thymeleaf.dialect.dialectordering.P1Processor;
import org.thymeleaf.dialect.dialectordering.P2Processor;
import org.thymeleaf.dialect.dialectordering.P3Processor;
import org.thymeleaf.dialect.dialectordering.P4Processor;
import org.thymeleaf.dialect.dialectordering.Span3Processor;
import org.thymeleaf.dialect.dialectordering.Span4Processor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ProcessorConfigurationUtils;


public class DialectOrderingTest {


    public DialectOrderingTest() {
        super();
    }
    
    
    
    
    @Test
    public void testDialectOrder() throws Exception {

        final Dialect01 dialect01 = new Dialect01();
        final Dialect02 dialect02 = new Dialect02();
        final Dialect03 dialect03 = new Dialect03();
        final Dialect04 dialect04 = new Dialect04();

        final Set<IElementProcessor> dialect01Processors = combineDialects(dialect01);
        final Set<IElementProcessor> dialect02Processors = combineDialects(dialect02);
        final Set<IElementProcessor> dialect03Processors = combineDialects(dialect03);
        final Set<IElementProcessor> dialect04Processors = combineDialects(dialect04);

        List<IProcessor> d01ProcList = new ArrayList<IProcessor>(dialect01Processors);
        List<IProcessor> d02ProcList = new ArrayList<IProcessor>(dialect02Processors);
        List<IProcessor> d03ProcList = new ArrayList<IProcessor>(dialect03Processors);
        List<IProcessor> d04ProcList = new ArrayList<IProcessor>(dialect04Processors);


        Assertions.assertEquals(2, d01ProcList.size());
        Assertions.assertEquals(2, d02ProcList.size());
        Assertions.assertEquals(2, d03ProcList.size());
        Assertions.assertEquals(3, d04ProcList.size());


        Assertions.assertEquals(P1Processor.class.getSimpleName(), unwrappedClassName(d01ProcList.get(0)));    // 110 - 1000
        Assertions.assertEquals(Div1Processor.class.getSimpleName(), unwrappedClassName(d01ProcList.get(1)));  // 110 - 1100

        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d02ProcList.get(0)));  // 100 -  900
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d02ProcList.get(1)));    // 100 - 1100

        Assertions.assertEquals(P3Processor.class.getSimpleName(), unwrappedClassName(d03ProcList.get(0)));    //  90 -  800
        Assertions.assertEquals(Span3Processor.class.getSimpleName(), unwrappedClassName(d03ProcList.get(1))); //  90 -  900

        Assertions.assertEquals(Span4Processor.class.getSimpleName(), unwrappedClassName(d04ProcList.get(0))); // 100 -  700
        Assertions.assertEquals(Div4Processor.class.getSimpleName(), unwrappedClassName(d04ProcList.get(1)));  // 100 - 1000
        Assertions.assertEquals(P4Processor.class.getSimpleName(), unwrappedClassName(d04ProcList.get(2)));    // 100 - 1200


        List<IElementProcessor> d0102ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect01, dialect02));

        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d0102ProcList.get(0)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d0102ProcList.get(1)));
        Assertions.assertEquals(P1Processor.class.getSimpleName(), unwrappedClassName(d0102ProcList.get(2)));
        Assertions.assertEquals(Div1Processor.class.getSimpleName(), unwrappedClassName(d0102ProcList.get(3)));


        List<IElementProcessor> d0103ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect01, dialect03));

        Assertions.assertEquals(P3Processor.class.getSimpleName(), unwrappedClassName(d0103ProcList.get(0)));
        Assertions.assertEquals(Span3Processor.class.getSimpleName(), unwrappedClassName(d0103ProcList.get(1)));
        Assertions.assertEquals(P1Processor.class.getSimpleName(), unwrappedClassName(d0103ProcList.get(2)));
        Assertions.assertEquals(Div1Processor.class.getSimpleName(), unwrappedClassName(d0103ProcList.get(3)));


        List<IElementProcessor> d0203ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect02, dialect03));

        Assertions.assertEquals(P3Processor.class.getSimpleName(), unwrappedClassName(d0203ProcList.get(0)));
        Assertions.assertEquals(Span3Processor.class.getSimpleName(), unwrappedClassName(d0203ProcList.get(1)));
        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d0203ProcList.get(2)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d0203ProcList.get(3)));


        List<IElementProcessor> d010204ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect01, dialect02, dialect04));

        Assertions.assertEquals(Span4Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(0)));
        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(1)));
        Assertions.assertEquals(Div4Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(2)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(3)));
        Assertions.assertEquals(P4Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(4)));
        Assertions.assertEquals(P1Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(5)));
        Assertions.assertEquals(Div1Processor.class.getSimpleName(), unwrappedClassName(d010204ProcList.get(6)));


        List<IElementProcessor> d010402ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect01, dialect04, dialect02));

        Assertions.assertEquals(Span4Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(0)));
        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(1)));
        Assertions.assertEquals(Div4Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(2)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(3)));
        Assertions.assertEquals(P4Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(4)));
        Assertions.assertEquals(P1Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(5)));
        Assertions.assertEquals(Div1Processor.class.getSimpleName(), unwrappedClassName(d010402ProcList.get(6)));


        List<IElementProcessor> d030402ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect03, dialect04, dialect02));

        Assertions.assertEquals(P3Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(0)));
        Assertions.assertEquals(Span3Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(1)));
        Assertions.assertEquals(Span4Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(2)));
        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(3)));
        Assertions.assertEquals(Div4Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(4)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(5)));
        Assertions.assertEquals(P4Processor.class.getSimpleName(), unwrappedClassName(d030402ProcList.get(6)));


        List<IElementProcessor> d020403ProcList = new ArrayList<IElementProcessor>(combineDialects(dialect02, dialect04, dialect03));

        Assertions.assertEquals(P3Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(0)));
        Assertions.assertEquals(Span3Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(1)));
        Assertions.assertEquals(Span4Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(2)));
        Assertions.assertEquals(Div2Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(3)));
        Assertions.assertEquals(Div4Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(4)));
        Assertions.assertEquals(P2Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(5)));
        Assertions.assertEquals(P4Processor.class.getSimpleName(), unwrappedClassName(d020403ProcList.get(6)));

    }
    

    private static String unwrappedClassName(final IProcessor processor) {
        if (processor instanceof IElementProcessor) {
            return ProcessorConfigurationUtils.unwrap((IElementProcessor) processor).getClass().getSimpleName();
        }
        throw new IllegalArgumentException("This test is only meant for element processors");
    }


    private static Set<IElementProcessor> combineDialects(final IDialect... dialects) {

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setDialect(dialects[0]);
        for (int i = 1; i < dialects.length; i++) {
            templateEngine.addDialect(dialects[i]);
        }

        return templateEngine.getConfiguration().getElementProcessors(TemplateMode.HTML);

    }


}
