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
package org.thymeleaf.spring5.context;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.context.Theme;
import org.springframework.validation.Errors;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;

/**
 * <p>
 *   This interface is meant to abstract a Spring {@code RequestContext}, without the client code
 *   needing to know if it is a Spring WebMVC or Spring WebFlux implementation of this
 *   {@code RequestContext}.
 * </p>
 *
 * @see org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext
 * @see SpringWebFluxThymeleafRequestContext
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface IThymeleafRequestContext {


    public MessageSource getMessageSource();

    public Map<String, Object> getModel();

    public Locale getLocale();
    public TimeZone getTimeZone();
    public void changeLocale(Locale locale);
    public void changeLocale(Locale locale, TimeZone timeZone);

    public void setDefaultHtmlEscape(boolean defaultHtmlEscape);
    public boolean isDefaultHtmlEscape();
    public Boolean getDefaultHtmlEscape();

    public String getContextPath();
    public String getContextUrl(String relativeUrl);
    public String getContextUrl(String relativeUrl, Map<String, ?> params);
    public String getRequestPath();
    public String getQueryString();

    public String getMessage(String code, String defaultMessage);
    public String getMessage(String code, Object[] args, String defaultMessage);
    public String getMessage(String code, List<?> args, String defaultMessage);
    public String getMessage(String code, Object[] args, String defaultMessage, boolean htmlEscape);
    public String getMessage(String code) throws NoSuchMessageException;
    public String getMessage(String code, Object[] args) throws NoSuchMessageException;
    public String getMessage(String code, List<?> args) throws NoSuchMessageException;
    public String getMessage(String code, Object[] args, boolean htmlEscape) throws NoSuchMessageException;
    public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException;
    public String getMessage(MessageSourceResolvable resolvable, boolean htmlEscape) throws NoSuchMessageException;

    public Optional<Errors> getErrors(String name);
    public Optional<Errors> getErrors(String name, boolean htmlEscape);

    public Theme getTheme();

    public IThymeleafRequestDataValueProcessor getRequestDataValueProcessor();

    public IThymeleafBindStatus getBindStatus(String path) throws IllegalStateException;
    public IThymeleafBindStatus getBindStatus(String path, boolean htmlEscape) throws IllegalStateException;


}
