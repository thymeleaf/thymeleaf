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
package org.thymeleaf.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 *   Base abstract class for {@link IExpressionObjects} implementations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractExpressionObjects implements IExpressionObjects {


    private Map<String,Object> objects;


    protected AbstractExpressionObjects() {
        super();
    }

    public int size() {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        return this.objects.size();
    }

    public boolean containsObject(final String name) {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        return this.objects.containsKey(name);
    }

    public Set<String> getObjectNames() {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        return this.objects.keySet();
    }

    public Object getObject(final String name) {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        return this.objects.get(name);
    }

    protected void put(final String name, final Object object) {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        this.objects.put(name, object);
    }

    protected void putAll(final Map<? extends String, ?> map) {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        this.objects.putAll(map);
    }

    protected Map<String,Object> getObjectsMap() {
        if (this.objects == null) {
            this.objects = new HashMap<String, Object>(30);
        }
        return this.objects;
    }

}
