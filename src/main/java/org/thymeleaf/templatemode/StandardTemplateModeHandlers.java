/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templatemode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.templateparser.StandardTemplateParser;
import org.thymeleaf.templateparser.html.LegacyHtml5TemplateParser;
import org.thymeleaf.templateparser.xmlsax.XhtmlAndHtml5NonValidatingSAXTemplateParser;
import org.thymeleaf.templateparser.xmlsax.XhtmlValidatingSAXTemplateParser;
import org.thymeleaf.templateparser.xmlsax.XmlNonValidatingSAXTemplateParser;
import org.thymeleaf.templateparser.xmlsax.XmlValidatingSAXTemplateParser;
import org.thymeleaf.templatewriter.XhtmlHtml5TemplateWriter;
import org.thymeleaf.templatewriter.XmlTemplateWriter;

/**
 * <p>
 *   Utility class that defines the standard set of {@link ITemplateModeHandler} objects.
 * </p>
 * <p>
 *   Standard template modes are:
 * </p>
 * <ul>
 *   <li>XML</li>
 *   <li>VALIDXML</li>
 *   <li>XHTML</li>
 *   <li>VALIDXHTML</li>
 *   <li>HTML5</li>
 *   <li>LEGACYHTML5 (for non XML-formed HTML5 code &ndash;needs tag balancing prior to parsing)</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class StandardTemplateModeHandlers {
    

    // We have to set a maximum pool size. Some environments might set too high
    // numbers for Runtime.availableProcessors (for example, Google App Engine sets
    // this to 1337).
    private static final int MAX_PARSERS_POOL_SIZE = 24;

    public static final ITemplateModeHandler XML;
    public static final ITemplateModeHandler VALIDXML;
    public static final ITemplateModeHandler XHTML;
    public static final ITemplateModeHandler VALIDXHTML;
    public static final ITemplateModeHandler HTML5;
    public static final ITemplateModeHandler LEGACYHTML5;
    
    
    public static final Set<ITemplateModeHandler> ALL_TEMPLATE_MODE_HANDLERS;
    
    
    
    static {

        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final int poolSize = 
                Math.min(
                        (availableProcessors <= 2? availableProcessors : availableProcessors - 1),
                        MAX_PARSERS_POOL_SIZE);
        
        XML = new TemplateModeHandler(
                "XML",
                new XmlNonValidatingSAXTemplateParser(poolSize),
                new XmlTemplateWriter());
        VALIDXML = new TemplateModeHandler(
                "VALIDXML", 
                new XmlValidatingSAXTemplateParser(poolSize),
                new XmlTemplateWriter());
        XHTML = new TemplateModeHandler(
                "XHTML", 
                new XhtmlAndHtml5NonValidatingSAXTemplateParser(poolSize),
                new XhtmlHtml5TemplateWriter());
        VALIDXHTML = new TemplateModeHandler(
                "VALIDXHTML", 
                new XhtmlValidatingSAXTemplateParser(poolSize),
                new XhtmlHtml5TemplateWriter());
        HTML5 = new TemplateModeHandler(
                "HTML5", 
                new StandardTemplateParser(),
                new XhtmlHtml5TemplateWriter());
        LEGACYHTML5 = new TemplateModeHandler(
                "LEGACYHTML5", 
                new LegacyHtml5TemplateParser("LEGACYHTML5", poolSize),
                new XhtmlHtml5TemplateWriter());
        
        ALL_TEMPLATE_MODE_HANDLERS =
                new HashSet<ITemplateModeHandler>(
                        Arrays.asList(
                                new ITemplateModeHandler[] { XML, VALIDXML, XHTML, VALIDXHTML, HTML5, LEGACYHTML5 }));
        
    }
    
    
    

    
    private StandardTemplateModeHandlers() {
        super();
    }
    
    
}
