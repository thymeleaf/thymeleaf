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
package org.thymeleaf.linkbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *   Base abstract implementation for link builders implementing the
 *   {@link ILinkBuilder} interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public abstract class AbstractLinkBuilder
        implements ILinkBuilder {


    private static final Logger logger = LoggerFactory.getLogger(AbstractLinkBuilder.class);

    private String name = this.getClass().getName();
    private Integer order = null;




    protected AbstractLinkBuilder() {
        super();
    }

    




    public final String getName() {
        return this.name;
    }

    
    /**
     * <p>
     *   Sets a name for this link builder.
     * </p>
     * 
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }


    
    public final Integer getOrder() {
        return this.order;
    }

    
    /**
     * <p>
     *   Sets a new order for the link builder.
     * </p>
     * 
     * @param order the new order
     */
    public void setOrder(final Integer order) {
        this.order = order;
    }

    
}
