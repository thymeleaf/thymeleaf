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
package org.thymeleaf;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.ElementDefinitions;
import org.thymeleaf.engine.ProcessorAggregationTestDialect;
import org.thymeleaf.templatemode.TemplateMode;


public final class DialectSetConfigurationTest {





    @Test
    public void testProcessorComputation01() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildDialect("standard", "th",
                        "CD-10-cdataone,CD-5-cdatatwo,C-20-comone,E-20-null-src,N-ELEMENT-10-test-null",
                        "CD-5-cdataxml");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration("wo",dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));

        Assertions.assertEquals("[standard,th,[CD-10-cdataone, CD-5-cdatatwo, C-20-comone, E-20-null-{wo:src,data-wo-src}, N-ELEMENT-10-{wo:test,wo-test}-null, CD-5-cdataxml]]", dialect.toString(dialectConfiguration.getPrefix()));
        Assertions.assertEquals("[CD-5-cdatatwo, CD-10-cdataone]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[CD-5-cdataxml]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.XML).toString());

    }


    @Test
    public void testProcessorComputation02() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "TH",
                        "E-20-null-src, E-10-null-src,E-20-null-href,E-20-null-text,E-10-null-text,E-10-*div-text,E-15-*div-src,E-1-form-*action,E-20-form-null,E-10-null-*action,E-50-null-null");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));
        final AttributeDefinitions attributeDefinitions = dialectSetConfiguration.getAttributeDefinitions();
        final ElementDefinitions elementDefinitions = dialectSetConfiguration.getElementDefinitions();

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[E-10-null-{th:src,data-th-src}, E-15-{div}-{th:src,data-th-src}, E-20-null-{th:src,data-th-src}]",attributeDefinitions.forHTMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-1-{th:form,th-form}-{action}, E-10-null-{action}]",attributeDefinitions.forHTMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-20-{th:form,th-form}-null, E-50-null-null]",elementDefinitions.forHTMLName("th","form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forHTMLName("th","utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-50-null-null]",elementDefinitions.forHTMLName("p").getAssociatedProcessors().toString());

    }


    @Test
    public void testProcessorComputation03() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildDialect("standard", "TH",
                        "E-20-null-src, E-10-null-src,E-20-null-href,E-20-null-text,E-10-null-text,E-10-*div-text,E-15-*div-src,E-1-form-*action,E-20-form-null,E-10-null-*action,E-50-null-null",
                        "E-200-null-src, E-100-null-src,E-200-null-href,E-200-null-text,E-100-null-text,E-100-*div-text,E-150-*div-src,E-10-form-*action,E-200-form-null,E-100-null-*action,E-500-null-null");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));
        final AttributeDefinitions attributeDefinitions = dialectSetConfiguration.getAttributeDefinitions();
        final ElementDefinitions elementDefinitions = dialectSetConfiguration.getElementDefinitions();

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[E-10-null-{th:src,data-th-src}, E-15-{div}-{th:src,data-th-src}, E-20-null-{th:src,data-th-src}]",attributeDefinitions.forHTMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-1-{th:form,th-form}-{action}, E-10-null-{action}]",attributeDefinitions.forHTMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-20-{th:form,th-form}-null, E-50-null-null]",elementDefinitions.forHTMLName("th","form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forHTMLName("th","utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-50-null-null]",elementDefinitions.forHTMLName("p").getAssociatedProcessors().toString());

        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-100-null-{TH:src}, E-150-{div}-{TH:src}, E-200-null-{TH:src}]",attributeDefinitions.forXMLName("TH:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-10-{TH:form}-{action}, E-100-null-{action}]",attributeDefinitions.forXMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-200-{TH:form}-null, E-500-null-null]",elementDefinitions.forXMLName("TH", "form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("th", "utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("TH", "utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-500-null-null]",elementDefinitions.forXMLName("p").getAssociatedProcessors().toString());

    }


    @Test
    public void testProcessorComputation04() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildDialect("standard", "TH",
                        "N-ELEMENT-20-null-src, N-ELEMENT-10-null-src,N-ELEMENT-20-null-href,N-ELEMENT-20-null-text,N-ELEMENT-10-null-text,N-ELEMENT-10-*div-text,N-ELEMENT-15-*div-src,N-ELEMENT-1-form-*action,N-ELEMENT-20-form-null,N-ELEMENT-10-null-*action,N-ELEMENT-50-null-null",
                        "N-ELEMENT-200-null-src, N-ELEMENT-100-null-src,N-ELEMENT-200-null-href,N-ELEMENT-200-null-text,N-ELEMENT-100-null-text,N-ELEMENT-100-*div-text,N-ELEMENT-150-*div-src,N-ELEMENT-10-form-*action,N-ELEMENT-200-form-null,N-ELEMENT-100-null-*action,N-ELEMENT-500-null-null");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));
        final AttributeDefinitions attributeDefinitions = dialectSetConfiguration.getAttributeDefinitions();
        final ElementDefinitions elementDefinitions = dialectSetConfiguration.getElementDefinitions();

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[N-ELEMENT-10-null-{th:src,data-th-src}, N-ELEMENT-15-{div}-{th:src,data-th-src}, N-ELEMENT-20-null-{th:src,data-th-src}]",attributeDefinitions.forHTMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-1-{th:form,th-form}-{action}, N-ELEMENT-10-null-{action}]",attributeDefinitions.forHTMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-20-{th:form,th-form}-null, N-ELEMENT-50-null-null]",elementDefinitions.forHTMLName("th","form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forHTMLName("th","utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-50-null-null]",elementDefinitions.forHTMLName("p").getAssociatedProcessors().toString());

        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-100-null-{TH:src}, N-ELEMENT-150-{div}-{TH:src}, N-ELEMENT-200-null-{TH:src}]",attributeDefinitions.forXMLName("TH:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-10-{TH:form}-{action}, N-ELEMENT-100-null-{action}]",attributeDefinitions.forXMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-200-{TH:form}-null, N-ELEMENT-500-null-null]",elementDefinitions.forXMLName("TH", "form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("th", "utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forXMLName("TH", "utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-500-null-null]",elementDefinitions.forXMLName("p").getAssociatedProcessors().toString());

    }


    @Test
    public void testProcessorComputation05() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildDialect("standard", "TH",
                        "N-ELEMENT-20-null-src,E-20-null-text,E-10-null-text,E-15-*div-src,E-1-form-*action,N-ELEMENT-20-form-null,E-10-null-*action,E-50-null-null,T-25-uye,T-10-eo",
                        "T-10-eoX");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));
        final AttributeDefinitions attributeDefinitions = dialectSetConfiguration.getAttributeDefinitions();
        final ElementDefinitions elementDefinitions = dialectSetConfiguration.getElementDefinitions();

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[E-15-{div}-{th:src,data-th-src}, N-ELEMENT-20-null-{th:src,data-th-src}]",attributeDefinitions.forHTMLName("th:src").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-1-{th:form,th-form}-{action}, E-10-null-{action}]",attributeDefinitions.forHTMLName("action").getAssociatedProcessors().toString());
        Assertions.assertEquals("[N-ELEMENT-20-{th:form,th-form}-null, E-50-null-null]",elementDefinitions.forHTMLName("th", "form").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]",attributeDefinitions.forHTMLName("th", "utext").getAssociatedProcessors().toString());
        Assertions.assertEquals("[E-50-null-null]",elementDefinitions.forHTMLName("p").getAssociatedProcessors().toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[T-10-eo, T-25-uye]", dialectSetConfiguration.getTextProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[T-10-eoX]", dialectSetConfiguration.getTextProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.HTML).toString());

        Assertions.assertEquals("[]", dialectSetConfiguration.getCommentProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.HTML).toString());

        Assertions.assertEquals("[]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getCommentProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.XML).toString());

    }




    @Test
    public void testProcessorComputation06() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildHTMLDialect("standard", "TH",
                        "CD-4-other,C-4-other,DT-4-other,PI-4-other,T-4-other,XD-4-other");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[CD-4-other]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[C-4-other]", dialectSetConfiguration.getCommentProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[DT-4-other]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[PI-4-other]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[T-4-other]", dialectSetConfiguration.getTextProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[XD-4-other]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.HTML).toString());

        Assertions.assertEquals("[]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getCommentProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getTextProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.XML).toString());

    }




    @Test
    public void testProcessorComputation07() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildXMLDialect("standard", "TH",
                        "CD-4-other,C-4-other,DT-4-other,PI-4-other,T-4-other,XD-4-other");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[CD-4-other]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[C-4-other]", dialectSetConfiguration.getCommentProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[DT-4-other]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[PI-4-other]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[T-4-other]", dialectSetConfiguration.getTextProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[XD-4-other]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.XML).toString());

        Assertions.assertEquals("[]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getCommentProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getTextProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.HTML).toString());

    }




    @Test
    public void testProcessorComputation08() {

        final ProcessorAggregationTestDialect dialect =
                ProcessorAggregationTestDialect.buildDialect("standard", "TH",
                        "CD-4-other,C-4-other,DT-4-other,PI-4-other,T-4-other,XD-4-other",
                        "CD-40-other,C-40-other,DT-40-other,PI-40-other,T-40-other,XD-40-other");

        final DialectConfiguration dialectConfiguration = new DialectConfiguration(dialect);
        final DialectSetConfiguration dialectSetConfiguration = DialectSetConfiguration.build(Collections.singleton(dialectConfiguration));

        Assertions.assertEquals("TH", dialect.getPrefix());

        Assertions.assertEquals("[CD-4-other]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[C-4-other]", dialectSetConfiguration.getCommentProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[DT-4-other]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[PI-4-other]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[T-4-other]", dialectSetConfiguration.getTextProcessors(TemplateMode.HTML).toString());
        Assertions.assertEquals("[XD-4-other]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.HTML).toString());

        Assertions.assertEquals("[CD-40-other]", dialectSetConfiguration.getCDATASectionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[C-40-other]", dialectSetConfiguration.getCommentProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[DT-40-other]", dialectSetConfiguration.getDocTypeProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[PI-40-other]", dialectSetConfiguration.getProcessingInstructionProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[T-40-other]", dialectSetConfiguration.getTextProcessors(TemplateMode.XML).toString());
        Assertions.assertEquals("[XD-40-other]", dialectSetConfiguration.getXMLDeclarationProcessors(TemplateMode.XML).toString());

    }






}
