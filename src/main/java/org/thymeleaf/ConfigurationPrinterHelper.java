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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class ConfigurationPrinterHelper {

    public static final String CONFIGURATION_LOGGER_NAME = TemplateEngine.class.getName() + ".CONFIG";

    private static final Logger configLogger = LoggerFactory.getLogger(CONFIGURATION_LOGGER_NAME);

    
    
    
    
    
    
    static void printConfiguration(
            final Set<DialectConfiguration> dialectConfigurations, final Map<String,Boolean> lenienciesByPrefix, 
            final Set<ITemplateResolver> templateResolvers, final Set<IMessageResolver> messageResolvers, 
            final int parsedTemplateCacheSize) {

        final ConfigLogBuilder logBuilder = new ConfigLogBuilder();
        
        logBuilder.line("[THYMELEAF] TEMPLATE ENGINE CONFIGURATION:");
        logBuilder.line("[THYMELEAF] * Parsed template cache size: {}", Integer.valueOf(parsedTemplateCacheSize));
        logBuilder.line("[THYMELEAF] * Template resolvers (in order):");
        for (final ITemplateResolver templateResolver : templateResolvers) {
            if (templateResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", templateResolver.getOrder(), templateResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", templateResolver.getName());
            }
        }
        logBuilder.line("[THYMELEAF] * Message resolvers (in order):");
        for (final IMessageResolver messageResolver : messageResolvers) {
            if (messageResolver.getOrder() != null) {
                logBuilder.line("[THYMELEAF]     * [{}] {}", messageResolver.getOrder(), messageResolver.getName());
            } else{
                logBuilder.line("[THYMELEAF]     * {}", messageResolver.getName());
            }
        }
        
        int dialectIndex = 1;
        final Integer totalDialects = Integer.valueOf(dialectConfigurations.size());
        
        for (final DialectConfiguration dialectConfiguration : dialectConfigurations) {
        
            final IDialect dialect = dialectConfiguration.getDialect();
            
            if (totalDialects.intValue() > 1) {
                logBuilder.line("[THYMELEAF] * Dialect [{} of {}]: {}", new Object[] {Integer.valueOf(dialectIndex), totalDialects, dialect.getClass().getName()});
            } else {
                logBuilder.line("[THYMELEAF] * Dialect: {}", dialect.getClass().getName());
            }
            
            logBuilder.line("[THYMELEAF]     * Prefix: \"{}\"", (dialectConfiguration.getPrefix() != null? dialectConfiguration.getPrefix() : "(none)"));

            if (configLogger.isDebugEnabled()) {
                printDebugConfiguration(logBuilder, dialectConfiguration);
            }
            
            dialectIndex++;
            
        }

        if (totalDialects.intValue() > 1) {
            logBuilder.line("[THYMELEAF] * Leniencies by prefix:");
            for (final Map.Entry<String,Boolean> lenienciesByPrefixEntry : lenienciesByPrefix.entrySet()) {
                logBuilder.line("[THYMELEAF]     * \"{}\": {}", lenienciesByPrefixEntry.getKey(), (lenienciesByPrefixEntry.getValue().booleanValue()? "LENIENT" : "NON-LENIENT"));
            }
        }

        logBuilder.end("[THYMELEAF] TEMPLATE ENGINE CONFIGURED OK");

        /*
         * The following condition makes sense because contents in each case will differ a lot.
         */
        if (configLogger.isDebugEnabled()) {
            configLogger.debug(logBuilder.toString());
        } else if (configLogger.isInfoEnabled()) {
            configLogger.info(logBuilder.toString());
        }
        
    }
    
    
    
    
    private static void printDebugConfiguration(
            final ConfigLogBuilder logBuilder, final DialectConfiguration dialectConfiguration) {

        
//        final Map<String,Map<TagNameProcessorMatcher,ITagProcessor>> tagProcessorsByTagName =
//            dialectConfiguration.unsafeGetTagProcessorsByTagName();
//        final Map<String,Map<AttributeNameProcessorMatcher,IAttrProcessor>> attrProcessorsByAttrName =
//            dialectConfiguration.unsafeGetAttrProcessorsByAttrName();
//        final Map<String,Object> executionAttributes = dialectConfiguration.getExecutionAttributes();
//        final boolean lenient = dialectConfiguration.isLenient();
//        final Set<IDocTypeResolutionEntry> docTypeResolutionEntries = dialectConfiguration.getDialect().getDocTypeResolutionEntries();
//        final Set<IDocTypeTranslation> docTypeTranslations = dialectConfiguration.getDialect().getDocTypeTranslations();
//        
//        
//        final Map<String,Map<TagNameProcessorMatcher,ITagProcessor>> tagProcessors = new LinkedHashMap<String,Map<TagNameProcessorMatcher,ITagProcessor>>();
//        final List<String> tagNames = new ArrayList<String>(tagProcessorsByTagName.keySet());
//        Collections.sort(tagNames);
//        for (final String tagName : tagNames) {
//            tagProcessors.put(tagName, tagProcessorsByTagName.get(tagName));
//        }
//        
//        final Map<String,Map<AttributeNameProcessorMatcher,IAttrProcessor>> attrProcessors = new LinkedHashMap<String,Map<AttributeNameProcessorMatcher,IAttrProcessor>>();
//        final List<String> attrNames = new ArrayList<String>(attrProcessorsByAttrName.keySet());
//        Collections.sort(attrNames);
//        for (final String attrName : attrNames) {
//            attrProcessors.put(attrName, attrProcessorsByAttrName.get(attrName));
//        }
//        
//        logBuilder.line("[THYMELEAF]     * Lenient: {}", Boolean.valueOf(lenient));
//        if (!tagProcessors.isEmpty()) {
//            logBuilder.line("[THYMELEAF]     * Tag Processors:");
//            for (final Map.Entry<String,Map<TagNameProcessorMatcher,ITagProcessor>> tagApplicabilityEntry : tagProcessors.entrySet()) {
//                final String tagName = tagApplicabilityEntry.getKey();
//                for (final Map.Entry<TagNameProcessorMatcher,ITagProcessor> tagProcessorEntry : tagApplicabilityEntry.getValue().entrySet()) {
//                    final TagNameProcessorMatcher tagApplicability = tagProcessorEntry.getKey();
//                    final ITagProcessor tagProcessor = tagProcessorEntry.getValue();
//                    if (tagApplicability.hasFilter()) {
//                        logBuilder.line("[THYMELEAF]         * \"{}[{}]\" : {}", new Object[] {tagName, tagApplicability.getFilterStringRepresentation(), tagProcessor.getClass().getName()});
//                    } else {
//                        logBuilder.line("[THYMELEAF]         * \"{}\" : {}", new Object[] {tagName, tagProcessor.getClass().getName()});
//                    }
//                }
//            }
//        }
//        if (!attrProcessors.isEmpty()) {
//            logBuilder.line("[THYMELEAF]     * Attribute Processors with precedence:");
//            for (final Map.Entry<String,Map<AttributeNameProcessorMatcher,IAttrProcessor>> attrApplicabilityEntry : attrProcessors.entrySet()) {
//                final String attrName = attrApplicabilityEntry.getKey();
//                for (final Map.Entry<AttributeNameProcessorMatcher,IAttrProcessor> attrProcessorEntry : attrApplicabilityEntry.getValue().entrySet()) {
//                    final AttributeNameProcessorMatcher attrApplicability = attrProcessorEntry.getKey();
//                    final IAttrProcessor attrProcessor = attrProcessorEntry.getValue();
//                    if (attrApplicability.hasFilter()) {
//                        logBuilder.line("[THYMELEAF]         * \"{}[{}]\" [{}]: {}", new Object[] {attrName, attrApplicability.getFilterStringRepresentation(), attrProcessor.getPrecedence(), attrProcessor.getClass().getName()});
//                    } else {
//                        logBuilder.line("[THYMELEAF]         * \"{}\" [{}]: {}", new Object[] {attrName, attrProcessor.getPrecedence(), attrProcessor.getClass().getName()});
//                    }
//                }
//            }
//        }
//        if (!executionAttributes.isEmpty()) {
//            logBuilder.line("[THYMELEAF]     * Execution Attributes:");
//            for (final Map.Entry<String,Object> executionAttributesEntry : executionAttributes.entrySet()) {
//                final String attrName = executionAttributesEntry.getKey();
//                final String attrValue = 
//                    (executionAttributesEntry.getValue() == null? null : executionAttributesEntry.getValue().toString());
//                logBuilder.line("[THYMELEAF]         * \"{}\": {}", new Object[] {attrName, attrValue});
//            }
//        }
//        logBuilder.line("[THYMELEAF]     * DOCTYPE translations:");
//        for (final IDocTypeTranslation translation : docTypeTranslations) {
//            logBuilder.line("[THYMELEAF]         * DOCTYPE Translation:");
//            if (translation.getSourcePublicID().isNone()) {
//                logBuilder.line("[THYMELEAF]             * Source: SYSTEM \"{}\"", 
//                        new Object[] {
//                            (translation.getSourceSystemID().isAny()? "*" : translation.getSourceSystemID())});
//            } else {
//                logBuilder.line("[THYMELEAF]             * Source: PUBLIC \"{}\" \"{}\"", 
//                        new Object[] {
//                            (translation.getSourcePublicID().isAny()? "*" : translation.getSourcePublicID()), 
//                            (translation.getSourceSystemID().isAny()? "*" : translation.getSourceSystemID())});
//            }
//            if (translation.getTargetPublicID().isNone()) {
//                logBuilder.line("[THYMELEAF]             * Target: SYSTEM \"{}\"", 
//                        new Object[] {
//                            (translation.getTargetSystemID().isAny()? "*" : translation.getTargetSystemID())});
//            } else {
//                logBuilder.line("[THYMELEAF]             * Target: PUBLIC \"{}\" \"{}\"", 
//                        new Object[] {
//                            (translation.getTargetPublicID().isAny()? "*" : translation.getTargetPublicID()), 
//                            (translation.getTargetSystemID().isAny()? "*" : translation.getTargetSystemID())});
//            }
//        }
//        logBuilder.line("[THYMELEAF]     * DOCTYPE resolution entries:");
//        for (final IDocTypeResolutionEntry entry : docTypeResolutionEntries) {
//            if (entry.getPublicID().isNone()) {
//                logBuilder.line("[THYMELEAF]         * SYSTEM \"{}\"", 
//                        new Object[] {
//                            (entry.getSystemID().isAny()? "*" : entry.getSystemID())});
//            } else {
//                logBuilder.line("[THYMELEAF]         * PUBLIC \"{}\" \"{}\"", 
//                        new Object[] {
//                            (entry.getPublicID().isAny()? "*" : entry.getPublicID()), 
//                            (entry.getSystemID().isAny()? "*" : entry.getSystemID())});
//            }
//        }

    }
    
    
    
    
    
    private static class ConfigLogBuilder {
        
        private static final String PLACEHOLDER = "\\{\\}";
        private final StringBuilder strBuilder;
        
        protected ConfigLogBuilder() {
            super();
            this.strBuilder = new StringBuilder();
        }
        
        protected void end(final String line) {
            this.strBuilder.append(line);
        }
        
        protected void line(final String line) {
            this.strBuilder.append(line + "\n");
        }
        
        protected void line(final String line, final Object p1) {
            this.strBuilder.append(replace(line, p1) + "\n");
        }
        
        protected void line(final String line, final Object p1, final Object p2) {
            this.strBuilder.append(replace(replace(line, p1), p2) + "\n");
        }
        
        protected void line(final String line, final Object[] pArr) {
            String newLine = line;
            for (int i = 0; i < pArr.length; i++) {
                newLine = replace(newLine, pArr[i]);
            }
            this.strBuilder.append(newLine + "\n");
        }
        
        @Override
        public String toString() {
            return this.strBuilder.toString();
        }
        
        private String replace(final String str, final Object replacement) {
            return str.replaceFirst(PLACEHOLDER, param(replacement));
        }
        
        private String param(final Object p) {
            if (p == null) {
                return null;
            }
            return p.toString();
        }
        
    }
    
    
    
    
    private ConfigurationPrinterHelper() {
        super();
    }
    
}
