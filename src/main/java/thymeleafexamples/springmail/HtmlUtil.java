/*
 * Copyright 2011 The THYMELEAF team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package thymeleafexamples.springmail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for HTML files.
 */
public class HtmlUtil {

    /**
     * Given an HTML text, extracts body text content.
     */
    public static String extractBody(String html) throws SAXException, IOException {
        
        DOMParser parser = new DOMParser();
        InputSource inputSource = new InputSource(new ByteArrayInputStream(html.getBytes()));
        parser.parse(inputSource);
        Document htmlDoc = parser.getDocument();
        NodeList body = htmlDoc.getElementsByTagName("body");
        
        if (body.getLength() > 0) {
            return body.item(0).getTextContent();
        }
        
        return null;
        
    }

}
