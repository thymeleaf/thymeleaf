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
package org.thymeleaf.extras.springsecurity5.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.ConfigurationException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.0.1
 *
 */
public final class AclAuthUtils {


    private static final Logger logger = LoggerFactory.getLogger(AclAuthUtils.class);




    private AclAuthUtils() {
        super();
    }




    public static boolean authorizeUsingAccessControlList(
            final IExpressionContext context,
            final Object domainObject,
            final ApplicationContext applicationContext, final String permissionsString,
            final Authentication authentication) {

        final List<Permission> permissions =
                parsePermissionsString(applicationContext, permissionsString);

        return authorizeUsingAccessControlList(context, domainObject, permissions, authentication);

    }



    public static boolean authorizeUsingAccessControlList(
            final IExpressionContext context,
            final Object domainObject, final List<Permission> permissions, 
            final Authentication authentication) {


        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Checking authorization using Access Control List for user \"{}\". " +
            		"Domain object is of class \"{}\" and permissions are \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), (authentication == null? null : authentication.getName()),
                            (domainObject == null? null : domainObject.getClass().getName()), permissions});
        }
        
        final ApplicationContext applicationContext = AuthUtils.getContext(context);

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
        
        if (permissionsString == null || permissionsString.trim().length() == 0) {
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
    

}
