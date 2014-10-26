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
package org.thymeleaf.aurora.engine;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class Element {

    ElementDefinition definition;
    String name;
    int line;
    int col;
    Attribute[] attributes;
    InnerWhiteSpace[] innerWhiteSpaces;




    Element() {
        super();
    }


    public ElementDefinition getDefinition() {
        return this.definition;
    }

    public String getName() {
        return this.name;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    InnerWhiteSpace[] getInnerWhiteSpaces() {
        return innerWhiteSpaces;
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }



    static class InnerWhiteSpace {

        String whiteSpace;
        int line;
        int col;

    }

}
