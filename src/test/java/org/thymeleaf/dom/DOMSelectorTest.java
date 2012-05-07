/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.dom;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.thymeleaf.util.DOMUtils;

public class DOMSelectorTest extends TestCase {


    
    public void testDOMSelector() throws Exception {

        final String markup0 =
                "<!DOCTYPE html>\n<html><body><div><h1>Hello!</h1><p>This is the text over here.</p>" +
                "<p>...and another one!</p></div></body></html>";
        
        
        final DOMSelector selector0 = new DOMSelector("//h1");
        final DOMSelector selector1 = new DOMSelector("//p");
        final DOMSelector selector2 = new DOMSelector("/html/p");
        final DOMSelector selector3 = new DOMSelector("/html/body");
        final DOMSelector selector4 = new DOMSelector("/html/body//p");
        final DOMSelector selector5 = new DOMSelector("//body/div/p");
        
        final Document doc0 = DOMUtils.getHtml5DOMFor(new StringReader(markup0)); 
        
        final Document doc1 = doc0.clone(true);
        final Element doc1div = 
                (Element)((NestableNode)((NestableNode)doc1.getChildren().get(0)).getChildren().get(0)).getChildren().get(0);
        final GroupNode doc1divGroupNode = new GroupNode();
        doc1div.moveAllChildren(doc1divGroupNode);
        doc1div.insertChild(0, doc1divGroupNode);
        
        outputResult(selector0, doc0);
        outputResult(selector1, doc0);
        outputResult(selector2, doc0);
        outputResult(selector3, doc0);
        outputResult(selector4, doc0);
        final List<String> list1 = outputResult(selector5, doc0);

        System.out.println(DOMUtils.getHtml5For(doc1));

        
        outputResult(selector0, doc1);
        outputResult(selector1, doc1);
        outputResult(selector2, doc1);
        outputResult(selector3, doc1);
        outputResult(selector4, doc1);
        final List<String> list2 = outputResult(selector5, doc1);
        
        Assert.assertEquals(list1,list2);
        
    }
    
    
    private static List<String> outputResult(final DOMSelector selector, final Document document) {
        final List<Node> selected = selector.select(document.getChildren());
        final List<String> selectedStrs = new ArrayList<String>();
        for (final Node node : selected) {
            selectedStrs.add(DOMUtils.getHtml5For(node));
        }
        System.out.println("**" + selector + "**");
        for (final String selectedStr : selectedStrs) {
            System.out.println(selectedStr);
        }
        return selectedStrs;
    }
    
}
