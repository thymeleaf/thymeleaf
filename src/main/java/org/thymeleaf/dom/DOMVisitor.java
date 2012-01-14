package org.thymeleaf.dom;


/**
 * <p>
 *   A generic visitor interface for Document Object Model trees.
 * </p>
 * 
 * @author Guven Demir
 */
public interface DOMVisitor {
    
    void visit(final Node node);
    
}
