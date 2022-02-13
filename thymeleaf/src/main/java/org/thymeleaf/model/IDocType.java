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

/**
 * <p>
 *   Event interface defining a DOCTYPE clause.
 * </p>
 * <p>
 *   Note that any implementations of this interface should be <strong>immutable</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IDocType extends ITemplateEvent {

    /**
     * <p>
     *   Returns the keyword of the DOCTYPE clause in its original
     *   case (usually {@code DOCTYPE}).
     * </p>
     *
     * @return the DOCTYPE keyword.
     */
    public String getKeyword();

    /**
     * <p>
     *   Returns the root element name in the DOCTYPE clause. Will normally be
     *   {@code html} in HTML or XHTML documents.
     * </p>
     *
     * @return the element name
     */
    public String getElementName();

    /**
     * <p>
     *   Returns the type of DOCTYPE, usually {@code null}, {@code PUBLIC} or {@code SYSTEM}.
     * </p>
     *
     * @return the type of DOCTYPE (might be null).
     */
    public String getType();

    /**
     * <p>
     *   Returns the PUBLIC ID, if it has been specified.
     * </p>
     *
     * @return the PUBLIC ID (might be null).
     */
    public String getPublicId();

    /**
     * <p>
     *   Returns the SYSTEM ID, if it has been specified.
     * </p>
     *
     * @return the SYSTEM ID (might be null).
     */
    public String getSystemId();

    /**
     * <p>
     *   Returns the internal subset in the DOCTYPE, if it has been specified.
     * </p>
     *
     * @return the internal subset (might be null).
     */
    public String getInternalSubset();

    /**
     * <p>
     *   Returns the complete DOCTYPE clause as a String.
     * </p>
     *
     * @return the complete DOCTYPE clause.
     */
    public String getDocType();

}
