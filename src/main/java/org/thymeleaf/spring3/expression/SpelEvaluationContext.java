package org.thymeleaf.spring3.expression;

import java.util.List;
import java.util.Map;

import org.springframework.expression.BeanResolver;
import org.springframework.expression.ConstructorResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeComparator;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.TypedValue;

/**
 * <p>
 *   The purpose of this class is to serve as a wrapper for a
 *   standard-defined {@link org.springframework.expression.spel.support.StandardEvaluationContext} object
 *   which will contain every expression-evaluation structure needed except for the
 *   expression variables. This avoids initializing the structures in 
 *   {@link org.springframework.expression.spel.support.StandardEvaluationContext} (some of which
 *   involve synchronized blocks) with every SpEL expression evaluation. 
 * </p>
 * 
 * @author Guven Demir
 *
 * @since 1.1.3
 *
 */
public final class SpelEvaluationContext implements EvaluationContext {
    
    private final EvaluationContext delegate;
    private final Map<String,Object> variables;
    
    public SpelEvaluationContext(final EvaluationContext delegate, final Map<String,Object> variables) {
        super();
        this.delegate = delegate;
        this.variables = variables;
    }

    
    public TypedValue getRootObject() {
        return this.delegate.getRootObject();
    }

    public List<ConstructorResolver> getConstructorResolvers() {
        return this.delegate.getConstructorResolvers();
    }

    public List<MethodResolver> getMethodResolvers() {
        return this.delegate.getMethodResolvers();
    }

    public List<PropertyAccessor> getPropertyAccessors() {
        return this.delegate.getPropertyAccessors();
    }

    public TypeLocator getTypeLocator() {
        return this.delegate.getTypeLocator();
    }

    public TypeConverter getTypeConverter() {
        return this.delegate.getTypeConverter();
    }

    public TypeComparator getTypeComparator() {
        return this.delegate.getTypeComparator();
    }

    public OperatorOverloader getOperatorOverloader() {
        return this.delegate.getOperatorOverloader();
    }

    public BeanResolver getBeanResolver() {
        return this.delegate.getBeanResolver();
    }

    public void setVariable(final String name, final Object value) {
        this.variables.put(name, value);
    }

    public Object lookupVariable(final String name) {
        final Object result = this.variables.get(name);
        if (result != null) {
            return result;
        }
        // fail back to delegate
        return this.delegate.lookupVariable(name);
    }
    
}
