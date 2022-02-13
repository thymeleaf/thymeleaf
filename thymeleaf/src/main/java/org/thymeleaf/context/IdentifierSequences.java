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
package org.thymeleaf.context;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Objects of this class are kept at {@link ITemplateContext} in order to provide templates
 *   with a way to create unique {@code id} attribute values during template processing.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 * 
 */
public final class IdentifierSequences {


    private final Map<String,Integer> idCounts;




    public IdentifierSequences() {
        super();
        this.idCounts = new HashMap<String,Integer>(1,1.0f);
    }




    /**
     * <p>
     *   Returns a new index (ID count) for a specific
     *   value of the {@code id} attribute, and increments
     *   the count.
     * </p>
     *
     * @param id the ID for which the count will be computed
     * @return the new count, ready to be used
     */
    public Integer getAndIncrementIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = Integer.valueOf(1);
        }
        this.idCounts.put(id, Integer.valueOf(count.intValue() + 1));
        return count;
    }


    /**
     * <p>
     *   Returns the index (ID count) for a specific
     *   value of the {@code id} attribute without incrementing
     *   the count.
     * </p>
     *
     * @param id the ID for which the count will be retrieved
     * @return the current count
     */
    public Integer getNextIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = Integer.valueOf(1);
        }
        return count;
    }


    /**
     * <p>
     *   Returns the last index (ID count) returned for a specific
     *   value of the {@code id} attribute (without incrementing
     *   the count).
     * </p>
     *
     * @param id the ID for which the last count will be retrieved
     * @return the count
     */
    public Integer getPreviousIDSeq(final String id) {
        Validate.notNull(id, "ID cannot be null");
        final Integer count = this.idCounts.get(id);
        if (count == null) {
            throw new TemplateProcessingException(
                    "Cannot obtain previous ID count for ID \"" + id + "\"");
        }
        return Integer.valueOf(count.intValue() - 1);
    }


}
