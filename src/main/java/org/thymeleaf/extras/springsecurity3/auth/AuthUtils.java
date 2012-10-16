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
package org.thymeleaf.extras.springsecurity3.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.ObjectIdentityRetrievalStrategyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.access.expression.WebSecurityExpressionHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.expression.SpelEvaluationContext;
import org.thymeleaf.spring3.expression.SpelVariableExpressionEvaluator;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class AuthUtils {


    private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);
    
    private static final FilterChain DUMMY_CHAIN = new FilterChain() {
        public void doFilter(ServletRequest request, ServletResponse response) 
                throws IOException, ServletException {
           throw new UnsupportedOperationException();
        }
    };

    
    
    
    private AuthUtils() {
        super();
    }

    
    

    public static Authentication getAuthenticationObject() {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Obtaining authentication object.",
                    new Object[] {TemplateEngine.threadIndex()});
        }
        
        if ((SecurityContextHolder.getContext() == null)) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] No security context found, no authentication object returned.",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            return null;
        }

        final Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] No authentication object found in context.",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            return null;
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Authentication object of class {} found in context for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), authentication.getClass().getName()}, authentication.getName());
        }
        
        return authentication;
        
    }

    

    public static Object getAuthenticationProperty(final Authentication authentication, final String property) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Reading property \"{}\" from authentication object.",
                    new Object[] {TemplateEngine.threadIndex(), property});
        }
        
        if (authentication == null) {
            return null;
        }
        
        try {
            
            final BeanWrapperImpl wrapper = new BeanWrapperImpl(authentication);
            final Object propertyObj = wrapper.getPropertyValue(property);
            
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Property \"{}\" obtained from authentication object " +
                		"for user \"{}\". Returned value of class {}.",
                        new Object[] {TemplateEngine.threadIndex(), property, authentication.getName(), 
                                (propertyObj == null? null : propertyObj.getClass().getName())});
            }
            
            return propertyObj;
            
        } catch (BeansException e) {
            throw new TemplateProcessingException(
                    "Error retrieving value for property \"" + property + "\" of authentication " + 
                    "object of class " + authentication.getClass().getName(), e);
        }

    }
    
    
    
    

    public static boolean authorizeUsingAccessExpression(
            final IProcessingContext processingContext,
            final String accessExpression, final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
    
        Validate.notNull(processingContext, "Processing context cannot be null");
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization using access expression \"{}\" for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
        }

        /*
         * In case this expression is specified as a standard variable expression (${...}), clean it.
         */
        final String expr =
                ((accessExpression != null && accessExpression.startsWith("${") && accessExpression.endsWith("}"))?
                        accessExpression.substring(2, accessExpression.length() - 1) :
                        accessExpression);
        
        final WebSecurityExpressionHandler handler = getExpressionHandler(servletContext);

        Expression expressionObject = null;
        try {
            expressionObject = handler.getExpressionParser().parseExpression(expr);
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happened trying to parse Spring Security access expression \"" +  
                    expr + "\"", e);
        }

        final FilterInvocation filterInvocation = new FilterInvocation(request, response, DUMMY_CHAIN);
        
        final EvaluationContext evaluationContext = handler.createEvaluationContext(authentication, filterInvocation);
        
        /*
         * Initialize the context variables map.
         * 
         * This will allow SpringSecurity expressions to include any variables from
         * the IContext just by accessing them as properties of the "#vars" utility object.
         */
        Map<String,Object> contextVariables = null;
        if (processingContext instanceof Arguments) {
            // Try to initialize the context variables by asking the SpEL expression
            // evaluator object (which might be a subclass of the standard one) to create
            // this variable map.
            
            final Arguments arguments = (Arguments) processingContext;
            final Object expressionEvaluator = 
                    arguments.getExecutionAttribute(StandardDialect.EXPRESSION_EVALUATOR_EXECUTION_ATTRIBUTE);
            
            final SpelVariableExpressionEvaluator spelExprEval =
                    ((expressionEvaluator != null && expressionEvaluator instanceof SpelVariableExpressionEvaluator)?
                            (SpelVariableExpressionEvaluator) expressionEvaluator :
                            SpelVariableExpressionEvaluator.INSTANCE);
            contextVariables = spelExprEval.computeContextVariables(arguments.getConfiguration(), arguments);
            
        }
        
        if (contextVariables == null) {
            // if we could not create it the more integrated way, just do it the hard-wired way
            contextVariables = new HashMap<String, Object>();
            
            final Map<String,Object> expressionObjects = processingContext.getExpressionObjects();
            if (expressionObjects != null) {
                contextVariables.putAll(expressionObjects);
            }
            
        }
        
        
        // We add Thymeleaf's wrapper on top of the SpringSecurity basic evaluation context
        final SpelEvaluationContext spelEvaluationContext = new SpelEvaluationContext(evaluationContext, contextVariables);
        
        if (ExpressionUtils.evaluateAsBoolean(expressionObject, spelEvaluationContext)) {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Checked authorization using access expression \"{}\" for user \"{}\". Access GRANTED.",
                        new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
            }
            
            return true;
            
        }

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checked authorization using access expression \"{}\" for user \"{}\". Access DENIED.",
                    new Object[] {TemplateEngine.threadIndex(), accessExpression, (authentication == null? null : authentication.getName())});
        }
        
        return false;
    
    }
    
    
    
    
    private static WebSecurityExpressionHandler getExpressionHandler(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebSecurityExpressionHandler> expressionHandlers = 
                ctx.getBeansOfType(WebSecurityExpressionHandler.class);

        if (expressionHandlers.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebSecurityExpressionHandler instance could be found in the application " +
                    "context. There must be at least one in order to support expressions in Spring Security " +
                    "authorization queries.");
        }

        return (WebSecurityExpressionHandler) expressionHandlers.values().toArray()[0];
        
    }
    
    
    
    
    
    
    public static boolean authorizeUsingUrlCheck(
            final String url, final String method, final Authentication authentication, 
            final HttpServletRequest request, final ServletContext servletContext) {
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization for URL \"{}\" and method \"{}\" for user \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), url, method, (authentication == null? null : authentication.getName())});
        }
        
        final boolean result =
                getPrivilegeEvaluator(servletContext).isAllowed(
                    request.getContextPath(), url, method, authentication) ? 
                            true : false;

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checked authorization for URL \"{}\" and method \"{}\" for user \"{}\". " +
                    (result? "Access GRANTED." : "Access DENIED."),
                    new Object[] {TemplateEngine.threadIndex(), url, method, (authentication == null? null : authentication.getName())});
        }
        
        return result;
        
    }


    


    
    private static WebInvocationPrivilegeEvaluator getPrivilegeEvaluator(final ServletContext servletContext) {

        final ApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        
        final Map<String, WebInvocationPrivilegeEvaluator> privilegeEvaluators = 
                ctx.getBeansOfType(WebInvocationPrivilegeEvaluator.class);

        if (privilegeEvaluators.size() == 0) {
            throw new TemplateProcessingException(
                    "No visible WebInvocationPrivilegeEvaluator instance could be found in the application " +
                    "context. There must be at least one in order to support URL access checks in " +
                    "Spring Security authorization queries.");
        }

        return (WebInvocationPrivilegeEvaluator) privilegeEvaluators.values().toArray()[0];
        
    }
    

    
    
    
    
    public static boolean authorizeUsingAccessControlList(
            final Object domainObject, final List<Permission> permissions, 
            final Authentication authentication, final ServletContext servletContext) {


        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization using Access Control List for user \"{}\". " +
            		"Domain object is of class \"{}\" and permissions are \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), (authentication == null? null : authentication.getName()),
                            (domainObject == null? null : domainObject.getClass().getName()), permissions});
        }
        
        final ApplicationContext applicationContext = getContext(servletContext);

        final AclService aclService = getBeanOfType(applicationContext, AclService.class);

        if (authentication == null) {
            // If authentication is null, authorization cannot be granted.
            
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Authentication object is null. Access is DENIED. ",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            
            return false;
            
        }
        
        
        /*
         * Initialize required objects
         */
        
        SidRetrievalStrategy sidRetrievalStrategy = getBeanOfType(applicationContext, SidRetrievalStrategy.class);
        if (sidRetrievalStrategy == null) {
            sidRetrievalStrategy = new SidRetrievalStrategyImpl();
        }

        ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = getBeanOfType(applicationContext, ObjectIdentityRetrievalStrategy.class);
        if (objectIdentityRetrievalStrategy == null) {
            objectIdentityRetrievalStrategy = new ObjectIdentityRetrievalStrategyImpl();
        }
        

        /*
         * Compute permissions
         */
        
        if ((null == permissions) || permissions.isEmpty()) {
            
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Permissions are null or empty. Access is DENIED. ",
                        new Object[] {TemplateEngine.threadIndex()});
            }
            
            return false;
            
        }

        if (domainObject == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Domain object for resolved to null. Access by " +
                		"Access Control List is GRANTED.", new Object[] {TemplateEngine.threadIndex()});
            }
            // Access to null object is considered always true
            return true;
        }

        final List<Sid> sids = 
                sidRetrievalStrategy.getSids(SecurityContextHolder.getContext().getAuthentication());
        
        final ObjectIdentity oid = 
                objectIdentityRetrievalStrategy.getObjectIdentity(domainObject);

        try {
            
            final Acl acl = aclService.readAclById(oid, sids);

            if (acl.isGranted(permissions, sids, false)) {
                
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Checked authorization using Access Control List for user \"{}\". " +
                            "Domain object is of class \"{}\" and permissions are \"{}\". Access is GRANTED.",
                            new Object[] {TemplateEngine.threadIndex(), authentication.getName(),
                                    domainObject.getClass().getName(), permissions});
                }
                
                return true;
                
            }

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Checked authorization using Access Control List for user \"{}\". " +
                        "Domain object is of class \"{}\" and permissions are \"{}\". Access is DENIED.",
                        new Object[] {TemplateEngine.threadIndex(), authentication.getName(),
                                domainObject.getClass().getName(), permissions});
            }
            
            return false;
            
        } catch (final NotFoundException nfe) {
            return false;
        }
        
    }

    



    public static List<Permission> parsePermissionsString(
            final ApplicationContext applicationContext, final String permissionsString) 
            throws NumberFormatException {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Parsing permissions string \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), permissionsString});
        }
        
        if (permissionsString == null || permissionsString.trim().equals("")) {
            return Collections.emptyList();
        }
        
        PermissionFactory permissionFactory = getBeanOfType(applicationContext, PermissionFactory.class);
        if (permissionFactory == null) {
            permissionFactory = new DefaultPermissionFactory();
        }
        
        final Set<Permission> permissions = new HashSet<Permission>();
        final StringTokenizer tokenizer = new StringTokenizer(permissionsString, ",", false);

        while (tokenizer.hasMoreTokens()) {
            String permission = tokenizer.nextToken();
            try {
                permissions.add(permissionFactory.buildFromMask(Integer.valueOf(permission).intValue()));
            } catch (final NumberFormatException nfe) {
                // Not an integer mask. Try using a name
                permissions.add(permissionFactory.buildFromName(permission));
            }
        }

        return new ArrayList<Permission>(permissions);
        
    }
    
    
    
    
    private static <T> T getBeanOfType(final ApplicationContext applicationContext, final Class<T> type) {
        
        final Map<String, T> map = applicationContext.getBeansOfType(type);

        for (ApplicationContext context = applicationContext.getParent(); context != null; context = context.getParent()) {
            map.putAll(context.getBeansOfType(type));
        }

        if (map.size() == 0) {
            return null;
        } else if (map.size() == 1) {
            return map.values().iterator().next();
        }

        throw new ConfigurationException(
                "Found incorrect number of " + type.getSimpleName() +" instances in " +
                "application context - you must have only have one!");
        
    }
    


    
    public static ApplicationContext getContext(final ServletContext servletContext) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }
    
    
}
