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
package org.thymeleaf.message.resolver;

import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Result of resolving a message.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class MessageResolution {

    private final String resolvedMessage;
    
    
    public MessageResolution(final String resolvedMessage) {
        super();
        Validate.notNull(resolvedMessage, "Resolved message cannot be null");
        this.resolvedMessage = resolvedMessage;
    }
    
    
    /**
     * <p>
     *  Returns the resolved message (as String).
     * </p>
     * 
     * @return the resolved message.
     */
    public String getResolvedMessage() {
        return this.resolvedMessage;
    }
    
}
