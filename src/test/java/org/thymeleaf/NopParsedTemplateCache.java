package org.thymeleaf;


/**
 * Utility class for use in test cases.
 * @author guvend
 */
public class NopParsedTemplateCache implements IParsedTemplateCache
{
    @Override
    public Template getParsedTemplate(String templateName)
    {
        return null;
    }

    @Override
    public void putParsedTemplate(Template parsedTemplate)
    {
        // nop
    }

    @Override
    public void clearTemplateCache()
    {
        // nop
    }

    @Override
    public void clearTemplateCacheFor(String templateName)
    {
        // nop
    }
}
