/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.util.Validate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;




/**
 * <p>
 *   This class manages a couple of flags used for indicating that a node in the template DOM 
 *   (or the whole tree of nodes below it) has no reason to be executed (either because it 
 *   should not be executed to avoid issues like code injection, or because we can assure 
 *   there are no processors in any of the dialects that can be applied to it).  
 * </p>
 * <p>
 *   Among other uses, these flags are set into Text nodes that are created during template 
 *   processing. The presence of this flag disallows the execution of text <i>inlining</i> 
 *   on nodes already generated from template code, effectively avoiding code injection. 
 * </p>
 * <p>
 *   These flags are set as Node <i>user data</i> using the 
 *   ({@link org.w3c.dom.Node#setUserData(String, Object, org.w3c.dom.UserDataHandler)})
 *   method.
 * </p>
 * <p>
 *   This class should only be directly used when creating custom processors/dialects.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class DOMExecution {
    
    
    private static final String TH_NON_EXECUTABLE_NODE = "TH_NON_EXECUTABLE_NODE";
    private static final String TH_NON_EXECUTABLE_TREE = "TH_NON_EXECUTABLE_TREE";
    
    private static final NonExecutableNodeHandler TH_NON_EXECUTABLE_NODE_HANDLER = new NonExecutableNodeHandler();
    private static final NonExecutableTreeHandler TH_NON_EXECUTABLE_TREE_HANDLER = new NonExecutableTreeHandler();
    
    
    
    /**
     * <p>
     *   Returns whether a node has any of the <i>executable</i> flags
     *   (either the node-specific one or the tree-wide one).
     * </p>
     * 
     * @param node the node to be queried.
     * @return true if the node is executable, false if not (has any of the flags set).
     */
    public static boolean isExecutableNode(final Node node) {
        Validate.notNull(node, "Node cannot be null");
        return 
            node.getUserData(TH_NON_EXECUTABLE_NODE) == null && 
            node.getUserData(TH_NON_EXECUTABLE_TREE) == null;
    }

    
    /**
     * <p>
     *   Returns whether a node has the <i>executable tree</i> flag set, which
     *   means that neither this node is executable nor any of its children.
     * </p>
     * 
     * @param node the node to be queried.
     * @return true if the node and its children are executable, false if not.
     */
    public static boolean isExecutableTree(final Node node) {
        Validate.notNull(node, "Node cannot be null");
        return node.getUserData(TH_NON_EXECUTABLE_TREE) == null;
    }
    
    

    /**
     * <p>
     *   Sets or cleans the flag indicating a node should not be executed. This flag
     *   has no influence in the execution of this node's children.
     * </p>
     * 
     * @param node the node to be applied the flag.
     * @param executable whether the node should be considered executable (clean the flag)
     *        of non-executable (set the flag).
     */
    public static void setExecutableNode(final Node node, final boolean executable) {
        Validate.notNull(node, "Node cannot be null");
        if (executable) {
            node.setUserData(TH_NON_EXECUTABLE_NODE, null, null);
        } else {
            node.setUserData(TH_NON_EXECUTABLE_NODE, TH_NON_EXECUTABLE_NODE, TH_NON_EXECUTABLE_NODE_HANDLER);
        }
    }

    
    
    /**
     * <p>
     *   Sets or cleans the flag indicating neither the node nor any of its children should 
     *   not be executed.
     * </p>
     * 
     * @param node the node to be applied the flag.
     * @param executable whether the node should be considered executable (clean the flag)
     *        of non-executable (set the flag).
     */
    public static void setExecutableTree(final Node node, final boolean executable) {
        Validate.notNull(node, "Node cannot be null");
        if (executable) {
            node.setUserData(TH_NON_EXECUTABLE_TREE, null, null);
        } else {
            node.setUserData(TH_NON_EXECUTABLE_TREE, TH_NON_EXECUTABLE_TREE, TH_NON_EXECUTABLE_TREE_HANDLER);
        }
    }
    
    
    
    
    
    
    public static void clearExecutabilityFlags(final Node node) {
        Validate.notNull(node, "Node cannot be null");
        unsafeClearExecutabilityFlags(node);
    }
    

    
    private static void unsafeClearExecutabilityFlags(final Node node) {
        
        DOMExecution.setExecutableNode(node, true);
        DOMExecution.setExecutableTree(node, true);
        
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            unsafeClearExecutabilityFlags(children.item(i));
        }

    }
    
    
    
    
    private DOMExecution() {
        super();
    }
    
    
    
    public static class NonExecutableNodeHandler implements UserDataHandler {

        NonExecutableNodeHandler() {
            super();
        }
        
        public void handle(final short operation, final String key, final Object data, 
                final Node src, final Node dst) {
            if (operation == UserDataHandler.NODE_CLONED || operation == UserDataHandler.NODE_IMPORTED) {
                DOMExecution.setExecutableNode(dst, false);
            }
        }
        
    }
    
    
    
    public static class NonExecutableTreeHandler implements UserDataHandler {

        NonExecutableTreeHandler() {
            super();
        }
        
        public void handle(final short operation, final String key, final Object data, 
                final Node src, final Node dst) {
            if (operation == UserDataHandler.NODE_CLONED || operation == UserDataHandler.NODE_IMPORTED) {
                DOMExecution.setExecutableTree(dst, false);
            }
        }
        
    }
    
}
