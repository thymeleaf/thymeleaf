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
package org.thymeleaf.model;

import java.util.Map;

import org.thymeleaf.engine.AttributeName;

/**
 * <p>
 *   Event interface defining a processable element tag, i.e an element tag on which processors can be
 *   applied (open, standalone).
 * </p>
 * <p>
 *   Note that any implementations of this interface should be <strong>immutable</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IProcessableElementTag extends IElementTag {

    /**
     * <p>
     *   Returns an array with all the attributes contained in the tag.
     * </p>
     * <p>
     *   This array is a defensive copy of the original data kept at the tag, and might be null if there
     *   are no attributes at the tag.
     * </p>
     *
     * @return the array of attributes.
     */
    public IAttribute[] getAllAttributes();

    /**
     * <p>
     *   Returns a Map containing all the attribute names+values in the tag.
     * </p>
     * <p>
     *   This array is a defensive copy of the original data kept at the tag, and might be null if there
     *   are no attributes at the tag.
     * </p>
     *
     * @return the map of attributes.
     */
    public Map<String,String> getAttributeMap();


    /**
     * <p>
     *   Returns whether an attribute is exists at the tag or not.
     * </p>
     *
     * @param completeName the complete name of the attribute that is being queried.
     * @return true if the attribute exists, false if not.
     */
    public boolean hasAttribute(final String completeName);

    /**
     * <p>
     *   Returns whether an attribute is exists at the tag or not.
     * </p>
     *
     * @param prefix the prefix of the attribute that is being queried (might be null).
     * @param name the name of tha attribute that is being queried.
     * @return true if the attribute exists, false if not.
     */
    public boolean hasAttribute(final String prefix, final String name);

    /**
     * <p>
     *   Returns whether an attribute is exists at the tag or not.
     * </p>
     *
     * @param attributeName the name of the attribute that is being queried.
     * @return true if the attribute exists, false if not.
     */
    public boolean hasAttribute(final AttributeName attributeName);


    /**
     * <p>
     *   Returns the object modelling a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param completeName the complete name of the attribute that is being queried.
     * @return the {@link IAttribute} for the queried attribute, or null if it does not exist.
     */
    public IAttribute getAttribute(final String completeName);

    /**
     * <p>
     *   Returns the object modelling a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param prefix the prefix of the attribute that is being queried (might be null).
     * @param name the name of tha attribute that is being queried.
     * @return the {@link IAttribute} for the queried attribute, or null if it does not exist.
     */
    public IAttribute getAttribute(final String prefix, final String name);

    /**
     * <p>
     *   Returns the object modelling a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param attributeName the name of the attribute that is being queried.
     * @return the {@link IAttribute} for the queried attribute, or null if it does not exist.
     */
    public IAttribute getAttribute(final AttributeName attributeName);


    /**
     * <p>
     *   Returns the value of a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param completeName the complete name of the attribute that is being queried.
     * @return the value of the queried attribute, or null if it does not exist.
     */
    public String getAttributeValue(final String completeName);

    /**
     * <p>
     *   Returns the value of a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param prefix the prefix of the attribute that is being queried (might be null).
     * @param name the name of tha attribute that is being queried.
     * @return the value of the queried attribute, or null if it does not exist.
     */
    public String getAttributeValue(final String prefix, final String name);

    /**
     * <p>
     *   Returns the value of a specific attribute in the tag (or null if it does not exist).
     * </p>
     *
     * @param attributeName the name of the attribute that is being queried.
     * @return the value of the queried attribute, or null if it does not exist.
     */
    public String getAttributeValue(final AttributeName attributeName);

}
