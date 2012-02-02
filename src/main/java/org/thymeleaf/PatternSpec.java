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
package org.thymeleaf;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.util.PatternUtils;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class PatternSpec {

    
    private final LinkedHashSet<String> patternStrs = new LinkedHashSet<String>();
    private final LinkedHashSet<Pattern> patterns = new LinkedHashSet<Pattern>();

    private volatile boolean initialized;
    
    
    
                   
    public PatternSpec() {
        super();
    }

    
    
    protected final boolean isInitialized() {
        return this.initialized;
    }

    
    public final synchronized void initialize() {
        
        if (!isInitialized()) {
            this.initialized = true;
        }
        
    }
    
    
    protected final void checkNotInitialized() {
        if (isInitialized()) {
            throw new AlreadyInitializedException(
                    "Cannot modify template resolver when it has already been initialized");
        }
    }
    


    
    public final Set<String> getPatterns() {
        return Collections.unmodifiableSet(this.patternStrs);
    }


    public final void setPatterns(final Set<String> newPatterns) {
        checkNotInitialized();
        if (newPatterns != null) {
            this.patternStrs.addAll(newPatterns);
            for (final String pattern : newPatterns) {
                this.patterns.add(PatternUtils.strPatternToPattern(pattern));
            }
        }
    }
    
    
    public final void addPattern(final String pattern) {
        checkNotInitialized();
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        this.patternStrs.add(pattern);
        this.patterns.add(PatternUtils.strPatternToPattern(pattern));
    }

    
    public final void clearPatterns() {
        checkNotInitialized();
        this.patternStrs.clear();
        this.patterns.clear();
    }
    
    
    
    
    public boolean matches(final String templateName) {
        for (final Pattern p : this.patterns) {
            if (p.matcher(templateName).matches()) {
                return true;
            }
        }
        return false;
    }
    
    
    
    
    
    
}
