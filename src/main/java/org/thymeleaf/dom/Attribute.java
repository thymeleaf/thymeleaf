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

import org.thymeleaf.util.PrefixUtils;
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
public final class Attribute {

    /**
     * <p>
     *   Prefix for namespace-definition attributes (like <tt>xmlns:th</tt>).
     * </p>
     */
    public static final String XMLNS_PREFIX = "xmlns";
    
    private final String originalName;
    private final String normalizedName;
    private final String normalizedPrefix;
    private final String unprefixedNormalizedName;
    private final boolean hasPrefix;
    
    private final boolean xmlnsAttribute;
    private final String xmlnsPrefix;
    
    private final String value;


    public Attribute(final String name, final String value) {
        
        super();
        
        Validate.notNull(name, "Attribute name cannot be null");
        
        this.originalName = name;
        this.normalizedName = Node.normalizeName(name);
        this.normalizedPrefix = PrefixUtils.getPrefix(this.normalizedName);
        this.unprefixedNormalizedName = PrefixUtils.getUnprefixed(this.normalizedName);
        this.hasPrefix = this.normalizedPrefix != null;

        this.xmlnsAttribute = (this.normalizedPrefix != null && this.normalizedPrefix.equals(XMLNS_PREFIX));
        this.xmlnsPrefix = (this.xmlnsAttribute? this.unprefixedNormalizedName : null);
        
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
     *   means of the {@link Node#normalizeName(String)} method so that the engine
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
     *   Prefixes are normalized in the same way as element names, using the
     *   {@link Node#normalizeName(String)} method.
     * </p>
     * 
     * @return the normalized prefix.
     */
    public String getNormalizedPrefix() {
        return this.normalizedPrefix;
    }


    /**
     * <p>
     *   Returns the normalized version of the attribute name, without its prefix
     *   (if it has one).
     * </p>
     * 
     * @return the unprefixed normalized name.
     */
    public String getUnprefixedNormalizedName() {
        return this.unprefixedNormalizedName;
    }


    /**
     * <p>
     *   Returns whether the attribute name has a prefix or not.
     * </p>
     * 
     * @return true if the attribute name has a prefix, false if not.
     */
    public boolean hasPrefix() {
        return this.hasPrefix;
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
     */
    public boolean isXmlnsAttribute() {
        return this.xmlnsAttribute;
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
     */
    public String getXmlnsPrefix() {
        return this.xmlnsPrefix;
    }

}
