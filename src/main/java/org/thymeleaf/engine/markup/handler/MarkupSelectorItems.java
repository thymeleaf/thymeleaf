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
package org.thymeleaf.engine.markup.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class MarkupSelectorItems {


    private static final SelectorRepository NO_REFERENCE_RESOLVER_REPOSITORY = new SelectorRepository();
    private static final ConcurrentHashMap<IMarkupSelectorReferenceResolver,SelectorRepository> REPOSITORIES_BY_REFERENCE_RESOLVER =
            new ConcurrentHashMap<IMarkupSelectorReferenceResolver, SelectorRepository>(20);



    private static final String selectorPatternStr = "^(/{1,2})([^/\\s]*?)(\\[(?:.*)\\])?$";
    private static final Pattern selectorPattern = Pattern.compile(selectorPatternStr);
    private static final String modifiersPatternStr = "^(?:\\[(.*?)\\])(\\[(?:.*)\\])?$";
    private static final Pattern modifiersPattern = Pattern.compile(modifiersPatternStr);




    static List<IMarkupSelectorItem> forSelector(
            final boolean caseSensitive, final String selector, final IMarkupSelectorReferenceResolver referenceResolver) {

        if (StringUtils.isEmptyOrWhitespace(selector)) {
            throw new IllegalArgumentException("Selector cannot be null");
        }

        final ConcurrentHashMap<String,List<IMarkupSelectorItem>> map;
        if (referenceResolver == null) {
            map = NO_REFERENCE_RESOLVER_REPOSITORY.getMap(caseSensitive);
        } else {
            if (!REPOSITORIES_BY_REFERENCE_RESOLVER.containsKey(referenceResolver)) {
                if (REPOSITORIES_BY_REFERENCE_RESOLVER.size() < SelectorRepository.SELECTOR_ITEMS_MAX_SIZE) {
                    // We will use the same max size for reference resolvers and selectors per resolver (1000)
                    REPOSITORIES_BY_REFERENCE_RESOLVER.putIfAbsent(referenceResolver, new SelectorRepository());
                }
            }
            map = REPOSITORIES_BY_REFERENCE_RESOLVER.get(referenceResolver).getMap(caseSensitive);
        }

        List<IMarkupSelectorItem> items = map.get(selector);
        if (items != null) {
            return items;
        }

        items = Collections.unmodifiableList(parseSelector(caseSensitive, selector, referenceResolver));

        if (map.size() < SelectorRepository.SELECTOR_ITEMS_MAX_SIZE) {
            map.putIfAbsent(selector, items);
        }

        return items;

    }



    static final class SelectorRepository {

        private static final int SELECTOR_ITEMS_MAX_SIZE = 1000; // Just in case some crazy uses of this are done
        private final ConcurrentHashMap<String,List<IMarkupSelectorItem>> CASE_INSENSITIVE_SELECTOR_ITEMS =
                new ConcurrentHashMap<String, List<IMarkupSelectorItem>>(20);
        private final ConcurrentHashMap<String,List<IMarkupSelectorItem>> CASE_SENSITIVE_SELECTOR_ITEMS =
                new ConcurrentHashMap<String, List<IMarkupSelectorItem>>(20);


        ConcurrentHashMap<String,List<IMarkupSelectorItem>> getMap(final boolean caseSensitive) {
            return (caseSensitive ? CASE_SENSITIVE_SELECTOR_ITEMS : CASE_INSENSITIVE_SELECTOR_ITEMS);
        }

    }






    static List<IMarkupSelectorItem> parseSelector(
            final boolean caseSensitive, final String selector,
            final IMarkupSelectorReferenceResolver referenceResolver) {
        return parseSelector(caseSensitive, selector, null, null, referenceResolver);
    }


    private static List<IMarkupSelectorItem> parseSelector(
            final boolean caseSensitive, final String selector,
            final MarkupSelectorItem.IAttributeCondition initialAttributeCondition,
            final MarkupSelectorItem.IndexCondition initialIndexCondition,
            final IMarkupSelectorReferenceResolver referenceResolver) {

        /*
         * STRATEGY: We will divide the Selector into several, one for each level, and chain them all using the
         * 'next' property. That way, a '/x//y[0]/z[@id='a']' selector will be divided into three chained selectors,
         * like: '/x' -(next)-> '//y[0]' -(next)-> '/z[@id='a']'
         */

        String selectorSpecStr = selector.trim();
        if (!selectorSpecStr.startsWith("/")) {
            // "x" is equivalent to "//x"
            selectorSpecStr = "//" + selectorSpecStr;
        }

        final int selectorSpecStrLen = selectorSpecStr.length();
        int firstNonSlash = 0;
        while (firstNonSlash < selectorSpecStrLen && selectorSpecStr.charAt(firstNonSlash) == '/') {
            firstNonSlash++;
        }

        if (firstNonSlash >= selectorSpecStrLen) {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector \"" + selector + "\": '/' should be followed by " +
                            "further selector specification");
        }

        final List<IMarkupSelectorItem> result;
        final int selEnd = selectorSpecStr.substring(firstNonSlash).indexOf('/');
        if (selEnd != -1) {
            final String tail = selectorSpecStr.substring(firstNonSlash).substring(selEnd);
            selectorSpecStr = selectorSpecStr.substring(0, firstNonSlash + selEnd);
            result = parseSelector(caseSensitive, tail, referenceResolver);
        } else {
            result = new ArrayList(3);
        }

        final Matcher matcher = selectorPattern.matcher(selectorSpecStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                            "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }

        final String rootGroup = matcher.group(1);
        final String selectorNameGroup = matcher.group(2);
        final String modifiersGroup = matcher.group(3);

        if (rootGroup == null) {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                            "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }

        final boolean anyLevel;
        if ("//".equals(rootGroup)) {
            anyLevel = true;
        } else if ("/".equals(rootGroup)) {
            anyLevel = false;
        } else {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                            "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }

        if (selectorNameGroup == null) {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                            "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
        }


        /*
         * ----------------------------------------------------------
         * Process path: extract id, class, reference modifiers...
         * ----------------------------------------------------------
         */

        String path = selectorNameGroup;


        MarkupSelectorItem.IndexCondition index = initialIndexCondition;
        MarkupSelectorItem.IAttributeCondition attributeCondition = initialAttributeCondition;


        final int idModifierPos = path.indexOf(MarkupSelectorItem.ID_MODIFIER_SEPARATOR);
        final int classModifierPos = path.indexOf(MarkupSelectorItem.CLASS_MODIFIER_SEPARATOR);
        final int referenceModifierPos = path.indexOf(MarkupSelectorItem.REFERENCE_MODIFIER_SEPARATOR);


        /*
         * Compute the possible existence of an ID selector: "x#id"
         */

        if (idModifierPos != -1) {
            if (classModifierPos != -1 || referenceModifierPos != -1) {
                throw new IllegalArgumentException(
                        "More than one modifier (id, class, reference) have been specified at " +
                                "selector expression \"" + selector + "\", which is forbidden.");
            }
            final String selectorPathIdModifier = path.substring(idModifierPos + MarkupSelectorItem.ID_MODIFIER_SEPARATOR.length());
            path = path.substring(0, idModifierPos);
            if (StringUtils.isEmptyOrWhitespace(selectorPathIdModifier)) {
                throw new IllegalArgumentException(
                        "Empty id modifier in selector expression " +
                                "\"" + selector + "\", which is forbidden.");
            }
            final MarkupSelectorItem.AttributeCondition newAttributeCondition =
                    new MarkupSelectorItem.AttributeCondition(
                            MarkupSelectorItem.ID_ATTRIBUTE_NAME, MarkupSelectorItem.AttributeCondition.Operator.EQUALS, selectorPathIdModifier);
            if (attributeCondition == null) {
                attributeCondition = newAttributeCondition;
            } else {
                attributeCondition =
                        new MarkupSelectorItem.AttributeConditionRelation(
                                MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition, newAttributeCondition);
            }
        }

        /*
         * Compute the possible existence of a CLASS selector: "x.class"
         */

        if (classModifierPos != -1) {
            if (idModifierPos != -1 || referenceModifierPos != -1) {
                throw new IllegalArgumentException(
                        "More than one modifier (id, class, reference) have been specified at " +
                                "selector expression \"" + selector + "\", which is forbidden.");
            }
            final String selectorPathClassModifier = path.substring(classModifierPos + MarkupSelectorItem.CLASS_MODIFIER_SEPARATOR.length());
            path = path.substring(0, classModifierPos);
            if (StringUtils.isEmptyOrWhitespace(selectorPathClassModifier)) {
                throw new IllegalArgumentException(
                        "Empty id modifier in selector expression " +
                                "\"" + selector + "\", which is forbidden.");
            }
            final MarkupSelectorItem.AttributeCondition newAttributeCondition =
                    new MarkupSelectorItem.AttributeCondition(
                            MarkupSelectorItem.CLASS_ATTRIBUTE_NAME, MarkupSelectorItem.AttributeCondition.Operator.EQUALS, selectorPathClassModifier);
            if (attributeCondition == null) {
                attributeCondition = newAttributeCondition;
            } else {
                attributeCondition =
                        new MarkupSelectorItem.AttributeConditionRelation(
                                MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition, newAttributeCondition);
            }
        }

        /*
         * Compute the possible existence of a REFERENCE selector: "x%ref"
         */

        String selectorPathReferenceModifier = null;
        if (referenceModifierPos != -1) {
            if (idModifierPos != -1 || classModifierPos != -1) {
                throw new IllegalArgumentException(
                        "More than one modifier (id, class, reference) have been specified at " +
                                "selector expression \"" + selector + "\", which is forbidden.");
            }
            selectorPathReferenceModifier = path.substring(referenceModifierPos + MarkupSelectorItem.REFERENCE_MODIFIER_SEPARATOR.length());
            path = path.substring(0, referenceModifierPos);
            if (StringUtils.isEmptyOrWhitespace(selectorPathReferenceModifier)) {
                throw new IllegalArgumentException(
                        "Empty id modifier in selector expression " +
                                "\"" + selector + "\", which is forbidden.");
            }
        }


        /*
         * Compute the possibility that our path selector is a TEXT selector: "text()"
         */

        final boolean textSelector = MarkupSelectorItem.TEXT_SELECTOR.equals(path);


        /*
         * Compute the final path selector we're left with (if any)
         */

        final String caseSensitiveSelectorPath =
                (textSelector ? null : (StringUtils.isEmptyOrWhitespace(path) ? null : path));
        final String selectorPath =
                (caseSensitiveSelectorPath == null? null : (caseSensitive? caseSensitiveSelectorPath : caseSensitiveSelectorPath.toLowerCase()));


        /*
         * Process classifiers: attributes and index.
         */

        if (modifiersGroup != null) {

            /*
             * A selector level can include two types of filters between [...], in this order:
             *   * 1. Attribute based: [@a='X' and @b='Y'], any number of them: [@a='X'][@b='Y']...
             *   * 2. Index based: [23]
             */

            String remainingModifiers = modifiersGroup;

            while (remainingModifiers != null) {

                // This pattern is made to be recursive, acting group 2 as the recursion tail
                final Matcher modifiersMatcher = modifiersPattern.matcher(remainingModifiers);
                if (!modifiersMatcher.matches()) {
                    throw new IllegalArgumentException(
                            "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                                    "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                }

                final String currentModifier = modifiersMatcher.group(1);
                remainingModifiers = modifiersMatcher.group(2);

                final MarkupSelectorItem.IndexCondition newIndex = parseIndex(currentModifier);

                if (newIndex != null) {

                    if (remainingModifiers != null) {
                        // If this is an index, it must be the last modifier!
                        throw new IllegalArgumentException(
                                "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                                        "((/|//)?selector)?([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                    }

                    if (index != null) {
                        // If this is an index, it must be the last modifier!
                        throw new IllegalArgumentException(
                                "Invalid syntax in selector \"" + selector + "\": cannot combine two different index " +
                                "modifiers (probably one was specified in the expression itself, and the other one comes " +
                                "from a reference resolver).");
                    }

                    index = newIndex;

                } else {
                    // Modifier is not an index

                    final MarkupSelectorItem.IAttributeCondition newAttributeCondition = parseAttributeCondition(caseSensitive, selector, currentModifier);
                    if (newAttributeCondition == null) {
                        throw new IllegalArgumentException(
                                "Invalid syntax in selector \"" + selector + "\": selector does not match selector syntax: " +
                                        "(/|//)(selector)([@attrib=\"value\" ((and|or) @attrib2=\"value\")?])?([index])?");
                    }

                    if (attributeCondition == null) {
                        attributeCondition = newAttributeCondition;
                    } else {
                        attributeCondition =
                                new MarkupSelectorItem.AttributeConditionRelation(
                                        MarkupSelectorItem.AttributeConditionRelation.Type.AND, attributeCondition, newAttributeCondition);
                    }

                }

            }

        }

        IMarkupSelectorItem thisItem = new MarkupSelectorItem(caseSensitive, anyLevel, textSelector, selectorPath, index, attributeCondition);

        if (referenceResolver != null && (selectorPathReferenceModifier != null || selectorPath != null)) {

            if (selectorPathReferenceModifier != null) {
                // We will feed the Reference Resolver with a specifically-specified reference value

                String resolvedSelector = referenceResolver.resolveSelectorFromReference(selectorPathReferenceModifier);

                if (resolvedSelector != null) {

                    if (resolvedSelector.startsWith("//")) {
                        if (!anyLevel) {
                            resolvedSelector = resolvedSelector.substring(1); // We remove one slash to make it match
                        }
                    } else if (resolvedSelector.startsWith("/")) {
                        if (anyLevel) {
                            resolvedSelector = "/" + resolvedSelector; // We add a slash to make it match
                        }
                    } else if (!anyLevel) {
                        resolvedSelector = "/" + resolvedSelector;  // We add a slash to make it match
                    }

                    // We don't send the reference resolver again (null)
                    final List<IMarkupSelectorItem> parsedReference = parseSelector(caseSensitive, resolvedSelector, null);
                    if (parsedReference != null && parsedReference.size() > 1) {
                        throw new IllegalArgumentException(
                                "Invalid selector resolved by reference resolver of class " + referenceResolver.getClass().getName() + " " +
                                        " for selector " + selectorPath + ": resolved selector has more than one level, which is forbidden.");
                    }
                    if (parsedReference != null && parsedReference.size() == 1) {
                        thisItem = new MarkupSelectorAndItem(thisItem, parsedReference.get(0));
                    }

                }

            } else {
                // There is no specifically-specified reference value, but given we have a selector path, we should try
                // to use it as a reference (instead of as an element name)

                String resolvedSelector = referenceResolver.resolveSelectorFromReference(caseSensitiveSelectorPath);

                if (resolvedSelector != null) {

                    if (resolvedSelector.startsWith("//")) {
                        if (!anyLevel) {
                            resolvedSelector = resolvedSelector.substring(1); // We remove one slash to make it match
                        }
                    } else if (resolvedSelector.startsWith("/")) {
                        if (anyLevel) {
                            resolvedSelector = "/" + resolvedSelector; // We add a slash to make it match
                        }
                    } else if (!anyLevel) {
                        resolvedSelector = "/" + resolvedSelector;  // We add a slash to make it match
                    }

                    // We don't send the reference resolver again (null)
                    final List<IMarkupSelectorItem> parsedReference = parseSelector(caseSensitive, resolvedSelector, attributeCondition, index, null);
                    if (parsedReference != null && parsedReference.size() > 1) {
                        throw new IllegalArgumentException(
                                "Invalid selector resolved by reference resolver of class " + referenceResolver.getClass().getName() + " " +
                                        " for selector " + selectorPath + ": resolved selector has more than one level, which is forbidden.");
                    }
                    if (parsedReference != null && parsedReference.size() == 1) {
                        thisItem = new MarkupSelectorOrItem(thisItem, parsedReference.get(0));
                    }

                }

            }

        }

        result.add(0, thisItem);

        return result;

    }



    private static MarkupSelectorItem.IndexCondition parseIndex(final String indexGroup) {

        // Look for the 'even()' and 'odd()' selectors
        if (MarkupSelectorItem.ODD_SELECTOR.equals(indexGroup.toLowerCase())) {
            return MarkupSelectorItem.IndexCondition.INDEX_CONDITION_ODD;
        }
        if (MarkupSelectorItem.EVEN_SELECTOR.equals(indexGroup.toLowerCase())) {
            return MarkupSelectorItem.IndexCondition.INDEX_CONDITION_EVEN;
        }

        if (indexGroup.charAt(0) == '>') {

            try {
                return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.MORE_THAN, Integer.valueOf(indexGroup.substring(1).trim()));
            } catch (final Exception ignored) {
                return null;
            }

        } else if (indexGroup.charAt(0) == '<') {

            try {
                return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.LESS_THAN, Integer.valueOf(indexGroup.substring(1).trim()));
            } catch (final Exception ignored) {
                return null;
            }

        }

        try {
            return new MarkupSelectorItem.IndexCondition(MarkupSelectorItem.IndexCondition.IndexConditionType.VALUE, Integer.valueOf(indexGroup.trim()));
        } catch (final Exception ignored) {
            return null;
        }

    }



    private static MarkupSelectorItem.IAttributeCondition parseAttributeCondition(final boolean caseSensitive, final String selectorSpec, final String attrGroup) {

        String text = attrGroup.trim();
        if (text.startsWith("(") && text.endsWith(")")) {
            text = text.substring(1, text.length() - 1);
        }

        final int textLen = text.length();
        if (StringUtils.isEmptyOrWhitespace(text)) {
            throw new IllegalArgumentException(
                    "Invalid syntax in selector: \"" + selectorSpec + "\"");
        }

        int nestingLevel = 0;
        int i = 0;
        while (i < textLen) {

            final char c = text.charAt(i);
            if (c == '(') {
                nestingLevel++;
                continue;
            }
            if (c == ')') {
                nestingLevel--;
                continue;
            }
            if (nestingLevel == 0 && (i + 4 < textLen) &&
                    Character.isWhitespace(c) &&
                    (text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'A') &&
                    (text.charAt(i + 2) == 'n' || text.charAt(i + 2) == 'N') &&
                    (text.charAt(i + 3) == 'd' || text.charAt(i + 3) == 'D') &&
                    Character.isWhitespace(text.charAt(i + 4))) {

                final MarkupSelectorItem.IAttributeCondition left =
                        parseAttributeCondition(caseSensitive, selectorSpec, text.substring(0,i));
                final MarkupSelectorItem.IAttributeCondition right =
                        parseAttributeCondition(caseSensitive, selectorSpec, text.substring(i + 5,textLen));
                return new MarkupSelectorItem.AttributeConditionRelation(
                        MarkupSelectorItem.AttributeConditionRelation.Type.AND, left, right);

            }
            if (nestingLevel == 0 && (i + 3 < textLen) &&
                    Character.isWhitespace(c) &&
                    (text.charAt(i + 1) == 'o' || text.charAt(i + 1) == 'O') &&
                    (text.charAt(i + 2) == 'r' || text.charAt(i + 2) == 'R') &&
                    Character.isWhitespace(text.charAt(i + 3))) {

                final MarkupSelectorItem.IAttributeCondition left =
                        parseAttributeCondition(caseSensitive, selectorSpec, text.substring(0,i));
                final MarkupSelectorItem.IAttributeCondition right =
                        parseAttributeCondition(caseSensitive, selectorSpec, text.substring(i + 4,textLen));
                return new MarkupSelectorItem.AttributeConditionRelation(
                        MarkupSelectorItem.AttributeConditionRelation.Type.OR, left, right);

            }

            i++;

        }

        return parseSimpleAttributeCondition(caseSensitive, selectorSpec, text);

    }


    private static MarkupSelectorItem.AttributeCondition parseSimpleAttributeCondition(
            final boolean caseSensitive, final String selectorSpec, final String attributeSpec) {

        // 0 = attribute name, 1 = operator, 2 = value
        final String[] fragments = tokenizeAttributeSpec(attributeSpec);

        String attrName = fragments[0];
        if (attrName.startsWith("@")) {
            attrName = attrName.substring(1);
        }
        attrName = (caseSensitive? attrName : attrName.toLowerCase());

        final MarkupSelectorItem.AttributeCondition.Operator operator = parseAttributeOperator(fragments[1]);

        final String attrValue = fragments[2];
        if (attrValue != null) {
            if (!(attrValue.startsWith("\"") && attrValue.endsWith("\"")) && !(attrValue.startsWith("'") && attrValue.endsWith("'"))) {
                throw new IllegalArgumentException(
                        "Invalid syntax in selector: \"" + selectorSpec + "\"");
            }
            return new MarkupSelectorItem.AttributeCondition(attrName, operator, attrValue.substring(1, attrValue.length() - 1));
        }
        return new MarkupSelectorItem.AttributeCondition(attrName, operator, null);

    }



    private static String[] tokenizeAttributeSpec(final String specification) {
        final int equalsPos = specification.indexOf('=');
        if (equalsPos == -1) {
            if (specification.charAt(0) == '!') {
                return new String[] {specification.substring(1).trim(), "!", null};
            }
            return new String[] {specification.trim(), "", null};
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
            case '*':
                return new String[] {
                        specification.substring(0, equalsPos - 1).trim(), "*=",
                        specification.substring(equalsPos + 1).trim()};
            default:
                return new String[] {
                        specification.substring(0, equalsPos).trim(), "=",
                        specification.substring(equalsPos + 1).trim()};
        }
    }


    private static MarkupSelectorItem.AttributeCondition.Operator parseAttributeOperator(final String operatorStr) {
        if (operatorStr == null) {
            return null;
        }
        if ("=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.EQUALS;
        }
        if ("!=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.NOT_EQUALS;
        }
        if ("^=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.STARTS_WITH;
        }
        if ("$=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.ENDS_WITH;
        }
        if ("*=".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.CONTAINS;
        }
        if ("!".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.NOT_EXISTS;
        }
        if ("".equals(operatorStr)) {
            return MarkupSelectorItem.AttributeCondition.Operator.EXISTS;
        }
        return null;
    }





    private MarkupSelectorItems() {
        super();
    }



}
