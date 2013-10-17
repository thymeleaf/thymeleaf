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

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Models an element attribute.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Attribute implements Serializable {

    private static final long serialVersionUID = 7133984802585560958L;

    // @since 2.1.0
    private static final ConcurrentHashMap<String,ConcurrentHashMap<String,String[]>> prefixedAttributeNamesByPrefix =
            new ConcurrentHashMap<String, ConcurrentHashMap<String,String[]>>(3);

    // @since 2.1.0
    private static final ConcurrentHashMap<String,String> prefixesByAttributeName = new ConcurrentHashMap<String,String>(100);


    private final String originalName;
    private final String normalizedName;

    private final boolean onlyName;
    private final String value;


    public Attribute(final String name, final boolean onlyName, final String value) {
        
        super();
        
        Validate.notNull(name, "Attribute name cannot be null");
        
        this.originalName = name;
        this.normalizedName = normalizeAttributeName(name);

        // The "onlyName" flag determines whether the attribute was specified
        // only with its name (without an equals sign or a value).
        // For "this.onlyName" to be honored by template writers, value should be null. 
        // If not, onlyName is ignored.
        this.onlyName = onlyName;
        
        this.value = value;
        
    }


    /*
     * This private constructor is only used from the "cloneForValue(...)" method, so that there is no need to
     * recompute prefix, etc when an attribute is simply changed value.
     */
    private Attribute(final String originalName, final String normalizedName,
                      final boolean onlyName, final String value) {
        super();
        this.originalName = originalName;
        this.normalizedName = normalizedName;
        this.onlyName = onlyName;
        this.value = value;
    }



    /**
     * <p>
     *   Returns the original name of the attribute. Given the fact that the engine
     *   works using the normalized versions of attribute names (in order to be case-insensitive),
     *   it is necessary to make a distinction between the 'original' name of an attribute
     *   (as it is written in the template file) and its 'normalized' name. 
     * </p>
     * <p>
     *   If the attribute name has a prefix, this 'original name' includes such prefix.
     * </p>
     * 
     * @return the original name of the attribute.
     */
    public String getOriginalName() {
        return this.originalName;
    }
    

    /**
     * <p>
     *   Returns the normalized name of the attribute. Attribute names are normalized by
     *   means of the {@link #normalizeAttributeName(String)} method so that the engine
     *   can work in a case-insensitive way.
     * </p>
     * <p>
     *   If the attribute name has a prefix, this 'normalized name' includes such prefix.
     * </p>
     * 
     * @return the normalized name of the attribute.
     */
    public String getNormalizedName() {
        return this.normalizedName;
    }
    
    
    /**
     * <p>
     *   Returns the normalized prefix of this attribute (part of its name), if it exists,
     *   or null if the element is unprefixed.
     * </p>
     * <p>
     *   Prefixes are normalized in the same way as attribute names.
     * </p>
     * 
     * @return the normalized prefix.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public String getNormalizedPrefix() {
        return getPrefixFromAttributeName(this.normalizedName);
    }


    /**
     * <p>
     *   Returns the normalized version of the attribute name, without its prefix
     *   (if it has one).
     * </p>
     * 
     * @return the unprefixed normalized name.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public String getUnprefixedNormalizedName() {
        return getUnprefixedAttributeName(this.normalizedName);
    }


    /**
     * <p>
     *   Returns whether the attribute name has a prefix or not.
     * </p>
     * 
     * @return true if the attribute name has a prefix, false if not.
     * @deprecated Deprecated in 2.1.0. There is no actual usage of this method
     */
    @Deprecated
    public boolean hasPrefix() {
        final int colonPos = this.normalizedName.indexOf(':');
        return colonPos != -1;
    }

    
    
    /**
     * <p>
     *   Determines whether this attribute is to be specified only by its
     *   name (no equals sign, no value).
     * </p>
     * <p>
     *   If this is true, but {@link #getValue()} is not null, this flag is
     *   ignored.
     * </p>
     * <p>
     *   Parsers and/or template writers are not required to honor this flag,
     *   and they can choose to render attributes with their values always
     *   even if these are empty and/or null and this flag is set.
     * </p>
     * 
     * @return true if attribute should be represented only by its name.
     * @since 2.0.14
     */
    public boolean isOnlyName() {
        return this.onlyName;
    }
    


    /**
     * <p>
     *   Returns the value of the attribute.
     * </p>
     * 
     * @return the value of the attribute.
     */
    public String getValue() {
        return this.value;
    }
    

    /**
     * <p>
     *   Returns whether this attribute is a namespace
     *   declaration (<tt>xmlns:*</tt>) or not.
     * </p>
     * 
     * @return true if the attribute is a namespace declaration, false if not.
     * @deprecated Deprecated in 2.1.0. The only usage of this method is at the moment of writing the template. No
     *             need to be always pre-computed.
     */
    @Deprecated
    public boolean isXmlnsAttribute() {
        return this.normalizedName.startsWith("xmlns:");
    }
    

    /**
     * <p>
     *   If this is a namespace declaration attribute (like "<tt>xmlns:x</tt>"),
     *   this method returns the prefix (the "<tt>x</tt>"). 
     * </p>
     * <p>
     *   If this is not a namespace declaration attribute, this method will return null.
     * </p>
     * 
     * @return the namespace prefix, if this is a namespace declaration attribute.
     * @deprecated Deprecated in 2.1.0. The only usage of this method is at the moment of writing the template. No
     *             need to be always pre-computed.
     */
    @Deprecated
    public String getXmlnsPrefix() {
        if (!isXmlnsAttribute()) {
            return null;
        }
        return this.normalizedName.substring("xmlns:".length());
    }



    Attribute cloneForValue(final boolean onlyName, final String value) {
        return new Attribute(
                this.originalName, this.normalizedName,
                onlyName, value);
    }





    /**
     * <p>
     *   Normalizes an attribute name by converting it
     *   to lower-case. Attributes are processed as
     *   case-insensitive, and this method allows normalizing their
     *   names before processing.
     * </p>
     *
     * @param name the name to be normalized.
     * @return the normalized name.
     * @since 2.1.0
     */
    public static String normalizeAttributeName(final String name) {
        if (name == null) {
            return null;
        }
        return name.toLowerCase();
    }



    /**
     * <p>
     *   Applies a prefix (a dialect prefix) to the specified name in order to obtain a complete
     *   valid attribute name.
     * </p>
     * <p>
     *   The result looks like: <tt>"${prefix}:${name}"</tt>.
     * </p>
     *
     * @param name the name to be prefixed.
     * @param dialectPrefix the prefix to be applied.
     * @return the prefixed name.
     * @since 2.1.0
     */
    public static String[] applyPrefixToAttributeName(final String name, final String dialectPrefix) {

        if (name == null) {
            return null;
        }

        // ConcurrentHashMaps dont allow null keys, so we will use the empty string as equivalent
        final String prefix = (dialectPrefix == null? "" : dialectPrefix);

        ConcurrentHashMap<String,String[]> prefixedAttributeNamesForPrefix = prefixedAttributeNamesByPrefix.get(prefix);
        if (prefixedAttributeNamesForPrefix == null) {
            prefixedAttributeNamesForPrefix = new ConcurrentHashMap<String, String[]>(100);
            prefixedAttributeNamesByPrefix.put(prefix, prefixedAttributeNamesForPrefix);
        }

        String[] prefixedAttributeNames = prefixedAttributeNamesForPrefix.get(name);
        if (prefixedAttributeNames != null) {
            // cache hit!
            return prefixedAttributeNames;
        }

        if (StringUtils.isEmptyOrWhitespace(prefix)) {
            prefixedAttributeNames = new String[] { name };
        } else {
            prefixedAttributeNames = new String[] { prefix + ':' + name, "data-" + prefix + '-' + name };
        }

        prefixedAttributeNamesForPrefix.put(name, prefixedAttributeNames);

        return prefixedAttributeNames;

    }



    /**
     * <p>
     *   Returns the equivalent, un-prefixed name of an attribute from its complete (prefixed, if applies) version.
     * </p>
     * <p>
     *   It supports both namespace prefix style (using <tt>:</tt>) and HTML5 custom attribute style
     *   ('<tt>data-{prefix}-{name}</tt>'). Examples: src -> src, th:href -> href, data-th-class -> class.
     * </p>
     *
     * @param attributeName the complete (prefixed, if applies) version of an attribute name.
     * @return the unprefixed version of the specified attribute name.
     * @since 2.1.0
     */
    public static String getUnprefixedAttributeName(final String attributeName) {
        if (attributeName == null) {
            return null;
        }
        final int colonPos = attributeName.indexOf(':');
        if (colonPos != -1) {
            return attributeName.substring(colonPos + 1);
        }
        if (attributeName.startsWith("data-")) {
            final int dashPos = attributeName.substring(5).indexOf('-');
            if (dashPos != -1) {
                return attributeName.substring(dashPos + 6); // 5 are because of the 'data-' prefix
            }
        }
        return attributeName;
    }




    /**
     * <p>
     *   Returns the prefix being applied to an attribute.
     * </p>
     *
     * @param attributeName the complete (prefixed, if applies) version of an attribute name.
     * @return the prefix being applied to the name, or null if the attribute has no prefix.
     * @since 2.1.0
     */
    public static String getPrefixFromAttributeName(final String attributeName) {

        if (attributeName == null) {
            return null;
        }

        String prefix = prefixesByAttributeName.get(attributeName);
        if (prefix != null) {
            // cache hit!
            if (prefix.length() == 0) {
                // ConcurrentHashMap objects do not allow null values. So we use "" as a substitute.
                return null;
            }
            return prefix;
        }

        final int colonPos = attributeName.indexOf(':');
        if (colonPos != -1) {
            prefix = attributeName.substring(0, colonPos);
        } else if (attributeName.startsWith("data-")) {
            final int dashPos = attributeName.substring(5).indexOf('-');
            if (dashPos != -1) {
                prefix = attributeName.substring(5, dashPos + 5); // 5 are because of the 'data-' prefix
            }
        }

        if (prefix == null) {
            // ConcurrentHashMap objects do not allow null values. So we use "" as a substitute.
            prefix = "";
        }

        prefixesByAttributeName.put(attributeName, prefix);

        if (prefix.length() == 0) {
            return null;
        }
        return prefix;

    }


}
