/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.IWebVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.unbescape.uri.UriEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * @since 1.1 (reimplemented in 3.0.0)
 *
 */
public final class LinkExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkExpression.class);
    
    private static final long serialVersionUID = -564516592085017252L;
    
    static final char SELECTOR = '@';
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';

    private static final char URL_TEMPLATE_DELIMITER_PREFIX_CHAR = '{';
    private static final String URL_TEMPLATE_DELIMITER_PREFIX = "{";
    private static final String URL_TEMPLATE_DELIMITER_SUFFIX = "}";

    private static final Pattern LINK_PATTERN = 
        Pattern.compile("^\\s*\\@\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    
    private static final String URL_PARAM_NO_VALUE = "%%%__NO_VALUE__%%%";


    
    private final IStandardExpression base;
    private final AssignationSequence parameters;
    
    
    
    
    public LinkExpression(final IStandardExpression base, final AssignationSequence parameters) {
        super();
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }
    
    
    
    
    public IStandardExpression getBase() {
        return this.base;
    }
    
    public AssignationSequence getParameters() {
        return this.parameters;
    }
    
    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }

    @Override
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(SELECTOR);
        sb.append(SimpleExpression.EXPRESSION_START_CHAR);
        sb.append(this.base);
        if (hasParameters()) {
            sb.append(PARAMS_START_CHAR);
            sb.append(this.parameters.getStringRepresentation());
            sb.append(PARAMS_END_CHAR);
        }
        sb.append(SimpleExpression.EXPRESSION_END_CHAR);
        return sb.toString();
    }

    
    
    
    static LinkExpression parseLink(final String input) {
        
        final Matcher matcher = LINK_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }

        final String content = matcher.group(1);

        if (StringUtils.isEmptyOrWhitespace(content)) {
            return null;
        }
        
        final String trimmedInput = content.trim();
        
        if (trimmedInput.endsWith(String.valueOf(PARAMS_END_CHAR))) {
            
            boolean inLiteral = false;
            int nestParLevel = 0;
            
            for (int i = trimmedInput.length() - 1; i >= 0; i--) {
                
                final char c = trimmedInput.charAt(i);
                
                if (c == TextLiteralExpression.DELIMITER) {
                    
                    if (i == 0 || content.charAt(i - 1) != '\\') {
                        inLiteral = !inLiteral;
                    }
                
                } else if (!inLiteral && c == PARAMS_END_CHAR) {
                    
                    nestParLevel++;
                        
                } else if (!inLiteral && c == PARAMS_START_CHAR) {
                    
                    nestParLevel--;
                    
                    if (nestParLevel < 0) {
                        return null;
                    }
                    
                    if (nestParLevel == 0) {
                        
                        if (i == 0) {
                            // It was not a parameter specification, but a base URL surrounded by parentheses! 
                            final Expression baseExpr = parseBaseDefaultAsLiteral(trimmedInput);
                            if (baseExpr == null) {
                                return null;
                            }
                            return new LinkExpression(baseExpr, null);
                        }
                        
                        final String base = trimmedInput.substring(0, i).trim();
                        final String parameters = trimmedInput.substring(i + 1, trimmedInput.length() - 1).trim();

                        final Expression baseExpr = parseBaseDefaultAsLiteral(base);
                        if (baseExpr == null) {
                            return null;
                        }
                        
                        final AssignationSequence parametersAssigSeq = 
                                AssignationUtils.internalParseAssignationSequence(
                                        parameters, true /* allow parameters without value or equals sign */);
                        if (parametersAssigSeq == null) {
                            return null;
                        }
                        
                        return new LinkExpression(baseExpr, parametersAssigSeq);
                        
                    }
                    
                }
            }

            return null;
            
        }

        final Expression baseExpr = parseBaseDefaultAsLiteral(trimmedInput);
        if (baseExpr == null) {
            return null;
        }

        return new LinkExpression(baseExpr, null);
        
    }





    private static Expression parseBaseDefaultAsLiteral(final String base) {

        if (StringUtils.isEmptyOrWhitespace(base)) {
            return null;
        }

        final Expression expr = Expression.parse(base);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(base));
        }
        return expr;

    }

    
    
    


    static Object executeLink(
            final IProcessingContext processingContext, final LinkExpression expression,
            final StandardExpressionExecutionContext expContext) {

        /*
         *  DEVELOPMENT NOTE: Reasons why Spring's RequestDataValueProcessor#processUrl(...) is not applied here
         *                    instead of at th:href and th:src.
         *
         *      1. Reduce complexity, as Dialects would need to add one more execution attribute for a wrapper
         *         able to apply such post-processor.
         *      2. Avoid link expressions in "th:action" be applied "processUrl(...)" and then "processAction(...)",
         *         which would break compatiliby with Spring's FormTag class.
         *         - The only way to avoid this would be to mess around with StandardExpressionExecutionContexts,
         *           which would mean much more complexity.
         *      3. Avoid that URLs that are not link expressions (or not only) but are anyway expressed with th:href
         *         or th:src end up not being processed.
         */

        final ITextRepository textRepository = processingContext.getConfiguration().getTextRepository();

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final IStandardExpression baseExpression = expression.getBase();
        Object base = baseExpression.execute(processingContext, expContext);

        base = LiteralValue.unwrap(base);
        if (base != null && !(base instanceof String)) {
            base = base.toString();
        }
        if (base == null || StringUtils.isEmptyOrWhitespace((String) base)) {
            base = "";
        }

        final boolean linkBaseAbsolute = isLinkBaseAbsolute((String)base);
        final boolean linkBaseContextRelative = !linkBaseAbsolute && isLinkBaseContextRelative((String)base);
        final boolean linkBaseServerRelative = !linkBaseAbsolute && !linkBaseContextRelative && isLinkBaseServerRelative((String) base);
        final boolean linkBaseRelative = !linkBaseAbsolute && !linkBaseContextRelative && !linkBaseServerRelative;

        if (!processingContext.isWeb() && linkBaseContextRelative) {
            throw new TemplateProcessingException(
                    "Link base \"" + base + "\" cannot be context relative (/...) unless the context " +
                    "used for executing the engine implements the " + IWebContext.class.getName() + " interface");
        }


        /*
         * Resolve the parameters from the expression into a LinkParameters object.
         * Note the parameters variable might be null if there are no parameters
         */
        final LinkParameters parameters = resolveParameters(processingContext, expression, expContext);


        /*
         * Compute URL fragments (selectors after '#') so that they can be output at the end of
         * the URL, after parameters.
         */
        final int hashPosition = findCharInSequence((String) base, '#');


        /*
         * Compute whether we might have variable templates (e.g. Spring Path Variables) inside this link base
         * that we might need to resolve afterwards
         */
        final boolean mightHaveVariableTemplates =
                findCharInSequence((String)base, URL_TEMPLATE_DELIMITER_PREFIX_CHAR) >= 0;


        /*
         * Precompute the context path, so that it can be afterwards used for determining if it has to be added to the
         * URL (in case it is context-relative) or not
         */
        final String contextPath;
        if (linkBaseContextRelative) {
            // If it is context-relative, it has to be a web context
            final IWebVariablesMap webVariablesMap = (IWebVariablesMap) processingContext.getVariablesMap();
            final HttpServletRequest request = webVariablesMap.getRequest();
            contextPath = request.getContextPath();
        } else {
            contextPath = null;
        }
        final boolean contextPathEmpty = contextPath != null && contextPath.length() > 0 && !contextPath.equals("/");


        /*
         * SHORTCUT - just before starting to work with StringBuilders, and in the case that we know: 1. That the URL is
         *            absolute, relative or context-relative with no context; 2. That there are no parameters; and
         *            3. That there are no URL fragments -> then just return the base URL String without further
         *            processing (except HttpServletResponse-encoding, of course...)
         */
        if (contextPathEmpty && !linkBaseServerRelative &&
                (parameters == null || parameters.size() == 0) && hashPosition < 0 && !mightHaveVariableTemplates) {

            if (processingContext.isWeb()) {
                final IWebVariablesMap webVariablesMap = (IWebVariablesMap) processingContext.getVariablesMap();
                final HttpServletResponse response = webVariablesMap.getResponse();
                return (response != null? response.encodeURL((String) base) : base);
            }
            // Processing context is not web, no need to HttpServletResponse-encode
            return base;

        }


        /*
         * Build the StringBuilder that will be used as a base for all URL-related operations from now on: variable
         * templates, parameters, URL fragments...
         */
        StringBuilder linkBase = new StringBuilder((String) base);


        /*
         * Compute URL fragments (selectors after '#') so that they can be output at the end of
         * the URL, after parameters.
         */
        String urlFragment = "";
        // If hash position == 0 we will not consider it as marking an
        // URL fragment.
        if (hashPosition > 0) {
            // URL fragment String will include the # sign
            urlFragment = linkBase.substring(hashPosition);
            linkBase.delete(hashPosition, linkBase.length());
        }


        /*
         * Replace those variable templates that might appear referenced in the path itself, as for example, Spring
         * "Path Variables" (e.g. '/something/{variable}/othersomething')
         */
        if (mightHaveVariableTemplates) {
            linkBase = replaceTemplateParamsInBase(textRepository, linkBase, parameters);
        }


        /*
         * Process parameters (those that have not already been processed as a result of replacing template
         * parameters in base).
         */
        if (parameters != null && parameters.size() > 0) {

            // Build the parameters query. The result will always start with '&'
            final StringBuilder parametersBuilder = parameters.processAllRemainingParametersAsQueryParams();

            // If there is no '?' in linkBase, we have to replace with first '&' with '?'
            if (findCharInSequence(linkBase,'?') < 0) {
                parametersBuilder.replace(0, 1, "?");
            }

            // Parameters have been processed, so just add them to the linkBase
            linkBase.append(parametersBuilder);

        }


        /*
         * Once parameters have been added (if there are parameters), we can add the URL fragment
         */
        if (urlFragment.length() > 0) {
            linkBase.append(urlFragment);
        }


        /*
         * If link base is server relative, we will delete now the leading '~' character so that it starts with '/'
         */
        if (linkBaseServerRelative) {
            linkBase.delete(0,1);
        }


        /*
         * Context is not web: URLs can only be absolute or server-relative and we will not be doing any
         * HttpServletRespons#encodeURL(...) because there is no response object, of course...
         */
        if (!processingContext.isWeb()) {
            return linkBase.toString();
        }
        

        /*
         * Context is web 
         */
        
        final IWebVariablesMap webVariablesMap = (IWebVariablesMap) processingContext.getVariablesMap();
        final HttpServletResponse response = webVariablesMap.getResponse();

        if (linkBaseContextRelative && !contextPathEmpty) {
            // Add the application's context path at the beginning
            linkBase.insert(0, contextPath);
        }

        return (response != null? response.encodeURL(linkBase.toString()) : linkBase.toString());
        
    }

    


    private static int findCharInSequence(final CharSequence seq, final char character) {
        int n = seq.length();
        while (n-- != 0) {
            final char c = seq.charAt(n);
            if (c == character) {
                return n;
            }
        }
        return -1;
    }



    
    private static boolean isLinkBaseAbsolute(final CharSequence linkBase) {
        final int linkBaseLen = linkBase.length();
        if (linkBaseLen < 2) {
            return false;
        }
        final char c0 = linkBase.charAt(0);
        if (c0 == 'm' || c0 == 'M') {
            // Let's check for "mailto:"
            if (linkBase.length() >= 7 &&
                    Character.toLowerCase(linkBase.charAt(1)) == 'a' &&
                    Character.toLowerCase(linkBase.charAt(2)) == 'i' &&
                    Character.toLowerCase(linkBase.charAt(3)) == 'l' &&
                    Character.toLowerCase(linkBase.charAt(4)) == 't' &&
                    Character.toLowerCase(linkBase.charAt(5)) == 'o' &&
                    Character.toLowerCase(linkBase.charAt(6)) == ':') {
                return true;
            }
        } else if (c0 == '/' || c0 == '/') {
            return linkBase.charAt(1) == '/'; // It starts with '//' -> true, any other '/x' -> false
        }
        for (int i = 0; i < (linkBaseLen - 2); i++) {
            // Let's try to find the '://' sequence anywhere in the base --> true
            if (linkBase.charAt(i) == ':' && linkBase.charAt(i + 1) == '/' && linkBase.charAt(i + 2) == '/') {
                return true;
            }
        }
        return false;
    }


    private static boolean isLinkBaseContextRelative(final CharSequence linkBase) {
        // For this to be true, it should start with '/', but not with '//'
        if (linkBase.length() == 0 || linkBase.charAt(0) != '/') {
            return false;
        }
        return linkBase.length() == 1 || linkBase.charAt(1) != '/';
    }

    
    private static boolean isLinkBaseServerRelative(final CharSequence linkBase) {
        // For this to be true, it should start with '~/'
        return (linkBase.length() >= 2 && linkBase.charAt(0) == '~' && linkBase.charAt(1) == '/');
    }
    
    
    private static LinkParameters resolveParameters(
            final IProcessingContext processingContext, final LinkExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (!expression.hasParameters()) {
            return null;
        }

        final LinkParameters parameters = new LinkParameters();

        final List<Assignation> assignationValues = expression.getParameters().getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignationValue = assignationValues.get(i);
            
            final IStandardExpression parameterNameExpr = assignationValue.getLeft();
            final IStandardExpression parameterValueExpr = assignationValue.getRight();

            // We know parameterNameExpr cannot be null (the Assignation class would not allow it)
            final Object parameterNameValue = parameterNameExpr.execute(processingContext, expContext);
            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            if (StringUtils.isEmptyOrWhitespace(parameterName)) {
                throw new TemplateProcessingException(
                        "Parameters in link expression \"" + expression.getStringRepresentation() + "\" are " +
                        "incorrect: parameter name expression \"" + parameterNameExpr.getStringRepresentation() +
                        "\" evaluated as null or empty string.");
            }

            final Object parameterValue;
            if (parameterValueExpr == null) {
                // If this is null, it means we want to render the parameter without a value and
                // also without an equals sign.
                parameterValue = URL_PARAM_NO_VALUE;
            } else {
                final Object value = parameterValueExpr.execute(processingContext, expContext);
                if (value == null) {
                    // Not the same as not specifying a value!
                    parameterValue = "";
                } else {
                    parameterValue = LiteralValue.unwrap(value);
                }
            }

            parameters.addSimpleParameter(parameterName, parameterValue);
            
        }
        return parameters;
        
    }

    
    
    




    static StringBuilder replaceTemplateParamsInBase(
            final ITextRepository textRepository, final StringBuilder linkBase, final LinkParameters parameters) {

        /*
         * If parameters is null, there's nothing to do
         */
        if (parameters == null) {
            return linkBase;
        }

        /*
         * Search {templateVar} in linkBase, and replace with value.
         * Parameters can be multivalued, in which case they will be comma-separated.
         * Parameter values will be URL-path-encoded. If there is a '?' char, only parameter values before this
         * char will be URL-path-encoded, whereas parameters after it will be URL-query-encoded.
         */

        final int questionMarkPosition = findCharInSequence(linkBase, '?');

        int i = parameters.size();
        while (i-- != 0) {

            // We will be (potentially) removing parameters as we process them, and by reverse iterating we will
            // be able to modify the parameter arrays without this affecting iteration

            final String paramName = parameters.getParameterName(i);

            // We use the text repository in order to avoid the unnecessary creation of too many instances of the same string
            final String template =
                    textRepository.getText(URL_TEMPLATE_DELIMITER_PREFIX, paramName, URL_TEMPLATE_DELIMITER_SUFFIX);

            final int templateIndex = linkBase.indexOf(template); // not great, because StringBuilder.indexOf ends up calling template.toCharArray(), but...

            if (templateIndex < 0) {
                // This parameter is not one of those used in path variables
                continue;
            }

            // Compute the replacement (unescaped!)
            final String templateReplacement = parameters.popParameterValueAsUnescapedVariableTemplate(i);
            final int templateReplacementLen = templateReplacement.length();

            // We will now use a the StringBuilder itself for replacing all appearances of the variable template in
            // the link base. Note we do this instead of using String#replace() because String#replace internally uses
            // pattern matching and is very slow :-(
            final int templateLen = template.length();
            int start = templateIndex;
            while (start > -1) {
                // Depending on whether the template appeared before or after the ?, we will apply different escaping
                final String escapedReplacement =
                        (start < questionMarkPosition?
                                UriEscape.escapeUriPath(templateReplacement) : UriEscape.escapeUriQueryParam(templateReplacement));
                linkBase.replace(start, start + templateLen, escapedReplacement);
                if (findCharInSequence(linkBase, URL_TEMPLATE_DELIMITER_PREFIX_CHAR) < 0) {
                    // Just trying to save an additional StringBuilder#indexOfU() -which provokes a
                    // template.toCharArray()-- in the most common case: only one variable template for a
                    // variable that is not multivalued.
                    return linkBase;
                }
                start = linkBase.indexOf(template, start + templateReplacementLen);
            }

        }

        return linkBase;

    }






    private static final class LinkParameters {

        private static final int DEFAULT_PARAMETERS_SIZE = 2;

        private int parameterSize = 0;
        private String[] parameterNames = null;
        private Object[] parameterValues = null;


        LinkParameters() {
            super();
        }


        int size() {
            return this.parameterSize;
        }

        String getParameterName(final int pos) {
            return this.parameterNames[pos];
        }


        /*
         * This method will return a String containing all the values for a specific parameter, separated with commas
         * and suitable therefore to be used as variable template (path variables) replacements
         */
        String popParameterValueAsUnescapedVariableTemplate(final int pos) {
            // Get the value
            final Object value = this.parameterValues[pos];
            // Remove the entry, moving all array positions since this one
            if (pos + 1 < this.parameterSize) {
                System.arraycopy(this.parameterNames, pos + 1, this.parameterNames, pos, (this.parameterSize - (pos + 1)));
                System.arraycopy(this.parameterValues, pos + 1, this.parameterValues, pos, (this.parameterSize - (pos + 1)));
                this.parameterSize--;
            }
            // If null or NO_VALUE, empty String
            if (value == null || URL_PARAM_NO_VALUE.equals(value)) { // Values can never be null, but anyway
                return "";
            }
            // If it is not multivalued (e.g. non-List) simply escape and return
            if (!(value instanceof List<?>)) {
                return value.toString();
            }
            // It is multivalued, so iterate and escape each item (no need to escape the comma separating them, it's an allowed char)
            final List<?> values = (List<?>)value;
            final int valuesLen = values.size();
            final StringBuilder strBuilder = new StringBuilder(valuesLen * 16);
            for (int i = 0; i < valuesLen; i++) {
                final Object valueItem = values.get(i);
                if (!URL_PARAM_NO_VALUE.equals(valueItem)) {
                    if (strBuilder.length() > 0) {
                        strBuilder.append(',');
                    }
                    strBuilder.append(valueItem == null? "" : valueItem.toString());
                }
            }
            return strBuilder.toString();
        }



        StringBuilder processAllRemainingParametersAsQueryParams() {

            final StringBuilder strBuilder = new StringBuilder(this.parameterSize * 16);
            for (int i = 0; i < this.parameterSize; i++) {

                final Object value = this.parameterValues[i];

                if (value == null || URL_PARAM_NO_VALUE.equals(value)) {
                    strBuilder.append('&');
                    strBuilder.append(UriEscape.escapeUriQueryParam(this.parameterNames[i]));
                    continue;
                }

                if (!(value instanceof List<?>)) {
                    strBuilder.append('&');
                    strBuilder.append(UriEscape.escapeUriQueryParam(this.parameterNames[i]));
                    strBuilder.append('=');
                    strBuilder.append(UriEscape.escapeUriQueryParam(value.toString())); // we know it's not null
                    continue;
                }

                // It is multivalued, so iterate and process each value
                final List<?> values = (List<?>)value;
                final int valuesLen = values.size();
                for (int j = 0; j < valuesLen; j++) {
                    final Object valueItem = values.get(j);
                    strBuilder.append('&');
                    strBuilder.append(UriEscape.escapeUriQueryParam(this.parameterNames[i]));
                    if (!URL_PARAM_NO_VALUE.equals(valueItem)) {
                        strBuilder.append('=');
                        strBuilder.append(valueItem == null ? "" : UriEscape.escapeUriQueryParam(value.toString()));
                    }
                }

            }
            return strBuilder;

        }


        void addSimpleParameter(final String parameterName, final Object parameterValue) {

            int n = this.parameterSize;
            while (n-- != 0) {
                if (this.parameterNames[n].equalsIgnoreCase(parameterName)) {
                    addSimpleParameter(n, true, parameterName, parameterValue);
                    return;
                }
            }

            if (this.parameterNames == null || this.parameterSize == this.parameterNames.length) {
                // We need to grow the container structures
                final String[] newParameterNames = new String[this.parameterSize + DEFAULT_PARAMETERS_SIZE];
                final Object[] newParameterValues = new Object[this.parameterSize + DEFAULT_PARAMETERS_SIZE];
                if (this.parameterNames != null) {
                    System.arraycopy(this.parameterNames, 0, newParameterNames, 0, this.parameterSize);
                    System.arraycopy(this.parameterValues, 0, newParameterValues, 0, this.parameterSize);
                }
                this.parameterNames = newParameterNames;
                this.parameterValues = newParameterValues;
            }

            addSimpleParameter(this.parameterSize, false, parameterName, parameterValue);
            this.parameterSize++;

        }


        private void addSimpleParameter(
                final int pos, final boolean append, final String parameterName, final Object parameterValue) {

            if (!append) {
                this.parameterNames[pos] = parameterName;
                this.parameterValues[pos] = processParameterValue(parameterValue); // -> arraylist or plain object
                return;
            }

            // We are appending to an existing value

            final Object currentValue = this.parameterValues[pos];

            if (currentValue == null || !(currentValue instanceof List<?>)) {
                this.parameterValues[pos] = new ArrayList<Object>(2);
                ((List<Object>)this.parameterValues[pos]).add(currentValue);
            }

            // No need to perform any kind of parameter value processing: we know its a mutable arraylist
            ((List<Object>)this.parameterValues[pos]).add(parameterValue);

        }


        private static Object processParameterValue(final Object parameterValue) {
            // After calling this, all parameter values that are either arrays or iterables (e.g. collections) will
            // be converted to a mutable ArrayList. All parameter values that are neither arrays nor iterables will
            // be left unchanged. That should allow us save a lot of arraylists for single-valued parameters (which
            // are the vast majority).

            if (parameterValue == null) {
                return null;
            }


            if (parameterValue instanceof Iterable<?>) {

                if (parameterValue instanceof List<?>) {
                    // faster than iterating as a generic Iterable<?>
                    return new ArrayList<Object>((List<?>) parameterValue);
                }
                if (parameterValue instanceof Set<?>) {
                    // faster than iterating as a generic Iterable<?>
                    return new ArrayList<Object>((Set<?>)parameterValue);
                }

                final List<Object> result = new ArrayList<Object>(4);
                for (final Object obj : (Iterable<?>) parameterValue) {
                    result.add(obj);
                }
                return result;

            }

            if (parameterValue.getClass().isArray()){

                final List<Object> result = new ArrayList<Object>(4);
                if (parameterValue instanceof byte[]) {
                    for (final byte obj : (byte[]) parameterValue) {
                        result.add(Byte.valueOf(obj));
                    }
                } else if (parameterValue instanceof short[]) {
                    for (final short obj : (short[]) parameterValue) {
                        result.add(Short.valueOf(obj));
                    }
                } else if (parameterValue instanceof int[]) {
                    for (final int obj : (int[]) parameterValue) {
                        result.add(Integer.valueOf(obj));
                    }
                } else if (parameterValue instanceof long[]) {
                    for (final long obj : (long[]) parameterValue) {
                        result.add(Long.valueOf(obj));
                    }
                } else if (parameterValue instanceof float[]) {
                    for (final float obj : (float[]) parameterValue) {
                        result.add(Float.valueOf(obj));
                    }
                } else if (parameterValue instanceof double[]) {
                    for (final double obj : (double[]) parameterValue) {
                        result.add(Double.valueOf(obj));
                    }
                } else if (parameterValue instanceof boolean[]) {
                    for (final boolean obj : (boolean[]) parameterValue) {
                        result.add(Boolean.valueOf(obj));
                    }
                } else if (parameterValue instanceof char[]) {
                    for (final char obj : (char[]) parameterValue) {
                        result.add(Character.valueOf(obj));
                    }
                } else {
                    final Object[] objParameterValue = (Object[]) parameterValue;
                    Collections.addAll(result, objParameterValue);
                }
                return result;

            }

            // Just return the parameter value object - no list wrapper to be built
            return parameterValue;

        }


    }


}
