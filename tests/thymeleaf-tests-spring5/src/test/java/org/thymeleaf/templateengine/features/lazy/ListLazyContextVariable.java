/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.features.lazy;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.context.LazyContextVariable;


public class ListLazyContextVariable extends LazyContextVariable {

    public ListLazyContextVariable() {
        super();
    }


    @Override
    protected Object loadValue() {
        final List<String> values = new ArrayList<String>();
        values.add("one");
        values.add("two");
        values.add("three");
        values.add("four");
        values.add("five");
        return values;
    }


}
