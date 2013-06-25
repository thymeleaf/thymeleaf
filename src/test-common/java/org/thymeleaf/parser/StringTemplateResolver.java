package org.thymeleaf.parser;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.input.ReaderInputStream;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * Utility class for use in test cases.
 * @author guvend
 */
public class StringTemplateResolver implements ITemplateResolver {
    
    protected String resourceName = StringTemplateResolver.class.getName();
    protected String content;
    private String characterEncoding = "utf-8";
    private String templateMode = "XHTML";
    protected boolean cacheable = false;
    private Integer order = null;
    
    // -----
    
    public StringTemplateResolver(String content) {
        this.content = content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public StringTemplateResolver setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public StringTemplateResolver setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        return this;
    }
    
    public StringTemplateResolver setTemplateMode(String templateMode) {
        this.templateMode = templateMode;
        return this;
    }
    
    public StringTemplateResolver setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
        return this;
    }
    
    public StringTemplateResolver setOrder(Integer order) {
        this.order = order;
        return this;
    }
    
    // -----

    protected IResourceResolver getResourceResolver() {
        
        return new IResourceResolver() {

            public InputStream getResourceAsStream(
                TemplateProcessingParameters templateProcessingParameters,
                String resName) {
                return new ReaderInputStream(new StringReader(StringTemplateResolver.this.content));
            }
            
            public String getName() {
                return StringTemplateResolver.this.resourceName + "--ResourceResolver";
            }
        };
    }

    protected ITemplateResolutionValidity getValidity() {
        return new ITemplateResolutionValidity() {

            public boolean isCacheable() {
                return StringTemplateResolver.this.cacheable;
            }
            
            public boolean isCacheStillValid() {
                return true;
            }
        };
    }

    public String getName() {
        return this.resourceName;
    }

    public Integer getOrder() {
        return this.order;
    }

    public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
        return new TemplateResolution(this.resourceName, this.resourceName,
            getResourceResolver(), this.characterEncoding, this.templateMode, getValidity());
    }

    public void initialize() {
        // Nothing to be done
    }
}
