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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class WholeFragmentSpec implements IFragmentSpec {
    
    public static final WholeFragmentSpec INSTANCE = new WholeFragmentSpec();
    
    
    public WholeFragmentSpec() {
        super();
    }

    
    
    public final List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        return nodes;
    }

    
    
    @Override
    public String toString() {
        return "(WHOLE TEMPLATE)";
    }
    
}

