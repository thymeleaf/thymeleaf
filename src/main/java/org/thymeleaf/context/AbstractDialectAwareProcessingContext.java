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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;



/**
 * <p>
 *   Specific abstract superclass for implementations of {@link IProcessingContext} that
 *   are able to add extra expression utility objects to expression evaluations if any
 *   of the configured {@link IDialect}s implement {@link IExpressionEnhancingDialect}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.12
 *
 */
public abstract class AbstractDialectAwareProcessingContext extends AbstractProcessingContext {

    private final Set<IExpressionEnhancingDialect> dialects;
    

    public AbstractDialectAwareProcessingContext(final IContext context, 
            final Collection<? extends IDialect> dialects) {
        super(context);
        this.dialects = selectEnhancingDialects(dialects);
    }

    
    public AbstractDialectAwareProcessingContext(final IContext context,
            final Map<String, Object> localVariables, final Collection<? extends IDialect> dialects) {
        super(context, localVariables);
        this.dialects = selectEnhancingDialects(dialects);
    }

    
    public AbstractDialectAwareProcessingContext(final IContext context,
            final Map<String, Object> localVariables, final Object selectionTarget,
            final boolean selectionTargetSet, final Collection<? extends IDialect> dialects) {
        super(context, localVariables, selectionTarget, selectionTargetSet);
        this.dialects = selectEnhancingDialects(dialects);
    }


    
    protected Set<IExpressionEnhancingDialect> getExpressionEnhancingDialects() {
        return this.dialects;
    }
    

    
    private static Set<IExpressionEnhancingDialect> selectEnhancingDialects(
            final Collection<? extends IDialect> dialects) {
        
        if (dialects == null || dialects.size() == 0) {
            return null;
        }
        
        Set<IExpressionEnhancingDialect> enhancingDialects = null;
        
        for (final IDialect dialect : dialects) {
            if (dialect instanceof IExpressionEnhancingDialect) {
                if (enhancingDialects == null) {
                    enhancingDialects = new HashSet<IExpressionEnhancingDialect>(4, 1.0f);
                }
                enhancingDialects.add((IExpressionEnhancingDialect) dialect);
            }
        }
        
        return enhancingDialects;
        
    }
    
    
    

    @Override
    protected Map<String, Object> computeExpressionObjects() {

        if (this.dialects == null) {
            return super.computeExpressionObjects();
        }
        
        final Map<String,Object> variables = 
                new HashMap<String, Object>(super.computeExpressionObjects());
        
        for (final IExpressionEnhancingDialect dialect : this.dialects) {
            final Map<String,Object> dialectVariables = dialect.getAdditionalExpressionObjects(this);
            if (dialectVariables != null) {
                variables.putAll(dialectVariables);
            }
        }
        
        return variables;
        
    }
    
    
    
    
}
