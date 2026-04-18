package org.thymeleaf.spring6.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.StandardExpressionParser;

public class SpelWhitespaceTest {

    @Test
    public void testSpelNewWhitespace() {
        SpelExpressionParser parser = new SpelExpressionParser();
        
        // Test with space
        Assertions.assertNotNull(parser.parseExpression("new java.lang.String('test')"));
        
        // Test with newline
        Assertions.assertNotNull(parser.parseExpression("new\njava.lang.String('test')"));
        
        // Test with multiple newlines and mixed whitespace
        Assertions.assertNotNull(parser.parseExpression("new\r\n  java.lang.String('test')"));
        
        // Test other places where whitespace is allowed
        Assertions.assertNotNull(parser.parseExpression("1\n+\n1"));
        Assertions.assertNotNull(parser.parseExpression("'a'.\nconcat('b')"));
    }

    @Test
    public void testThymeleafStandardParserNew() {
        StandardExpressionParser parser = new StandardExpressionParser();
        IExpressionContext context = org.mockito.Mockito.mock(IExpressionContext.class);
        IEngineConfiguration configuration = org.mockito.Mockito.mock(IEngineConfiguration.class);
        org.mockito.Mockito.when(context.getConfiguration()).thenReturn(configuration);

        // Standalone "new" is NOT a simple expression, so StandardExpressionParser fails.
        try {
            parser.parseExpression(context, "new java.lang.String('test')");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Standalone new failed as expected: " + e.getMessage());
        }

        // Inside ${...} it's a SpEL expression, which allows newlines.
        Assertions.assertNotNull(parser.parseExpression(context, "${new java.lang.String('test')}"));
        Assertions.assertNotNull(parser.parseExpression(context, "${new\njava.lang.String('test')}"));
    }
}
