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
package org.thymeleaf.aurora.resource;

import java.io.Serializable;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class CharArrayResource implements IResource, Serializable {

    private static final long serialVersionUID = -2301065999827410882L;

    private final String name;
    private final char[] content;
    private final int offset;
    private final int len;


    public CharArrayResource(final String name, final char[] content) {
        this(name, content, 0, (content != null? content.length : 0));
    }


    public CharArrayResource(final String name, final char[] content, final int offset, final int len) {
        super();
        // We will intentionally not use org.thymeleaf.util.Validate in order to make resource implementations
        // as free from other thymeleaf APIs as possible.
        if (content == null) {
            throw new IllegalArgumentException("Resource content cannot be null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Resource offset must be equal to or greater than 0");
        }
        if (len < 0) {
            throw new IllegalArgumentException("Resource length must be equal to or greater than 0");
        }
        this.name = name;
        this.content = content;
        this.offset = offset;
        this.len = len;
    }


    public String getName() {
        return this.name;
    }


    public char[] getContent() {
        return this.content;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLen() {
        return this.len;
    }
}
