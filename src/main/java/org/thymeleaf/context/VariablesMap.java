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
package org.thymeleaf.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * <p>
 *   Special implementation of the {@link Map} interface that
 *   will be used for containing context variables in {@link IContext} 
 *   implementations.
 * </p>
 * <p>
 *   Constructors in this class mimic those in {@link HashMap} and
 *   have the same meaning.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Michal Kreuzman
 * 
 * @since 1.0
 *
 */
public class VariablesMap<K,V> extends HashMap<K,V> {

    private static final long serialVersionUID = 6785956724279950873L;

    private List<IContextVariableRestriction> restrictions = null;

    
    
    static {

        try {

            final Class<?> ognlRuntimeClass = Class.forName("ognl.OgnlRuntime");
            final Class<?> ognlPropertyAccessorClass = Class.forName("ognl.PropertyAccessor");
            final Class<?> newPropertyAccessorClass =
                    Class.forName(VariablesMap.class.getPackage().getName() + ".OGNLVariablesMapPropertyAccessor");
            final Method setPropertyAccessorMethod =
                    ognlRuntimeClass.getMethod("setPropertyAccessor",Class.class, ognlPropertyAccessorClass);
            final Object newPropertyAccessor = newPropertyAccessorClass.newInstance();
            setPropertyAccessorMethod.invoke(null, VariablesMap.class, newPropertyAccessor);

        } catch (final ClassNotFoundException ignored) {
            // Nothing bad. We simply don't have OGNL in our classpath. We're probably using Spring.
        } catch (final NoSuchMethodException e) {
            // We will not ignore this: we have OGNL, but probably not the right version.
            throw new TemplateProcessingException(
                    "Class ognl.OgnlRuntime does not have method 'setPropertyAccessor'. " +
                    "Maybe OGNL version is too old/new?", e);
        } catch (final Exception e) {
            // We will not ignore this: there's a problem creating an instance of the new property accessor!
            throw new TemplateProcessingException("Exception while configuring OGNL variables map property accessor", e);
        }

    }

    
    
    
    public VariablesMap() {
        super();
    }

    public VariablesMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public VariablesMap(final int initialCapacity) {
        super(initialCapacity);
    }

    public VariablesMap(final Map<? extends K, ? extends V> m) {
        super(m);
    }


    
    
    public List<IContextVariableRestriction> getRestrictions() {
        return this.restrictions;
    }

    public void setRestrictions(final List<IContextVariableRestriction> restrictions) {
        this.restrictions = restrictions;
    }
    
    

    @Override
    public V get(final Object key) {

        if (this.restrictions != null && !this.restrictions.isEmpty()) {
            for (final IContextVariableRestriction restriction : this.restrictions) {
                if (restriction != null) {
                    restriction.checkAccess(this, (String)key);
                }
            }
        }
        
        return super.get(key);

    }


    @SuppressWarnings("unchecked")
    public VariablesMap<K,V> clone() {
        return (VariablesMap<K,V>) super.clone();
    }



}
