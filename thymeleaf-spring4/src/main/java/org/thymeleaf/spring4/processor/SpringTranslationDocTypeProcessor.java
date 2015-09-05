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
package org.thymeleaf.spring4.processor;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.processor.doctype.AbstractDocTypeProcessor;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringTranslationDocTypeProcessor extends AbstractDocTypeProcessor {


    private static final String XHTML1_STRICT_THYMELEAFSPRING4_1_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-1.dtd";
    private static final String XHTML1_STRICT_THYMELEAFSPRING4_2_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-2.dtd";
    private static final String XHTML1_STRICT_THYMELEAFSPRING4_3_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-3.dtd";
    private static final String XHTML1_STRICT_THYMELEAFSPRING4_4_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-spring4-4.dtd";

    private static final String XHTML1_TRANSITIONAL_THYMELEAFSPRING4_1_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring4-1.dtd";
    private static final String XHTML1_TRANSITIONAL_THYMELEAFSPRING4_2_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring4-2.dtd";
    private static final String XHTML1_TRANSITIONAL_THYMELEAFSPRING4_3_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring4-3.dtd";
    private static final String XHTML1_TRANSITIONAL_THYMELEAFSPRING4_4_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-transitional-thymeleaf-spring4-4.dtd";

    private static final String XHTML1_FRAMESET_THYMELEAFSPRING4_1_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring4-1.dtd";
    private static final String XHTML1_FRAMESET_THYMELEAFSPRING4_2_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring4-2.dtd";
    private static final String XHTML1_FRAMESET_THYMELEAFSPRING4_3_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring4-3.dtd";
    private static final String XHTML1_FRAMESET_THYMELEAFSPRING4_4_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml1-frameset-thymeleaf-spring4-4.dtd";

    private static final String XHTML11_THYMELEAFSPRING4_1_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring4-1.dtd";
    private static final String XHTML11_THYMELEAFSPRING4_2_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring4-2.dtd";
    private static final String XHTML11_THYMELEAFSPRING4_3_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring4-3.dtd";
    private static final String XHTML11_THYMELEAFSPRING4_4_SYSTEMID = "http://www.thymeleaf.org/dtd/xhtml11-thymeleaf-spring4-4.dtd";


    private static final String XHTML_1_STRICT_PUBLICID ="-//W3C//DTD XHTML 1.0 Strict//EN";
    private static final String XHTML_1_STRICT_SYSTEMID ="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";

    private static final String XHTML_1_TRANSITIONAL_PUBLICID ="-//W3C//DTD XHTML 1.0 Transitional//EN";
    private static final String XHTML_1_TRANSITIONAL_SYSTEMID ="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";

    private static final String XHTML_1_FRAMESET_PUBLICID ="-//W3C//DTD XHTML 1.0 Frameset//EN";
    private static final String XHTML_1_FRAMESET_SYSTEMID ="http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd";

    private static final String XHTML_11_PUBLICID ="-//W3C//DTD XHTML 1.1//EN";
    private static final String XHTML_11_SYSTEMID ="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";


    private static final Map<String,String> TRANSLATED_SYSTEM_IDS;
    private static final Map<String,String> PUBLIC_IDS_BY_SYSTEM_IDS;




    public static final int PRECEDENCE = 1000;




    static {

        TRANSLATED_SYSTEM_IDS = new HashMap<String, String>();

        TRANSLATED_SYSTEM_IDS.put(XHTML1_STRICT_THYMELEAFSPRING4_1_SYSTEMID, XHTML_1_STRICT_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_STRICT_THYMELEAFSPRING4_2_SYSTEMID, XHTML_1_STRICT_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_STRICT_THYMELEAFSPRING4_3_SYSTEMID, XHTML_1_STRICT_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_STRICT_THYMELEAFSPRING4_4_SYSTEMID, XHTML_1_STRICT_SYSTEMID);

        TRANSLATED_SYSTEM_IDS.put(XHTML1_TRANSITIONAL_THYMELEAFSPRING4_1_SYSTEMID, XHTML_1_TRANSITIONAL_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_TRANSITIONAL_THYMELEAFSPRING4_2_SYSTEMID, XHTML_1_TRANSITIONAL_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_TRANSITIONAL_THYMELEAFSPRING4_3_SYSTEMID, XHTML_1_TRANSITIONAL_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_TRANSITIONAL_THYMELEAFSPRING4_4_SYSTEMID, XHTML_1_TRANSITIONAL_SYSTEMID);

        TRANSLATED_SYSTEM_IDS.put(XHTML1_FRAMESET_THYMELEAFSPRING4_1_SYSTEMID, XHTML_1_FRAMESET_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_FRAMESET_THYMELEAFSPRING4_2_SYSTEMID, XHTML_1_FRAMESET_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_FRAMESET_THYMELEAFSPRING4_3_SYSTEMID, XHTML_1_FRAMESET_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML1_FRAMESET_THYMELEAFSPRING4_4_SYSTEMID, XHTML_1_FRAMESET_SYSTEMID);

        TRANSLATED_SYSTEM_IDS.put(XHTML11_THYMELEAFSPRING4_1_SYSTEMID, XHTML_11_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML11_THYMELEAFSPRING4_2_SYSTEMID, XHTML_11_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML11_THYMELEAFSPRING4_3_SYSTEMID, XHTML_11_SYSTEMID);
        TRANSLATED_SYSTEM_IDS.put(XHTML11_THYMELEAFSPRING4_4_SYSTEMID, XHTML_11_SYSTEMID);


        PUBLIC_IDS_BY_SYSTEM_IDS = new HashMap<String, String>();

        PUBLIC_IDS_BY_SYSTEM_IDS.put(XHTML_1_STRICT_SYSTEMID, XHTML_1_STRICT_PUBLICID);
        PUBLIC_IDS_BY_SYSTEM_IDS.put(XHTML_1_TRANSITIONAL_SYSTEMID, XHTML_1_TRANSITIONAL_PUBLICID);
        PUBLIC_IDS_BY_SYSTEM_IDS.put(XHTML_1_FRAMESET_SYSTEMID, XHTML_1_FRAMESET_PUBLICID);
        PUBLIC_IDS_BY_SYSTEM_IDS.put(XHTML_11_SYSTEMID, XHTML_11_PUBLICID);

    }



    public SpringTranslationDocTypeProcessor() {
        super(TemplateMode.HTML, PRECEDENCE);
    }




    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext, final IDocType docType,
            final IDocTypeStructureHandler structureHandler) {


        if ("SYSTEM".equalsIgnoreCase(docType.getType())) {

            final String translatedSystemId = TRANSLATED_SYSTEM_IDS.get(docType.getSystemId());
            if (translatedSystemId != null) {
                final String translatedPublicId = PUBLIC_IDS_BY_SYSTEM_IDS.get(translatedSystemId);
                docType.setIDs(translatedPublicId, translatedSystemId);
            }

        }

    }


}

