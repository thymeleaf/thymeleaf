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
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Le Roux Bernard
 * 
 * @since 1.0
 *
 */
public final class Strings {
    

    private final Locale locale;
    
    
    
    
    public Strings(final Locale locale) {
        super();
        this.locale = locale;
    }

    
    

    
    /**
     * <p>
     *   Performs a null-safe <tt>toString()</tt> operation.
     * </p>
     * 
     * @param target the object on which toString will be executed
     * @return the result of calling <tt>target.toString()</tt> if target is not null,
     *         <tt>null</tt> if target is null.
     * @since 2.0.12
     */
    public String toString(final Object target) {
        return StringUtils.toString(target);
    }
    

    /**
     * <p>
     *   Performs a null-safe <tt>toString()</tt> operation on each
     *   element of the array.
     * </p>
     * 
     * @param target the array of objects on which toString will be executed
     * @return for each element: the result of calling <tt>target.toString()</tt> 
     *         if target is not null, <tt>null</tt> if target is null.
     * @since 2.0.12
     */
    public String[] arrayToString(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toString(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     *   Performs a null-safe <tt>toString()</tt> operation on each
     *   element of the list.
     * </p>
     * 
     * @param target the list of objects on which toString will be executed
     * @return for each element: the result of calling <tt>target.toString()</tt> 
     *         if target is not null, <tt>null</tt> if target is null.
     * @since 2.0.12
     */
    public List<String> listToString(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(toString(element));
        }
        return result;
    }
    

    /**
     * <p>
     *   Performs a null-safe <tt>toString()</tt> operation on each
     *   element of the set.
     * </p>
     * 
     * @param target the set of objects on which toString will be executed
     * @return for each element: the result of calling <tt>target.toString()</tt> 
     *         if target is not null, <tt>null</tt> if target is null.
     * @since 2.0.12
     */
    public Set<String> setToString(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(toString(element));
        }
        return result;
    }
    


    
    
    public String abbreviate(final Object target, final int maxSize) {
        return StringUtils.abbreviate(target, maxSize);
    }
    
    public String[] arrayAbbreviate(final Object[] target, final int maxSize) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = abbreviate(target[i], maxSize);
        }
        return result;
    }
    
    public List<String> listAbbreviate(final List<?> target, final int maxSize) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(abbreviate(element, maxSize));
        }
        return result;
    }
    
    public Set<String> setAbbreviate(final Set<?> target, final int maxSize) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(abbreviate(element, maxSize));
        }
        return result;
    }
    

    
    
    /**
     * 
     * @since 2.0.16
     */
    public Boolean equals(final Object target, final String string) {
        return StringUtils.equals(target, string);
    }
    
    /**
     * 
     * @since 2.0.16
     */
    public Boolean equalsIgnoreCase(final Object target, final String string) {
        return StringUtils.equalsIgnoreCase(target, string);
    }
    
    
    public Boolean contains(final Object target, final String fragment) {
        return StringUtils.contains(target, fragment);
    }
    
    public Boolean[] arrayContains(final Object[] target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = contains(target[i], fragment);
        }
        return result;
    }
    
    public List<Boolean> listContains(final List<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object element : target) {
            result.add(contains(element, fragment));
        }
        return result;
    }
    
    public Set<Boolean> setContains(final Set<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>();
        for (final Object element : target) {
            result.add(contains(element, fragment));
        }
        return result;
    }

    
    
    
    
    
    public Boolean containsIgnoreCase(final Object target, final String fragment) {
        return StringUtils.containsIgnoreCase(target, fragment, this.locale);
    }
    
    public Boolean[] arrayContainsIgnoreCase(final Object[] target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = containsIgnoreCase(target[i], fragment);
        }
        return result;
    }
    
    public List<Boolean> listContainsIgnoreCase(final List<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object element : target) {
            result.add(containsIgnoreCase(element, fragment));
        }
        return result;
    }
    
    public Set<Boolean> setContainsIgnoreCase(final Set<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>();
        for (final Object element : target) {
            result.add(containsIgnoreCase(element, fragment));
        }
        return result;
    }
    

    

    
    
    public Boolean startsWith(final Object target, final String prefix) {
        return StringUtils.startsWith(target, prefix);
    }
    
    public Boolean[] arrayStartsWith(final Object[] target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = startsWith(target[i], prefix);
        }
        return result;
    }
    
    public List<Boolean> listStartsWith(final List<?> target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object element : target) {
            result.add(startsWith(element, prefix));
        }
        return result;
    }
    
    public Set<Boolean> setStartsWith(final Set<?> target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>();
        for (final Object element : target) {
            result.add(startsWith(element, prefix));
        }
        return result;
    }

    
    
    
    
    
    public Boolean endsWith(final Object target, final String suffix) {
        return StringUtils.endsWith(target, suffix);
    }
    
    public Boolean[] arrayEndsWith(final Object[] target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = endsWith(target[i], suffix);
        }
        return result;
    }
    
    public List<Boolean> listEndsWith(final List<?> target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object element : target) {
            result.add(endsWith(element, suffix));
        }
        return result;
    }
    
    public Set<Boolean> setEndsWith(final Set<?> target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>();
        for (final Object element : target) {
            result.add(endsWith(element, suffix));
        }
        return result;
    }
    

    
    
    
    
    public String substring(final Object target, final int start, final int end) {
        return StringUtils.substring(target, start, end);
    }
    
    public String[] arraySubstring(final Object[] target, final int start, final int end) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substring(target[i], start, end);
        }
        return result;
    }
    
    public List<String> listSubstring(final List<?> target, final int start, final int end) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(substring(element, start, end));
        }
        return result;
    }
    
    public Set<String> setSubstring(final Set<?> target, final int start, final int end) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(substring(element, start, end));
        }
        return result;
    }
    

    /**
     * <p>
     *  copy a part of target start beginIndex to the end of target. 
     *  If non-String object, toString() will be called.      
     * </p>
     * @param target  source of the copy.
     * @param start   index where the copy start.
     * 
     * @return part of target.
     *
     * @since 1.1.2
     * 
     */    
    public String substring(final Object target, final int start) {
        return StringUtils.substring(target, start);
    }
    
    /**
     * <p>
     *  copy a part of target start beginIndex to the end of target 
     *  for all the elements in the target array.     
     *  If non-String object, toString() will be called.      
     * </p>
     * @param target source of the copy.
     * @param start  index where the copy start.
     * 
     * @return part of target.
     *
     * @since 1.1.2
     * 
     */    
    public String[] arraySubstring(final Object[] target, final int start) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substring(target[i], start);
        }
        return result;
    }
    
    /**
     * <p>
     *  copy a part of target start beginIndex to the end of target 
     *  for  all the elements in the target list.
     *  If non-String object, toString() will be called.      
     * </p>
     * @param target   source of the copy.
     * @param start    index where the copy start.
     * 
     * @return part of target.
     *
     * @since 1.1.2
     * 
     */    
    public List<String> listSubstring(final List<?> target, final int start) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(substring(element, start));
        }
        return result;
    }
    
    /**
     * <p>
     *  copy a part of target start beginIndex to the end of target 
     *  for all the elements in the target set.
     *  If non-String object, toString() will be called.      
     * </p>
     * @param target source of the copy.
     * @param start  index where the copy start.
     * 
     * @return part of target.
     *
     * @since 1.1.2
     * 
     */    
    public Set<String> setSubstring(final Set<?> target, final int start) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(substring(element, start));
        }
        return result;
    }
    
    
    
    
    
    
    public String substringAfter(final Object target, final String substr) {
        return StringUtils.substringAfter(target, substr);
    }
    
    public String[] arraySubstringAfter(final Object[] target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substringAfter(target[i], substr);
        }
        return result;
    }
    
    public List<String> listSubstringAfter(final List<?> target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(substringAfter(element, substr));
        }
        return result;
    }
    
    public Set<String> setSubstringAfter(final Set<?> target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(substringAfter(element, substr));
        }
        return result;
    }
    
    
    
    

    
    public String substringBefore(final Object target, final String substr) {
        return StringUtils.substringBefore(target, substr);
    }
    
    public String[] arraySubstringBefore(final Object[] target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substringBefore(target[i], substr);
        }
        return result;
    }
    
    public List<String> listSubstringBefore(final List<?> target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(substringBefore(element, substr));
        }
        return result;
    }
    
    public Set<String> setSubstringBefore(final Set<?> target, final String substr) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(substringBefore(element, substr));
        }
        return result;
    }

    
    
    
    
    
    public String prepend(final Object target, final String prefix) {
        return StringUtils.prepend(target, prefix);
    }
    
    public String[] arrayPrepend(final Object[] target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = prepend(target[i], prefix);
        }
        return result;
    }
    
    public List<String> listPrepend(final List<?> target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(prepend(element, prefix));
        }
        return result;
    }
    
    public Set<String> setPrepend(final Set<?> target, final String prefix) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(prepend(element, prefix));
        }
        return result;
    }


    

    
    
    
    
    public String append(final Object target, final String suffix) {
        return StringUtils.append(target, suffix);
    }
    
    /**
     * 
     * @since 2.0.16
     */
    public String concat(final Object target, final String nullValue, final String ... strings) {
        return StringUtils.concat(target, nullValue, strings);
    }
        
    public String[] arrayAppend(final Object[] target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = append(target[i], suffix);
        }
        return result;
    }
    
    public List<String> listAppend(final List<?> target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(append(element, suffix));
        }
        return result;
    }
    
    public Set<String> setAppend(final Set<?> target, final String suffix) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(append(element, suffix));
        }
        return result;
    }
    
    
    
    
    
    
    public Integer indexOf(final Object target, final String fragment) {
        return StringUtils.indexOf(target, fragment);
    }
    
    public Integer[] arrayIndexOf(final Object[] target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = indexOf(target[i], fragment);
        }
        return result;
    }
    
    public List<Integer> listIndexOf(final List<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final List<Integer> result = new ArrayList<Integer>();
        for (final Object element : target) {
            result.add(indexOf(element, fragment));
        }
        return result;
    }
    
    public Set<Integer> setIndexOf(final Set<?> target, final String fragment) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Integer> result = new LinkedHashSet<Integer>();
        for (final Object element : target) {
            result.add(indexOf(element, fragment));
        }
        return result;
    }
    

    


    
    
    public Boolean isEmpty(final Object target) {
        return StringUtils.isEmpty(target);
    }
    
    public Boolean[] arrayIsEmpty(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        final Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isEmpty(target[i]);
        }
        return result;
    }
    
    public List<Boolean> listIsEmpty(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object element : target) {
            result.add(isEmpty(element));
        }
        return result;
    }
    
    public Set<Boolean> setIsEmpty(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Boolean> result = new LinkedHashSet<Boolean>();
        for (final Object element : target) {
            result.add(isEmpty(element));
        }
        return result;
    }
    
    
    
    

    
    
    public String arrayJoin(final Object[] stringArray, final String separator) {
        return StringUtils.join(stringArray, separator);
    }
    
    public String listJoin(final List<?> stringIter, final String separator) {
        return StringUtils.join(stringIter, separator);
    }
    
    public String setJoin(final Set<?> stringIter, final String separator) {
        return StringUtils.join(stringIter, separator);
    }
    
    
    
    
    
    public String[] arraySplit(final Object target, final String separator) {
        return StringUtils.split(target, separator);
    }
    
    public List<String> listSplit(final Object target, final String separator) {
        return new ArrayList<String>(java.util.Arrays.asList(StringUtils.split(target, separator)));
    }
    
    public Set<String> setSplit(final Object target, final String separator) {
        return new LinkedHashSet<String>(java.util.Arrays.asList(StringUtils.split(target, separator)));
    }

    

    
    
    public Integer length(final Object target) {
        return StringUtils.length(target);
    }
    
    public Integer[] arrayLength(final Object[] target) {
        Validate.notNull(target, "Target cannot be null");
        final Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = length(target[i]);
        }
        return result;
    }
    
    public List<Integer> listLength(final List<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final List<Integer> result = new ArrayList<Integer>();
        for (final Object element : target) {
            result.add(length(element));
        }
        return result;
    }
    
    public Set<Integer> setLength(final Set<?> target) {
        Validate.notNull(target, "Target cannot be null");
        final Set<Integer> result = new LinkedHashSet<Integer>();
        for (final Object element : target) {
            result.add(length(element));
        }
        return result;
    }
    
    

    
    
    
    
    public String replace(final Object target, final String before, final String after) {
        return StringUtils.replace(target, before, after);
    }
    
    public String[] arrayReplace(final Object[] target, final String before, final String after) {
        Validate.notNull(target, "Target cannot be null");
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = replace(target[i], before, after);
        }
        return result;
    }
    
    public List<String> listReplace(final List<?> target, final String before, final String after) {
        Validate.notNull(target, "Target cannot be null");
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(replace(element, before, after));
        }
        return result;
    }
    
    public Set<String> setReplace(final Set<?> target, final String before, final String after) {
        Validate.notNull(target, "Target cannot be null");
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(replace(element, before, after));
        }
        return result;
    }
    

    
    

    
    public String toUpperCase(final Object target) {
        return StringUtils.toUpperCase(target, this.locale);
    }
    
    public String[] arrayToUpperCase(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toUpperCase(target[i]);
        }
        return result;
    }
    
    public List<String> listToUpperCase(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(toUpperCase(element));
        }
        return result;
    }
    
    public Set<String> setToUpperCase(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(toUpperCase(element));
        }
        return result;
    }

    
    

    
    
    public String toLowerCase(final Object target) {
        return StringUtils.toLowerCase(target, this.locale);
    }
    
    public String[] arrayToLowerCase(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toLowerCase(target[i]);
        }
        return result;
    }
    
    public List<String> listToLowerCase(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(toLowerCase(element));
        }
        return result;
    }
    
    public Set<String> setToLowerCase(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(toLowerCase(element));
        }
        return result;
    }
    

    
    
    
    public String trim(final Object target) {
        return StringUtils.trim(target);
    }
    
    public String[] arrayTrim(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = trim(target[i]);
        }
        return result;
    }
    
    public List<String> listTrim(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(trim(element));
        }
        return result;
    }
    
    public Set<String> setTrim(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(trim(element));
        }
        return result;
    }
    
    
    /**
     * <p>
     *   Convert the first letter of target to uppercase (title-case, in fact).
     * </p>
     * 
     * @param target the String to be capitalized. 
     * If non-String object, toString() will be called. 
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public String capitalize(final Object target) {
        return StringUtils.capitalize(target);
    }
    
    
    /**
     * <p>
     *   Convert the first letter into uppercase (title-case, in fact) for
     *   all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @return a String[] with the result of capitalizing 
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public String[] arrayCapitalize(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalize(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     *   Convert the first letter into uppercase (title-case, in fact) for
     *   all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @return a List with the result of capitalizing 
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public List<String> listCapitalize(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(capitalize(element));
        }
        return result;
    }

    
    /**
     * <p>
     *   Convert the first letter into uppercase (title-case, in fact) for
     *   all the elements in the target set.
     * </p>
     * 
     * @param target the set of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @return a Set with the result of capitalizing each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public Set<String> setCapitalize(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(capitalize(element));
        }
        return result;
    }
    
    /**
     * <p>
     *   Convert the first letter of target to lowercase.
     * </p>
     * 
     * @param target the String to be uncapitalized.
     * If non-String object, toString() will be called. 
     * 
     * @return String the result of uncapitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public String unCapitalize(final Object target) {
        return StringUtils.unCapitalize(target);
    }
    
    
    /**
     * <p>
     *   Convert the first letter into lowercase for
     *   all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be uncapitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of uncapitalizing 
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public String[] arrayUnCapitalize(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unCapitalize(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     *   Convert the first letter into lowercase for
     *   all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be uncapitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of uncapitalizing
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public List<String> listUnCapitalize(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(unCapitalize(element));
        }
        return result;
    }

    
    /**
     * <p>
     *   Convert the first letter into lowercase for
     *   all the elements in the target set.
     * </p>
     * 
     * @param target the set of Strings to be uncapitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of uncapitalizing
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public Set<String> setUnCapitalize(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(unCapitalize(element));
        }
        return result;
    }
    

    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact).
     * The default delimiter characters between the words are 
     * the whitespace characters
     * (see Characters.IsWhiteSpace method in the Java documentation).
     * </p>
     * 
     * @param target the String to be capitalized.
     * If non-String object, toString() will be called. 
     * 
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public String capitalizeWords(final Object target) {
        return StringUtils.capitalizeWords(target);
    }
    
    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) for 
     * all the elements in the target array.
     * The default delimiter characters between the words are
     * the whitespace characters 
     * (see Characters.IsWhiteSpace method in the Java documentation).
     * </p>
     * 
     * @param target the array of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of capitalizing
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public String[] arrayCapitalizeWords(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalizeWords(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) for
     * all the elements in the target list.
     * The default delimiter characters between the words are 
     * the whitespace characters 
     * (see Characters.IsWhiteSpace method in the Java documentation).
     * </p>
     * 
     * @param target the list of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of capitalizing
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public List<String> listCapitalizeWords(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(capitalizeWords(element));
        }
        return result;
    }

    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) 
     * for all the elements in the target set.
     * The default delimiter characters between the words are 
     * the whitespace characters 
     * (see Characters.IsWhiteSpace method in the Java documentation).
     * </p>
     * 
     * @param target the set of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of capitalizing each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public Set<String> setCapitalizeWords(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(capitalizeWords(element));
        }
        return result;
    }

    
    /**
     * <p>
     * Convert the first letter of each words of target to uppercase
     * (title-case, in fact), using the specified delimiter chars for determining
     * word ends/starts.
     * </p>
     * 
     * @param target the String to be capitalized.
     * If non-String object, toString() will be called. 
     * @param delimiters the delimiters of the words.
     * If non-String object, toString() will be called.
     * 
     * @return String the result of capitalizing the target.
     *
     * @since 1.1.2
     * 
     */
    public String capitalizeWords(final Object target, 
            final Object delimiters) {
        
        return StringUtils.capitalizeWords(target, delimiters);
    }
    
    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) for
     * all the elements in the target array.
     * The specified delimiter chars will be used for determining
     * word ends/starts.
     * </p>
     * 
     * @param target the array of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @param delimiters the delimiters of the words.
     * If non-String object, toString() will be called.
     * 
     * @return a String[] with the result of capitalizing
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public String[] arrayCapitalizeWords(final Object[] target, 
            final Object delimiters) {
        
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalizeWords(target[i], delimiters);
        }
        return result;
    }

    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) for
     * all the elements in the target list.
     * The specified delimiter chars will be used for determining
     * word ends/starts.
     * </p>
     * 
     * @param target the list of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @param delimiters the delimiters of the words.
     * If non-String object, toString() will be called.
     * 
     * @return a List with the result of capitalizing 
     * each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public List<String> listCapitalizeWords(final List<?> target, 
            final Object delimiters) {
        
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(capitalizeWords(element, delimiters));
        }
        return result;
    }

    
    /**
     * <p>
     * Convert the first letter of each words of target 
     * to uppercase (title-case, in fact) for
     * all the elements in the target set.
     * The specified delimiter chars will be used for determining
     * word ends/starts.
     * </p>
     * 
     * @param target the set of Strings to be capitalized.
     * If non-String objects, toString() will be called. 
     * @param delimiters the delimiters of the words.
     * If non-String object, toString()
     * 
     * @return a Set with the result of capitalizing each element of the target.
     *
     * @since 1.1.2
     * 
     */
    public Set<String> setCapitalizeWords(final Set<?> target, 
            final Object delimiters) {
        
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(capitalizeWords(element, delimiters));
        }
        return result;
    }
    
    
    

    /**
     * <p>
     *   XML-escapes the specified text.
     * </p>
     * 
     * @param target the text to be escaped
     * @return the escaped text.
     * 
     * @since 2.0.9
     */
    public String escapeXml(final Object target) {
        return StringUtils.escapeXml(target);
    }
    
    
    /**
     * <p>
     * XML-escapes all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of each
     * each element of the target.
     *
     * @since 2.0.9
     * 
     */
    public String[] arrayEscapeXml(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeXml(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * XML-escapes all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of each
     * each element of the target.
     *
     * @since 2.0.9
     * 
     */
    public List<String> listEscapeXml(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(escapeXml(element));
        }
        return result;
    }

    
    /**
     * <p>
     * XML-escapes all the elements in the target set.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of each
     * each element of the target.
     *
     * @since 2.0.9
     * 
     */
    public Set<String> setEscapeXml(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(escapeXml(element));
        }
        return result;
    }
    
    

    
    
    
    
    
    

    /**
     * <p>
     *   JavaScript-escapes the specified text.
     * </p>
     * 
     * @param target the text to be escaped
     * @return the escaped text.
     * 
     * @since 2.0.11
     */
    public String escapeJavaScript(final Object target) {
        return StringUtils.escapeJavaScript(target);
    }
    
    
    /**
     * <p>
     * JavaScript-escapes all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public String[] arrayEscapeJavaScript(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeJavaScript(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * JavaScript-escapes all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public List<String> listEscapeJavaScript(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(escapeJavaScript(element));
        }
        return result;
    }

    
    /**
     * <p>
     * JavaScript-escapes all the elements in the target set.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public Set<String> setEscapeJavaScript(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(escapeJavaScript(element));
        }
        return result;
    }
    

    


    
    
    

    /**
     * <p>
     *   JavaScript-unescapes the specified text.
     * </p>
     * 
     * @param target the text to be unescaped
     * @return the unescaped text.
     * 
     * @since 2.0.11
     */
    public String unescapeJavaScript(final Object target) {
        return StringUtils.unescapeJavaScript(target);
    }
    
    
    /**
     * <p>
     * JavaScript-unescapes all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public String[] arrayUnescapeJavaScript(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unescapeJavaScript(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * JavaScript-unescapes all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public List<String> listUnescapeJavaScript(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(unescapeJavaScript(element));
        }
        return result;
    }

    
    /**
     * <p>
     * JavaScript-unescapes all the elements in the target set.
     * </p>
     * 
     * @param target the list of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public Set<String> setUnescapeJavaScript(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(unescapeJavaScript(element));
        }
        return result;
    }
    

    

    
    
    
    

    /**
     * <p>
     *   Java-escapes the specified text.
     * </p>
     * 
     * @param target the text to be escaped
     * @return the escaped text.
     * 
     * @since 2.0.11
     */
    public String escapeJava(final Object target) {
        return StringUtils.escapeJava(target);
    }
    
    
    /**
     * <p>
     * Java-escapes all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public String[] arrayEscapeJava(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeJava(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * Java-escapes all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public List<String> listEscapeJava(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(escapeJava(element));
        }
        return result;
    }

    
    /**
     * <p>
     * Java-escapes all the elements in the target set.
     * </p>
     * 
     * @param target the list of Strings to be escaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public Set<String> setEscapeJava(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(escapeJava(element));
        }
        return result;
    }
    
    

    
    
    
    
    
    
    
    

    /**
     * <p>
     *   Java-unescapes the specified text.
     * </p>
     * 
     * @param target the text to be unescaped
     * @return the unescaped text.
     * 
     * @since 2.0.11
     */
    public String unescapeJava(final Object target) {
        return StringUtils.unescapeJava(target);
    }
    
    
    /**
     * <p>
     * Java-unescapes all the elements in the target array.
     * </p>
     * 
     * @param target the array of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a String[] with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public String[] arrayUnescapeJava(final Object[] target) {
        if (target == null) {
            return null;
        }
        final String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unescapeJava(target[i]);
        }
        return result;
    }

    
    /**
     * <p>
     * Java-unescapes all the elements in the target list.
     * </p>
     * 
     * @param target the list of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a List with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public List<String> listUnescapeJava(final List<?> target) {
        if (target == null) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (final Object element : target) {
            result.add(unescapeJava(element));
        }
        return result;
    }

    
    /**
     * <p>
     * Java-unescapes all the elements in the target set.
     * </p>
     * 
     * @param target the list of Strings to be unescaped.
     * If non-String objects, toString() will be called. 
     * 
     * @return a Set with the result of each
     * each element of the target.
     *
     * @since 2.0.11
     * 
     */
    public Set<String> setUnescapeJava(final Set<?> target) {
        if (target == null) {
            return null;
        }
        final Set<String> result = new LinkedHashSet<String>();
        for (final Object element : target) {
            result.add(unescapeJava(element));
        }
        return result;
    }
    
    
    
    
}
