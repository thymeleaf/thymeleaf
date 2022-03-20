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
package org.thymeleaf.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * <p>
 *   Utility class of internal use for holding the patterns that certain artifacts
 *   (usually template names) must match for obtaining some consideration. For
 *   example, it is used at many {@link org.thymeleaf.templateresolver.ITemplateResolver}
 *   implementations for holding the patterns that match a template to a specific
 *   template mode.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class PatternSpec {

    private static final int DEFAULT_PATTERN_SET_SIZE = 3;
    
    private LinkedHashSet<String> patternStrs;
    private LinkedHashSet<Pattern> patterns;

    
    
                   
    public PatternSpec() {
        super();
    }

    

    public boolean isEmpty() {
        return this.patterns == null || this.patterns.size() == 0;
    }

    
    public Set<String> getPatterns() {
        if (this.patternStrs == null) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(this.patternStrs);
    }


    public void setPatterns(final Set<String> newPatterns) {
        if (newPatterns != null) {
            if (this.patterns == null) {
                this.patternStrs = new LinkedHashSet<String>(DEFAULT_PATTERN_SET_SIZE);
                this.patterns = new LinkedHashSet<Pattern>(DEFAULT_PATTERN_SET_SIZE);
            } else {
                this.patternStrs.clear();
                this.patterns.clear();
            }
            this.patternStrs.addAll(newPatterns);
            for (final String pattern : newPatterns) {
                this.patterns.add(PatternUtils.strPatternToPattern(pattern));
            }
        } else if (this.patterns != null) {
            this.patternStrs.clear();
            this.patterns.clear();
        }
    }
    
    
    public void addPattern(final String pattern) {
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        if (this.patterns == null) {
            this.patternStrs = new LinkedHashSet<String>(DEFAULT_PATTERN_SET_SIZE);
            this.patterns = new LinkedHashSet<Pattern>(DEFAULT_PATTERN_SET_SIZE);
        }
        this.patternStrs.add(pattern);
        this.patterns.add(PatternUtils.strPatternToPattern(pattern));
    }

    
    public void clearPatterns() {
        if (this.patterns != null) {
            this.patternStrs.clear();
            this.patterns.clear();
        }
    }
    
    
    
    
    public boolean matches(final String templateName) {
        if (this.patterns == null) {
            return false;
        }
        for (final Pattern p : this.patterns) {
            if (p.matcher(templateName).matches()) {
                return true;
            }
        }
        return false;
    }
    
    
    
    
    
    
}
