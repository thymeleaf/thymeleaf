/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ognl.ASTAdd;
import ognl.ASTAnd;
import ognl.ASTAssign;
import ognl.ASTBitAnd;
import ognl.ASTBitNegate;
import ognl.ASTBitOr;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTDivide;
import ognl.ASTEq;
import ognl.ASTEval;
import ognl.ASTGreater;
import ognl.ASTGreaterEq;
import ognl.ASTIn;
import ognl.ASTInstanceof;
import ognl.ASTKeyValue;
import ognl.ASTLess;
import ognl.ASTLessEq;
import ognl.ASTList;
import ognl.ASTMap;
import ognl.ASTMethod;
import ognl.ASTMultiply;
import ognl.ASTNegate;
import ognl.ASTNot;
import ognl.ASTNotEq;
import ognl.ASTNotIn;
import ognl.ASTOr;
import ognl.ASTProject;
import ognl.ASTProperty;
import ognl.ASTRemainder;
import ognl.ASTRootVarRef;
import ognl.ASTSelect;
import ognl.ASTSelectFirst;
import ognl.ASTSelectLast;
import ognl.ASTSequence;
import ognl.ASTShiftLeft;
import ognl.ASTShiftRight;
import ognl.ASTSubtract;
import ognl.ASTTest;
import ognl.ASTThisVarRef;
import ognl.ASTUnsignedShiftRight;
import ognl.ASTVarRef;
import ognl.ASTXor;
import ognl.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;


/**
 * <p>
 *   Checks applied to OGNL expressions executed in restricted mode.
 * </p>
 * <p>
 *   Note this class is <strong>meant for internal use only</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.7
 */
final class OGNLExpressionRestrictions {

    private static final Logger logger = LoggerFactory.getLogger(OGNLExpressionRestrictions.class);

    private static final String PARAM_NAME = "param";

    // Limits on the structure of the checked expressions (outside string literals, which are only located
    // approximately for this), so that parsing them cannot exhaust the thread stack
    private static final int MAX_LENGTH = 512;
    private static final int MAX_NESTING = 16;
    private static final int MAX_OPERATORS = 128;
    private static final String OPERATOR_CHARS = "!-~?:=+*/%<>&|^";



    /*
     * Confirms a match of StandardExpressionUtils.containsExternalAccess(...) on the parsed expression, so that the
     * contents of string literals are not taken into account (parsing does not evaluate anything). Any expression
     * that cannot be checked this way for whatever reason is considered a match.
     */
    static boolean containsExternalAccessOutsideStringLiterals(final String expression) {
        try {
            if (exceedsLimits(expression)) {
                return couldNotBeChecked(expression, null);
            }
            return ParsedExpressionCheck.containsExternalAccess(expression);
        } catch (final StackOverflowError e) {
            return couldNotBeChecked(expression, e);
        } catch (final VirtualMachineError e) {
            throw e;
        } catch (final Throwable t) {
            return couldNotBeChecked(expression, t);
        }
    }


    static Set<Class<?>> getNodeTypesWithoutExternalAccess() {
        return ParsedExpressionCheck.NODE_TYPES_WITHOUT_EXTERNAL_ACCESS;
    }


    private static boolean exceedsLimits(final String expression) {
        int length = 0;
        int nesting = 0;
        int maxNesting = 0;
        int operators = 0;
        char quote = 0;
        final int len = expression.length();
        for (int i = 0; i < len; i++) {
            final char c = expression.charAt(i);
            if (quote != 0) {
                if (c == '\\') {
                    i++;
                } else if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '\'' || c == '"') {
                quote = c;
                continue;
            }
            length++;
            if (c == '(' || c == '[' || c == '{') {
                nesting++;
                maxNesting = Math.max(maxNesting, nesting);
            } else if (c == ')' || c == ']' || c == '}') {
                nesting = Math.max(0, nesting - 1);
            } else if (OPERATOR_CHARS.indexOf(c) >= 0) {
                operators++;
            }
        }
        return length > MAX_LENGTH || maxNesting > MAX_NESTING || operators > MAX_OPERATORS;
    }


    private static boolean couldNotBeChecked(final String expression, final Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[THYMELEAF][{}] OGNL expression \"{}\" could not be checked on its parsed form{}, so it will be " +
                    "considered to contain object instantiation, static class access or parameter access",
                    TemplateEngine.threadIndex(), expression, (t == null ? " (structure exceeds the check limits)" : ""), t);
        }
        return true;
    }



    /*
     * All OGNL types are only referenced from here, so that any problems obtaining them happen during the check.
     */
    private static final class ParsedExpressionCheck {

        // Types of parsed expression nodes that cannot perform any instantiation or static access by themselves
        // (exact types, all node types not in this set are considered to perform external access)
        private static final Set<Class<?>> NODE_TYPES_WITHOUT_EXTERNAL_ACCESS =
                Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.<Class<?>>asList(
                        ASTAdd.class, ASTAnd.class, ASTAssign.class, ASTBitAnd.class, ASTBitNegate.class, ASTBitOr.class,
                        ASTChain.class, ASTConst.class, ASTDivide.class, ASTEq.class, ASTEval.class, ASTGreater.class,
                        ASTGreaterEq.class, ASTIn.class, ASTInstanceof.class, ASTKeyValue.class, ASTLess.class,
                        ASTLessEq.class, ASTList.class, ASTMap.class, ASTMethod.class, ASTMultiply.class,
                        ASTNegate.class, ASTNot.class, ASTNotEq.class, ASTNotIn.class, ASTOr.class, ASTProject.class,
                        ASTProperty.class, ASTRemainder.class, ASTRootVarRef.class, ASTSelect.class, ASTSelectFirst.class,
                        ASTSelectLast.class, ASTSequence.class, ASTShiftLeft.class, ASTShiftRight.class,
                        ASTSubtract.class, ASTTest.class, ASTThisVarRef.class, ASTUnsignedShiftRight.class,
                        ASTVarRef.class, ASTXor.class)));


        static boolean containsExternalAccess(final String expression) throws Exception {
            final Object parsedExpression = ognl.Ognl.parseExpression(expression);
            if (!(parsedExpression instanceof Node)) {
                throw new IllegalStateException(
                        "Parsed expression is of unexpected type " + parsedExpression.getClass().getName());
            }
            return containsExternalAccess((Node) parsedExpression);
        }


        private static boolean containsExternalAccess(final Node node) {
            final Class<?> nodeType = node.getClass();
            // Maps with a specified class (#@SomeMap@{...}) are instantiated from that class
            if (!NODE_TYPES_WITHOUT_EXTERNAL_ACCESS.contains(nodeType)
                    || (nodeType.equals(ASTMap.class) && node.toString().startsWith("#@"))
                    || (nodeType.equals(ASTProperty.class) && node.jjtGetNumChildren() > 0 && isParamName(node.jjtGetChild(0)))
                    || (nodeType.equals(ASTMethod.class) && PARAM_NAME.equalsIgnoreCase(((ASTMethod) node).getMethodName()))
                    || (nodeType.equals(ASTVarRef.class) && ("#" + PARAM_NAME).equalsIgnoreCase(node.toString()))) {
                return true;
            }
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                if (containsExternalAccess(node.jjtGetChild(i))) {
                    return true;
                }
            }
            return false;
        }


        private static boolean isParamName(final Node node) {
            if (!node.getClass().equals(ASTConst.class)) {
                return false;
            }
            final Object value = ((ASTConst) node).getValue();
            return value instanceof String && PARAM_NAME.equalsIgnoreCase((String) value);
        }


        private ParsedExpressionCheck() {
            super();
        }

    }



    private OGNLExpressionRestrictions() {
        super();
    }

}
