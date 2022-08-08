/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.decoupled.stsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring6.naming.SpringContextVariableNames;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.testing.templateengine.util.JakartaServletMockUtils;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.DateUtils;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


public class DecoupledSTSMTest {



    public DecoupledSTSMTest() {
        super();
    }




    @Test
    public void testSTSMEmpty() throws Exception {

        final VarietyRepository varietyRepository = new VarietyRepository();

        final Map<String,Object> vars = new HashMap<String, Object>();

        vars.put("allSeedStarters", new ArrayList<SeedStarter>());
        vars.put("allTypes", Arrays.asList(Type.ALL));
        vars.put("allFeatures", Arrays.asList(Feature.ALL));
        vars.put("allVarieties", varietyRepository.findAll());

        final SeedStarter seedStarter = new SeedStarter();
        seedStarter.setDatePlanted(DateUtils.create(Integer.valueOf(2016), Integer.valueOf(02), Integer.valueOf(20)).getTime());
        vars.put("seedStarter", seedStarter);


        test("seedstartermng", "templateparser/decoupled/stsm/result/seedstartermng-empty.html", vars);

    }



    @Test
    public void testSTSMValues() throws Exception {

        final VarietyRepository varietyRepository = new VarietyRepository();

        final SeedStarter seedStarter01 = new SeedStarter();
        seedStarter01.setId(Integer.valueOf(1));
        seedStarter01.setCovered(Boolean.TRUE);
        seedStarter01.setDatePlanted(DateUtils.create(Integer.valueOf(2016), Integer.valueOf(02), Integer.valueOf(19)).getTime());
        seedStarter01.setFeatures(new Feature[] { Feature.SEEDSTARTER_SPECIFIC_SUBSTRATE, Feature.FERTILIZER });
        seedStarter01.setType(Type.PLASTIC);
        final Row row0101 = new Row();
        row0101.setVariety(varietyRepository.findById(Integer.valueOf(2)));
        row0101.setSeedsPerCell(Integer.valueOf(20));
        seedStarter01.getRows().add(row0101);
        final Row row0102 = new Row();
        row0102.setVariety(varietyRepository.findById(Integer.valueOf(4)));
        row0102.setSeedsPerCell(Integer.valueOf(10));
        seedStarter01.getRows().add(row0102);

        final SeedStarter seedStarter02 = new SeedStarter();
        seedStarter02.setId(Integer.valueOf(2));
        seedStarter02.setCovered(Boolean.FALSE);
        seedStarter02.setDatePlanted(DateUtils.create(Integer.valueOf(2016), Integer.valueOf(02), Integer.valueOf(18)).getTime());
        seedStarter02.setFeatures(new Feature[0]);
        seedStarter02.setType(Type.WOOD);
        final Row row0201 = new Row();
        row0201.setVariety(varietyRepository.findById(Integer.valueOf(1)));
        row0201.setSeedsPerCell(Integer.valueOf(30));
        seedStarter02.getRows().add(row0201);


        final List<SeedStarter> allSeedStarters = new ArrayList<SeedStarter>();
        allSeedStarters.add(seedStarter01);
        allSeedStarters.add(seedStarter02);


        final Map<String,Object> vars = new HashMap<String, Object>();

        vars.put("allSeedStarters", allSeedStarters);
        vars.put("allTypes", Arrays.asList(Type.ALL));
        vars.put("allFeatures", Arrays.asList(Feature.ALL));
        vars.put("allVarieties", varietyRepository.findAll());

        final SeedStarter seedStarter = new SeedStarter();
        seedStarter.setDatePlanted(DateUtils.create(Integer.valueOf(2016), Integer.valueOf(02), Integer.valueOf(20)).getTime());
        vars.put("seedStarter", seedStarter);

        test("seedstartermng", "templateparser/decoupled/stsm/result/seedstartermng-values.html", vars);

    }






    private static void test(final String templateName, final String expectedResultLocation, final Map<String,Object> vars) throws Exception {



        final Map<String,Object> requestAttributes = new LinkedHashMap<String, Object>();
        final Map<String,String[]> requestParameters = new LinkedHashMap<String, String[]>();
        final Map<String,Object> servletContextAttributes = new LinkedHashMap<String, Object>();

        final ServletContext mockServletContext =
                JakartaServletMockUtils.buildServletContext().attributeMap(servletContextAttributes).build();
        final HttpServletRequest mockRequest =
                JakartaServletMockUtils.buildRequest(mockServletContext, "WebVariablesMap")
                        .attributeMap(requestAttributes)
                        .parameterMap(requestParameters)
                        .locale(Locale.US)
                        .build();
        final HttpServletResponse mockResponse =
                JakartaServletMockUtils.buildResponse().build();

        final IWebExchange webExchange =
                JakartaServletWebApplication.buildApplication(mockServletContext).buildExchange(mockRequest, mockResponse);

        final XmlWebApplicationContext appCtx = new XmlWebApplicationContext();

        appCtx.setServletContext(mockServletContext);
        appCtx.setConfigLocation("classpath:templateparser/decoupled/stsm/applicationContext.xml");

        appCtx.refresh();

        mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);

        final ConversionService conversionService = getConversionService(appCtx); // can be null!

        mockRequest.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx);
        final ServletWebRequest servletWebRequest = new ServletWebRequest(mockRequest, mockResponse);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        final RequestContext requestContext =
                new RequestContext(mockRequest, mockResponse, mockServletContext, vars);
        vars.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
        vars.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        vars.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, new SpringWebMvcThymeleafRequestContext(requestContext, mockRequest));


        final List<String> variableNames = new ArrayList<String>(vars.keySet());
        for (final String variableName : variableNames) {
            final Object bindingObject = vars.get(variableName);
            if (isBindingCandidate(variableName, bindingObject)) {
                final String bindingVariableName = BindingResult.MODEL_KEY_PREFIX + variableName;
                if (!vars.containsKey(bindingVariableName)) {
                    final WebDataBinder dataBinder = new WebDataBinder(bindingObject, bindingVariableName);
                    dataBinder.setConversionService(conversionService);
                    vars.put(bindingVariableName, dataBinder.getBindingResult());
                }
            }
        }





        final WebContext ctx = new WebContext(webExchange, Locale.US);
        for (final Map.Entry<String,Object> entry : vars.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }



        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix("templateparser/decoupled/stsm/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setUseDecoupledLogic(true);


        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setLinkBuilder(new StandardLinkBuilder() {
            @Override
            protected String computeContextPath(final IExpressionContext context, final String base, final Map<String, Object> parameters) {
                return "";
            }
        });
        appCtx.getAutowireCapableBeanFactory().initializeBean(templateEngine, "templateEngine");


        
        final String result = templateEngine.process(templateName, ctx);

        final String expected =
                ResourceUtils.read(
                        ClassLoaderUtils.getClassLoader(DecoupledSTSMTest.class).getResourceAsStream(expectedResultLocation),
                        "ISO-8859-1",
                        true);

        Assertions.assertEquals(ResourceUtils.normalize(expected), ResourceUtils.normalize(result));


    }




    private static ConversionService getConversionService(final ApplicationContext applicationContext) {

        if (applicationContext == null) {
            return null;
        }

        final Map<String, ConversionService> conversionServices =
                applicationContext.getBeansOfType(ConversionService.class);

        if (conversionServices.size() == 0) {
            return null;
        }

        return (ConversionService) conversionServices.values().toArray()[0];

    }



    private static boolean isBindingCandidate(final String variableName, final Object bindingObject) {
        if (variableName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            return false;
        }
        return (bindingObject != null && !bindingObject.getClass().isArray() && !(bindingObject instanceof Collection) &&
                !(bindingObject instanceof Map) && !BeanUtils.isSimpleValueType(bindingObject.getClass()));
    }



}
