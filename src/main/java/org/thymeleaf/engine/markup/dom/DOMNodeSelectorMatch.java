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
package org.thymeleaf.engine.markup.dom;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public enum DOMNodeSelectorMatch {

    MATCHES,                      // The current element/node matches the selector.
    DOESNT_MATCH,                 // The current element/node does not match, nor any of its
                                  // children can.
    DOESNT_MATCH_CHILDREN_COULD,  // The current element/node does not match, but the current selector
                                  // item is able to match recursively , so children might match.
    MIGHT_MATCH                   // We don't have enough information yet to determine whether this
                                  // element/node matches or not. For example, the element name might have
                                  // matched but we might also need some attribute-based matching.

}
