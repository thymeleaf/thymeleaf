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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;




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
        
        if ((SecurityContextHolder.getContext() == null)) {
            return null;
        }

        final Authentication authentication = 
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            // There is an Authentication object, but 
            return null;
        }
        
        return authentication;
        
    }

    

    public static Object getAuthenticationProperty(final Authentication authentication, final String property) {
        
        if (authentication == null) {
            return null;
        }
        
        try {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(authentication);
            return wrapper.getPropertyValue(property);
        } catch (BeansException e) {
            throw new TemplateProcessingException(
                    "Error retrieving value for property \"" + property + "\" of authentication " + 
                    "object of class " + authentication.getClass().getName(), e);
        }

    }
    
    
    
    

    public static boolean authorizeUsingAccessExpression(
            final String accessExpression, final Authentication authentication, 
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext) {
    
        final WebSecurityExpressionHandler handler = getExpressionHandler(servletContext);

        Expression expressionObject = null;
        try {
            expressionObject = handler.getExpressionParser().parseExpression(accessExpression);
        } catch (ParseException e) {
            throw new TemplateProcessingException(
                    "An error happened trying to parse Spring Security access expression \"" +  
                    accessExpression + "\"", e);
        }

        final FilterInvocation filterInvocation = new FilterInvocation(request, response, DUMMY_CHAIN);

        if (ExpressionUtils.evaluateAsBoolean(
                expressionObject, 
                handler.createEvaluationContext(authentication, filterInvocation))) {
            return true;
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
        
        return getPrivilegeEvaluator(servletContext).isAllowed(
                    request.getContextPath(), url, method, authentication) ? 
                            true : false;
        
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


        final ApplicationContext applicationContext = getContext(servletContext);

        final AclService aclService = getBeanOfType(applicationContext, AclService.class);

        if (authentication == null) {
            // If authentication is null, authorization cannot be granted.
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
            return false;
        }

        if (domainObject == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Domain object for resolved to null. Authorization by " +
                		"Access Control List is granted.", new Object[] {TemplateEngine.threadIndex()});
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
                return true;
            }
            return false;
            
        } catch (final NotFoundException nfe) {
            return false;
        }
        
    }

    



    public static List<Permission> parsePermissionsString(
            final ApplicationContext applicationContext, final String permissionsString) 
            throws NumberFormatException {

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
