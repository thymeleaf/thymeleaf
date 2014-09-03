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
package org.thymeleaf.engine.markup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.GroupNode;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class MarkupSelectorItem implements Serializable {

    private static final long serialVersionUID = -1380332779264140399L;

    private final boolean anyDepth;

    private final boolean textSelector;
    private final String idSelector;
    private final String classSelector;
    private final String referenceSelector;

    private final String elementNameSelector;
    private final AttributeCondition[] attributeSelectors;





    private MarkupSelectorItem(
            final boolean anyDepth,
            final boolean textSelector, final String idSelector, final String classSelector, final String referenceSelector,
            final String elementNameSelector, final AttributeCondition[] attributeSelectors) {

        super();

        this.anyDepth = anyDepth;
        this.textSelector = textSelector;
        this.idSelector = idSelector;
        this.classSelector = classSelector;
        this.referenceSelector = referenceSelector;
        this.elementNameSelector = elementNameSelector;
        this.attributeSelectors = attributeSelectors;

    }





    private static final class AttributeCondition implements Serializable {


        private static final long serialVersionUID = -1098331123264140911L;


        static enum Operator {
                EQUALS, NOT_EQUALS, STARTS_WITH, ENDS_WITH;

                static Operator parse(final String operatorStr) {
                    if (operatorStr == null) {
                        return null;
                    }
                    if ("=".equals(operatorStr)) {
                        return EQUALS;
                    }
                    if ("!=".equals(operatorStr)) {
                        return NOT_EQUALS;
                    }
                    if ("^=".equals(operatorStr)) {
                        return STARTS_WITH;
                    }
                    if ("$=".equals(operatorStr)) {
                        return ENDS_WITH;
                    }
                    return null;
                }

                static String[] extractOperator(final String specification) {
                    final int equalsPos = specification.indexOf('=');
                    if (equalsPos == -1) {
                        return new String[] {specification.trim(), null, null};
                    }
                    final char cprev = specification.charAt(equalsPos - 1);
                    switch (cprev) {
                        case '!':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "!=",
                                    specification.substring(equalsPos + 1).trim()};
                        case '^':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "^=",
                                    specification.substring(equalsPos + 1).trim()};
                        case '$':
                            return new String[] {
                                    specification.substring(0, equalsPos - 1).trim(), "$=",
                                    specification.substring(equalsPos + 1).trim()};
                        default:
                            return new String[] {
                                    specification.substring(0, equalsPos).trim(), "=",
                                    specification.substring(equalsPos + 1).trim()};
                    }
                }

            }


        private final String name;
        private final Operator operator;
        private final String value;

        AttributeCondition(final String name, final Operator operator, final String value) {
            super();
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        String getName() {
            return this.name;
        }

        Operator getOperator() {
            return this.operator;
        }

        String getValue() {
            return this.value;
        }

    }



}