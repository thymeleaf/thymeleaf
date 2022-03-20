/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.util;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;

/**
 * <p>
 *   This class mirrors the behaviour of the protected class
 *   {@code org.springframework.web.servlet.tags.form.SelectedValueComparator}, needed in order to compare
 *   and match form values in a way compatible to other Spring view-layer technologies.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public final class SpringSelectedValueComparator {


    /*
     * NOTE This code is based on org.springframework.web.servlet.tags.form.SelectedValueComparator as of Spring 5.0.0
     *      Original license is Apache License 2.0, which is the same as the license for this file.
     *      Original copyright notice is "Copyright 2002-2016 the original author or authors".
     *      Original authors are Rob Harrop and Juergen Hoeller.
     *
     * NOTE The code in this class has been adapted to use Thymeleaf's own BindStatus interfaces.
     */


    public static boolean isSelected(final IThymeleafBindStatus bindStatus, final Object candidateValue) {

        if (bindStatus == null) {
            return (candidateValue == null);
        }

        // Check obvious equality matches with the candidate first,
        // both with the rendered value and with the original value.
        Object boundValue = bindStatus.getValue();
        if (ObjectUtils.nullSafeEquals(boundValue, candidateValue)) {
            return true;
        }
        Object actualValue = bindStatus.getActualValue();
        if (actualValue != null && actualValue != boundValue &&
                ObjectUtils.nullSafeEquals(actualValue, candidateValue)) {
            return true;
        }
        if (actualValue != null) {
            boundValue = actualValue;
        } else if (boundValue == null) {
            return false;
        }

        // Non-null value but no obvious equality with the candidate value:
        // go into more exhaustive comparisons.
        boolean selected = false;
        if (boundValue.getClass().isArray()) {
            selected = collectionCompare(CollectionUtils.arrayToList(boundValue), candidateValue, bindStatus);
        } else if (boundValue instanceof Collection) {
            selected = collectionCompare((Collection<?>) boundValue, candidateValue, bindStatus);
        } else if (boundValue instanceof Map) {
            selected = mapCompare((Map<?, ?>) boundValue, candidateValue, bindStatus);
        }
        if (!selected) {
            selected = exhaustiveCompare(boundValue, candidateValue, bindStatus.getEditor(), null);
        }
        return selected;

    }


    private static boolean collectionCompare(
            final Collection<?> boundCollection, final Object candidateValue, final IThymeleafBindStatus bindStatus) {

        try {
            if (boundCollection.contains(candidateValue)) {
                return true;
            }
        } catch (ClassCastException ex) {
            // Probably from a TreeSet - ignore.
        }
        return exhaustiveCollectionCompare(boundCollection, candidateValue, bindStatus);

    }


    private static boolean mapCompare(
            final Map<?, ?> boundMap, final Object candidateValue, final IThymeleafBindStatus bindStatus) {

        try {
            if (boundMap.containsKey(candidateValue)) {
                return true;
            }
        } catch (ClassCastException ex) {
            // Probably from a TreeMap - ignore.
        }
        return exhaustiveCollectionCompare(boundMap.keySet(), candidateValue, bindStatus);

    }


    private static boolean exhaustiveCollectionCompare(
            final Collection<?> collection, final Object candidateValue, final IThymeleafBindStatus bindStatus) {

        final Map<PropertyEditor, Object> convertedValueCache = new HashMap<>(1);
        PropertyEditor editor = null;
        boolean candidateIsString = (candidateValue instanceof String);
        if (!candidateIsString) {
            editor = bindStatus.findEditor(candidateValue.getClass());
        }
        for (Object element : collection) {
            if (editor == null && element != null && candidateIsString) {
                editor = bindStatus.findEditor(element.getClass());
            }
            if (exhaustiveCompare(element, candidateValue, editor, convertedValueCache)) {
                return true;
            }
        }
        return false;

    }


    private static boolean exhaustiveCompare(
            final Object boundValue, final Object candidate,
            final PropertyEditor editor, final Map<PropertyEditor, Object> convertedValueCache) {

        final String candidateDisplayString = SpringValueFormatter.getDisplayString(candidate, editor, false);
        if (boundValue != null && boundValue.getClass().isEnum()) {
            final Enum<?> boundEnum = (Enum<?>) boundValue;
            final String enumCodeAsString = ObjectUtils.getDisplayString(boundEnum.name());
            if (enumCodeAsString.equals(candidateDisplayString)) {
                return true;
            }
            final String enumLabelAsString = ObjectUtils.getDisplayString(boundEnum.toString());
            if (enumLabelAsString.equals(candidateDisplayString)) {
                return true;
            }
        } else if (ObjectUtils.getDisplayString(boundValue).equals(candidateDisplayString)) {
            return true;
        } else if (editor != null && candidate instanceof String) {
            // Try PE-based comparison (PE should *not* be allowed to escape creating thread)
            final String candidateAsString = (String) candidate;
            final Object candidateAsValue;
            if (convertedValueCache != null && convertedValueCache.containsKey(editor)) {
                candidateAsValue = convertedValueCache.get(editor);
            } else {
                editor.setAsText(candidateAsString);
                candidateAsValue = editor.getValue();
                if (convertedValueCache != null) {
                    convertedValueCache.put(editor, candidateAsValue);
                }
            }
            if (ObjectUtils.nullSafeEquals(boundValue, candidateAsValue)) {
                return true;
            }
        }
        return false;

    }


    private SpringSelectedValueComparator() {
        super();
    }


}
