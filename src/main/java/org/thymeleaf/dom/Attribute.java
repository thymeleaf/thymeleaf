/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Attribute {

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
    

    
    public String getOriginalName() {
        return this.originalName;
    }
    
    public String getNormalizedName() {
        return this.normalizedName;
    }
    
    public String getNormalizedPrefix() {
        return this.normalizedPrefix;
    }


    public String getUnprefixedNormalizedName() {
        return this.unprefixedNormalizedName;
    }


    public boolean hasPrefix() {
        return this.hasPrefix;
    }


    public String getValue() {
        return this.value;
    }
    
    
    public boolean isXmlnsAttribute() {
        return this.xmlnsAttribute;
    }
    
    
    public String getXmlnsPrefix() {
        return this.xmlnsPrefix;
    }

}
