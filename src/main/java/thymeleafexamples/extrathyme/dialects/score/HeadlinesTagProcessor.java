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
package thymeleafexamples.extrathyme.dialects.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.TagApplicability;
import org.thymeleaf.processor.tag.AbstractMarkupSubstitutionTagProcessor;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import thymeleafexamples.extrathyme.business.entities.Headline;
import thymeleafexamples.extrathyme.business.entities.repositories.HeadlineRepository;

public class HeadlinesTagProcessor extends AbstractMarkupSubstitutionTagProcessor {

    private final Random rand = new Random(System.currentTimeMillis());
    
    
    public HeadlinesTagProcessor() {
        super();
    }
    
    
    public Set<TagApplicability> getTagApplicabilities() {
        return TagApplicability.createSetForTagName("headlines");
    }


    @Override
    protected List<Node> getMarkupSubstitutes(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element) {

        final ApplicationContext appCtx = 
            ((SpringWebContext)arguments.getContext()).getApplicationContext();
        
        final HeadlineRepository headlineRepository = appCtx.getBean(HeadlineRepository.class);
        final List<Headline> headlines = headlineRepository.findAllHeadlines();

        final String order = element.getAttribute("order");

        String headlineText = null;
        if (order != null && order.trim().toLowerCase().equals("random")) {

            final int r = this.rand.nextInt(headlines.size());
            headlineText = headlines.get(r).getText();
            
        } else {
            // order is "latest", only the latest headline will be shown
            
            Collections.sort(headlines);
            headlineText = headlines.get(headlines.size() - 1).getText();
            
        }
        

        final Element container = document.createElement("div");
        container.setAttribute("class", "headlines");

        final Text text = document.createTextNode(headlineText);
        container.appendChild(text);
        
        final List<Node> nodes = new ArrayList<Node>();
        nodes.add(container);
        
        return nodes;
        
    }


}
