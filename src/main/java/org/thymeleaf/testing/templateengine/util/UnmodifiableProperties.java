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
package org.thymeleaf.testing.templateengine.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;






public class UnmodifiableProperties extends Properties {

    
    private static final long serialVersionUID = -3538830402772374054L;
    
    

    public UnmodifiableProperties() {
        super();
    }

    public UnmodifiableProperties(final Properties defaults) {
        super(defaults);
    }
    
    
    

    @Override
    public synchronized Object setProperty(final String key, final String value) {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized void load(final Reader reader) throws IOException {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized void load(final InputStream inStream) throws IOException {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Deprecated
    @Override
    public synchronized void save(final OutputStream out, final String comments) {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized Object put(final Object key, final Object value) {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized Object remove(final Object key) {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized void putAll(final Map<? extends Object, ? extends Object> t) {
        throw new UnsupportedOperationException("Properties is read-only");
    }

    @Override
    public synchronized void clear() {
        throw new UnsupportedOperationException("Properties is read-only");
    }
    
}
