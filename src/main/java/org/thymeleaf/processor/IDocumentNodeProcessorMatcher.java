/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor;

import org.thymeleaf.dom.Document;



/**
 * <p>
 *   Sub-interface of {@link IProcessorMatcher} for matchers that match 
 *   {@link org.thymeleaf.dom.Document} nodes.
 * </p>
 * <p>
 *   Every processor matching {@link org.thymeleaf.dom.Document} nodes should 
 *   have matchers implementing this interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.13
 *
 */
public interface IDocumentNodeProcessorMatcher extends IProcessorMatcher<Document> {

    // Marker interface: no additional methods at the moment
    
}
