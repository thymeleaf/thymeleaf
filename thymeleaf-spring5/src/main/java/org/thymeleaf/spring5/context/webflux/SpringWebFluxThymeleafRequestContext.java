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
package org.thymeleaf.spring5.context.webflux;

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
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.IThymeleafRequestDataValueProcessor;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of the {@link IThymeleafRequestContext} interface, meant to wrap a Spring
 *   {@link RequestContext} object.
 * </p>
 *
 * @see RequestContext
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringWebFluxThymeleafRequestContext implements IThymeleafRequestContext {

    private final RequestContext requestContext;
    private final ServerWebExchange serverWebExchange;
    private final SpringWebFluxThymeleafRequestDataValueProcessor thymeleafRequestDataValueProcessor;


    public SpringWebFluxThymeleafRequestContext(
            final RequestContext requestContext, final ServerWebExchange serverWebExchange) {
        super();
        Validate.notNull(requestContext, "Spring WebFlux RequestContext cannot be null");
        Validate.notNull(serverWebExchange, "Server Web Exchange cannot be null");
        this.requestContext = requestContext;
        this.serverWebExchange = serverWebExchange;
        this.thymeleafRequestDataValueProcessor =
                new SpringWebFluxThymeleafRequestDataValueProcessor(
                        this.requestContext.getRequestDataValueProcessor(), this.serverWebExchange);
    }


    public ServerWebExchange getServerWebExchange() {
        return this.serverWebExchange;
    }


    @Override
    public MessageSource getMessageSource() {
        return this.requestContext.getMessageSource();
    }

    @Override
    public Map<String, Object> getModel() {
        return this.requestContext.getModel();
    }

    @Override
    public Locale getLocale() {
        return this.requestContext.getLocale();
    }

    @Override
    public TimeZone getTimeZone() {
        return this.requestContext.getTimeZone();
    }

    @Override
    public void changeLocale(final Locale locale) {
        this.requestContext.changeLocale(locale);
    }

    @Override
    public void changeLocale(final Locale locale, final TimeZone timeZone) {
        this.requestContext.changeLocale(locale, timeZone);
    }

    @Override
    public void setDefaultHtmlEscape(final boolean defaultHtmlEscape) {
        this.requestContext.setDefaultHtmlEscape(defaultHtmlEscape);
    }

    @Override
    public boolean isDefaultHtmlEscape() {
        return this.requestContext.isDefaultHtmlEscape();
    }

    @Override
    public Boolean getDefaultHtmlEscape() {
        return this.requestContext.getDefaultHtmlEscape();
    }

    @Override
    public String getContextPath() {
        return this.requestContext.getContextPath();
    }

    @Override
    public String getContextUrl(final String relativeUrl) {
        return this.requestContext.getContextUrl(relativeUrl);
    }

    @Override
    public String getContextUrl(final String relativeUrl, final Map<String, ?> params) {
        return this.requestContext.getContextUrl(relativeUrl, params);
    }

    @Override
    public String getRequestPath() {
        return this.requestContext.getRequestPath();
    }

    @Override
    public String getQueryString() {
        return this.requestContext.getQueryString();
    }

    @Override
    public String getMessage(final String code, final String defaultMessage) {
        return this.requestContext.getMessage(code, defaultMessage);
    }

    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage) {
        return this.requestContext.getMessage(code, args, defaultMessage);
    }

    @Override
    public String getMessage(final String code, final List<?> args, final String defaultMessage) {
        return this.requestContext.getMessage(code, args, defaultMessage);
    }

    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final boolean htmlEscape) {
        return this.requestContext.getMessage(code, args, defaultMessage, htmlEscape);
    }

    @Override
    public String getMessage(final String code) throws NoSuchMessageException {
        return this.requestContext.getMessage(code);
    }

    @Override
    public String getMessage(final String code, final Object[] args) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args);
    }

    @Override
    public String getMessage(final String code, final List<?> args) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args);
    }

    @Override
    public String getMessage(final String code, final Object[] args, final boolean htmlEscape) throws NoSuchMessageException {
        return this.requestContext.getMessage(code, args, htmlEscape);
    }

    @Override
    public String getMessage(final MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return this.requestContext.getMessage(resolvable);
    }

    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final boolean htmlEscape) throws NoSuchMessageException {
        return this.requestContext.getMessage(resolvable, htmlEscape);
    }

    @Override
    public Optional<Errors> getErrors(final String name) {
        return Optional.ofNullable(this.requestContext.getErrors(name));
    }

    @Override
    public Optional<Errors> getErrors(final String name, final boolean htmlEscape) {
        return Optional.ofNullable(this.requestContext.getErrors(name, htmlEscape));
    }

    @Override
    public Theme getTheme() {
        throw new TemplateProcessingException("Themes are not supported in Thymeleaf's Spring WebFlux integration");
    }


    @Override
    public IThymeleafRequestDataValueProcessor getRequestDataValueProcessor() {
        return this.thymeleafRequestDataValueProcessor;
    }

    @Override
    public IThymeleafBindStatus getBindStatus(final String path) throws IllegalStateException {
        return Optional.ofNullable(this.requestContext.getBindStatus(path)).map(SpringWebFluxThymeleafBindStatus::new).orElse(null);
    }

    @Override
    public IThymeleafBindStatus getBindStatus(final String path, final boolean htmlEscape) throws IllegalStateException {
        return Optional.ofNullable(this.requestContext.getBindStatus(path, htmlEscape)).map(SpringWebFluxThymeleafBindStatus::new).orElse(null);
    }




    @Override
    public String toString() {
        return this.requestContext.toString();
    }


}
