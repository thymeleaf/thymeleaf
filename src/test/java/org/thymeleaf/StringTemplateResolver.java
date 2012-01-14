package org.thymeleaf;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.input.ReaderInputStream;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * Utility class for use in test cases.
 * @author guvend
 */
public class StringTemplateResolver implements ITemplateResolver
{
    private String resourceName = StringTemplateResolver.class.getName();
    private String content;
    private String characterEncoding = "utf-8";
    private TemplateMode templateMode = TemplateMode.XHTML;
    private boolean cacheable = false;
    private Integer order = null;
    
    // -----
    
    public StringTemplateResolver(String content)
    {
        this.content = content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public StringTemplateResolver setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
        return this;
    }

    public StringTemplateResolver setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
        return this;
    }
    
    public StringTemplateResolver setTemplateMode(TemplateMode templateMode)
    {
        this.templateMode = templateMode;
        return this;
    }
    
    public StringTemplateResolver setCacheable(boolean cacheable)
    {
        this.cacheable = cacheable;
        return this;
    }
    
    public StringTemplateResolver setOrder(Integer order)
    {
        this.order = order;
        return this;
    }
    
    // -----

    protected IResourceResolver getResourceResolver()
    {
        return new IResourceResolver() {
            @Override
            public InputStream getResourceAsStream(
                TemplateProcessingParameters templateProcessingParameters,
                String resourceName)
            {
                return new ReaderInputStream(new StringReader(content));
            }
            
            @Override
            public String getName()
            {
                return resourceName + "--ResourceResolver";
            }
        };
    }

    protected ITemplateResolutionValidity getValidity()
    {
        return new ITemplateResolutionValidity() {
            @Override
            public boolean isCacheable()
            {
                return cacheable;
            }
            
            @Override
            public boolean isCacheStillValid()
            {
                return true;
            }
        };
    }

    @Override
    public String getName()
    {
        return resourceName;
    }

    @Override
    public Integer getOrder()
    {
        return order;
    }

    @Override
    public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters)
    {
        return new TemplateResolution(resourceName, resourceName,
            getResourceResolver(), characterEncoding, templateMode, getValidity());
    }

    @Override
    public void initialize()
    {
        // TODO Auto-generated method stub
        
    }
}
