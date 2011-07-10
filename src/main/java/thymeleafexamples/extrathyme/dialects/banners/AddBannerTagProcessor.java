/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.extrathyme.dialects.banners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.TagApplicability;
import org.thymeleaf.processor.tag.AbstractMarkupSubstitutionTagProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AddBannerTagProcessor extends AbstractMarkupSubstitutionTagProcessor {

    
    public AddBannerTagProcessor() {
        super();
    }
    
    
    public Set<TagApplicability> getTagApplicabilities() {
        return TagApplicability.createSetForTagName("addbanner");
    }


    @Override
    protected List<Node> getMarkupSubstitutes(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element) {
        
        final List<Node> nodes = new ArrayList<Node>();
        
        nodes.add(document.createTextNode("Testing adding banner!!"));
        
        return nodes;
        
    }


}
