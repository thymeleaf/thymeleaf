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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;




/**
 * <p>
 *   Implementation of {@link IFragmentSpec} that allows the execution of
 *   two fragment specs in chain, effectively performing a double-filering.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.11
 *
 */
public final class ChainedFragmentSpec implements IFragmentSpec {
    
    private final IFragmentSpec fragmentSpec1;
    private final IFragmentSpec fragmentSpec2;
    
    
    /**
     * <p>
     *   Creates a new instance of this fragment spec.
     * </p>
     * 
     * @param fragmentSpec1 the first fragment spec that will be executed.
     * @param fragmentSpec2 the second fragment spec that will be executed.
     */
    public ChainedFragmentSpec(
            final IFragmentSpec fragmentSpec1, final IFragmentSpec fragmentSpec2) {
        super();
        Validate.notNull(fragmentSpec1, "Fragment spec 1 cannot be null");
        Validate.notNull(fragmentSpec2, "Fragment spec 2 cannot be null");
        this.fragmentSpec1 = fragmentSpec1;
        this.fragmentSpec2 = fragmentSpec2;
    }
    

    /**
     * <p>
     *   Returns the first fragment spec in the chain.
     * </p>
     * 
     * @return the fragment spec
     */
    public IFragmentSpec getFragmentSpec1() {
        return this.fragmentSpec1;
    }


    /**
     * <p>
     *   Returns the second fragment spec in the chain.
     * </p>
     * <p>
     *   This fragment spec will be executed using the output from 
     *   <tt>fragmentSpec1</tt> as input.
     * </p>
     * 
     * @return the fragment spec
     */
    public IFragmentSpec getFragmentSpec2() {
        return this.fragmentSpec2;
    }



    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {
        final List<Node> firstResult = this.fragmentSpec1.extractFragment(configuration, nodes);
        return this.fragmentSpec2.extractFragment(configuration, firstResult);
    }


    
    
    @Override
    public String toString() {
        return "(FRAGMENT SPEC 1: " + this.fragmentSpec1 + " | FRAGMENT SPEC 2: " + this.fragmentSpec2 +")";
    }

    
    
}

