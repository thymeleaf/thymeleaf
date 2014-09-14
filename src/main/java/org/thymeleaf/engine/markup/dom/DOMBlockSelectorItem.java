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
package org.thymeleaf.engine.markup.dom;

import org.thymeleaf.engine.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class DOMBlockSelectorItem {

    private boolean inMatchingBlock = false;
    private boolean removeAtEnd = false;

    private final boolean anyLevel;
    private final String elementName;
    private final int elementNameLen;




    DOMBlockSelectorItem(final boolean anyLevel, final String elementName) {

        super();

        this.anyLevel = anyLevel;
        this.elementName = elementName.toLowerCase();
        this.elementNameLen = this.elementName.length();

    }




    public DOMBlockSelectorMatch onStandaloneElementStart(final String normalizedElementName) {

        if (this.inMatchingBlock) {
            return DOMBlockSelectorMatch.MATCHES;
        }

        if (TextUtil.equals(false, this.elementName, 0, this.elementNameLen, buffer, offset, len)) {
            // This is a standalone element, so there is no need to set the "inMatchingBlock" flag.
            return DOMBlockSelectorMatch.MATCHES;
        }

        return DOMBlockSelectorMatch.DOESNT_MATCH;

    }


    public DOMBlockSelectorMatch onOpenElementStart(final String normalizedElementName) {

        if (this.inMatchingBlock) {
            return DOMBlockSelectorMatch.MATCHES;
        }

        if (TextUtil.equals(false, this.elementName, 0, this.elementNameLen, buffer, offset, len)) {
            // This is a standalone element, so there is no need to set the "inMatchingBlock" flag.
            return DOMBlockSelectorMatch.MATCHES;
        }

        return DOMBlockSelectorMatch.DOESNT_MATCH;

    }


    public void onOpenElementEnd(final String normalizedElementName) {
        // Nothing to be done here
    }


    public DOMBlockSelectorMatch onCloseElementStart(final char[] buffer, final int offset, final int len) {
        return null;
    }


    public void onCloseElementEnd(final char[] buffer, final int offset, final int len) {

    }


}
