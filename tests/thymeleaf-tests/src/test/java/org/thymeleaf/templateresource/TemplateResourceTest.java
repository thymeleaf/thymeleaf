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
package org.thymeleaf.templateresource;

import java.io.File;

import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.testing.templateengine.util.JakartaServletMockUtils;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


public final class TemplateResourceTest {


    @Test
    public void testTemplateResourceUtils() throws Exception {

        Assertions.assertEquals("/", TemplateResourceUtils.computeRelativeLocation("/", "/"));
        Assertions.assertEquals("/something", TemplateResourceUtils.computeRelativeLocation("/", "something"));
        Assertions.assertEquals("/", TemplateResourceUtils.computeRelativeLocation("/something", "/"));
        Assertions.assertEquals("/", TemplateResourceUtils.computeRelativeLocation("something", "/"));
        Assertions.assertEquals("something/", TemplateResourceUtils.computeRelativeLocation("something/else", "/"));
        Assertions.assertEquals("something/else/", TemplateResourceUtils.computeRelativeLocation("something/else/more", "/"));
        Assertions.assertEquals("something/else/less", TemplateResourceUtils.computeRelativeLocation("something/else/more", "less"));
        Assertions.assertEquals("something/else/more.properties", TemplateResourceUtils.computeRelativeLocation("something/else/more.html", "more.properties"));
        Assertions.assertEquals("something/else/more_es.properties", TemplateResourceUtils.computeRelativeLocation("something/else/more.html", "more_es.properties"));
        Assertions.assertEquals("something/else/../more_es.properties", TemplateResourceUtils.computeRelativeLocation("something/else/more.html", "../more_es.properties"));
        Assertions.assertEquals("something/else/../../more_es.properties", TemplateResourceUtils.computeRelativeLocation("something/else/more.html", "../../more_es.properties"));

        Assertions.assertEquals("/", TemplateResourceUtils.cleanPath("/"));
        Assertions.assertEquals("something", TemplateResourceUtils.cleanPath("something"));
        Assertions.assertEquals("/something", TemplateResourceUtils.cleanPath("/something"));
        Assertions.assertEquals("something/else", TemplateResourceUtils.cleanPath("something/else"));
        Assertions.assertEquals("/something/else", TemplateResourceUtils.cleanPath("//something//else"));
        Assertions.assertEquals("/something/else", TemplateResourceUtils.cleanPath("//something//a//..//else"));
        Assertions.assertEquals("something/else/more", TemplateResourceUtils.cleanPath("something/else/more"));
        Assertions.assertEquals("something/else/more", TemplateResourceUtils.cleanPath("something/else//more"));
        Assertions.assertEquals("something/more", TemplateResourceUtils.cleanPath("something/else/../more"));
        Assertions.assertEquals("something/else/more", TemplateResourceUtils.cleanPath("something/else/./more"));
        Assertions.assertEquals("../something/else/more", TemplateResourceUtils.cleanPath("../something/else/./more"));
        Assertions.assertEquals("something/else/more", TemplateResourceUtils.cleanPath("./something/else/./more"));
        Assertions.assertEquals("something/else/more.html", TemplateResourceUtils.cleanPath("something/else/more.html"));
        Assertions.assertEquals("../something/else/more.html", TemplateResourceUtils.cleanPath("../something/else/more.html"));
        Assertions.assertEquals("../something/else", TemplateResourceUtils.cleanPath("../something/else/more.html/.."));
        Assertions.assertEquals("something/more_es.properties", TemplateResourceUtils.cleanPath("something/else/more.html/../../more_es.properties"));

        Assertions.assertNull(TemplateResourceUtils.computeBaseName("/"));
        Assertions.assertEquals("something", TemplateResourceUtils.computeBaseName("something"));
        Assertions.assertEquals("something", TemplateResourceUtils.computeBaseName("/something"));
        Assertions.assertEquals("else", TemplateResourceUtils.computeBaseName("something/else"));
        Assertions.assertEquals("else", TemplateResourceUtils.computeBaseName("//something//else"));
        Assertions.assertEquals("else", TemplateResourceUtils.computeBaseName("//something//a//..//else"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("something/else/more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("something/else//more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("something/else/../more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("something/else/./more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("../something/else/./more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("./something/else/./more"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("something/else/more.html"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("../something/else/more.html"));
        // The following result might be weird, but the passed path will never exist as it should have been 'cleaned' first
        Assertions.assertEquals(".", TemplateResourceUtils.computeBaseName("../something/else/more.html/.."));
        Assertions.assertEquals("more_es", TemplateResourceUtils.computeBaseName("something/else/more.html/../../more_es.properties"));
        Assertions.assertEquals("more", TemplateResourceUtils.computeBaseName("more.html"));

    }


    @Test
    public void testServletContextResource() throws Exception {

        final ServletContext servletContext = JakartaServletMockUtils.buildServletContext().build();
        final IWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);

        Assertions.assertEquals("/", (new WebApplicationTemplateResource(webApplication, "/", null)).getDescription());
        Assertions.assertEquals("/something", (new WebApplicationTemplateResource(webApplication, "something", null)).getDescription());
        Assertions.assertEquals("/something", (new WebApplicationTemplateResource(webApplication, "/something", null)).getDescription());
        Assertions.assertEquals("/something/else", (new WebApplicationTemplateResource(webApplication, "something/else", null)).getDescription());
        Assertions.assertEquals("/something/else", (new WebApplicationTemplateResource(webApplication, "//something//else", null)).getDescription());
        Assertions.assertEquals("/something/else", (new WebApplicationTemplateResource(webApplication, "//something//a//..//else", null)).getDescription());
        Assertions.assertEquals("/something/else/more", (new WebApplicationTemplateResource(webApplication, "something/else/more", null)).getDescription());
        Assertions.assertEquals("/something/else/more", (new WebApplicationTemplateResource(webApplication, "something/else//more", null)).getDescription());
        Assertions.assertEquals("/something/more", (new WebApplicationTemplateResource(webApplication, "something/else/../more", null)).getDescription());
        Assertions.assertEquals("/something/else/more", (new WebApplicationTemplateResource(webApplication, "something/else/./more", null)).getDescription());
        Assertions.assertEquals("/../something/else/more", (new WebApplicationTemplateResource(webApplication, "../something/else/./more", null)).getDescription());
        Assertions.assertEquals("/something/else/more", (new WebApplicationTemplateResource(webApplication, "./something/else/./more", null)).getDescription());
        Assertions.assertEquals("/something/else/more.html", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null)).getDescription());
        Assertions.assertEquals("/../something/else/more.html", (new WebApplicationTemplateResource(webApplication, "../something/else/more.html", null)).getDescription());
        Assertions.assertEquals("/../something/else", (new WebApplicationTemplateResource(webApplication, "../something/else/more.html/..", null)).getDescription());
        Assertions.assertEquals("/something/more_es.properties", (new WebApplicationTemplateResource(webApplication, "something/else/more.html/../../more_es.properties", null)).getDescription());

        Assertions.assertEquals("/", (new WebApplicationTemplateResource(webApplication, "/", null).relative("/")).getDescription());
        Assertions.assertEquals("/something", (new WebApplicationTemplateResource(webApplication, "/", null).relative("something")).getDescription());
        Assertions.assertEquals("/", (new WebApplicationTemplateResource(webApplication, "/something", null).relative("/")).getDescription());
        Assertions.assertEquals("/", (new WebApplicationTemplateResource(webApplication, "something", null).relative("/")).getDescription());
        Assertions.assertEquals("/something/", (new WebApplicationTemplateResource(webApplication, "something/else", null).relative("/")).getDescription());
        Assertions.assertEquals("/something/else/", (new WebApplicationTemplateResource(webApplication, "something/else/more", null).relative("/")).getDescription());
        Assertions.assertEquals("/something/else/less", (new WebApplicationTemplateResource(webApplication, "something/else/more", null).relative("less")).getDescription());
        Assertions.assertEquals("/something/else/more/less", (new WebApplicationTemplateResource(webApplication, "something/else/more/", null).relative("less")).getDescription());
        Assertions.assertEquals("/something/else/more.properties", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null).relative("more.properties")).getDescription());
        Assertions.assertEquals("/something/else/more_es.properties", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null).relative("more_es.properties")).getDescription());
        Assertions.assertEquals("/something/more_es.properties", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals("/something/more_es.properties", (new WebApplicationTemplateResource(webApplication, "something/more/../else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals("/more_es.properties", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null).relative("../../more_es.properties")).getDescription());

        Assertions.assertNull((new WebApplicationTemplateResource(webApplication, "/", null)).getBaseName());
        Assertions.assertEquals("something", (new WebApplicationTemplateResource(webApplication, "something", null)).getBaseName());
        Assertions.assertEquals("something", (new WebApplicationTemplateResource(webApplication, "/something", null)).getBaseName());
        Assertions.assertEquals("else", (new WebApplicationTemplateResource(webApplication, "something/else", null)).getBaseName());
        Assertions.assertEquals("else", (new WebApplicationTemplateResource(webApplication, "//something//else", null)).getBaseName());
        Assertions.assertEquals("else", (new WebApplicationTemplateResource(webApplication, "//something//a//..//else", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "something/else/more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "something/else//more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "something/else/../more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "../something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "./something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "../something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "../something/else/more.html/", null)).getBaseName());
        Assertions.assertEquals("else", (new WebApplicationTemplateResource(webApplication, "../something/else/more.html/..", null)).getBaseName());
        Assertions.assertEquals("more_es", (new WebApplicationTemplateResource(webApplication, "something/else/more.html/../../more_es.properties", null)).getBaseName());
        Assertions.assertEquals("more", (new WebApplicationTemplateResource(webApplication, "more.html", null)).getBaseName());

    }


    @Test
    public void testClassLoaderResource() throws Exception {

        final ClassLoader classLoader = TemplateEngine.class.getClassLoader();

        Assertions.assertEquals("", (new ClassLoaderTemplateResource(classLoader, "/", null)).getDescription());
        Assertions.assertEquals("something", (new ClassLoaderTemplateResource(classLoader, "something", null)).getDescription());
        Assertions.assertEquals("something", (new ClassLoaderTemplateResource(classLoader, "/something", null)).getDescription());
        Assertions.assertEquals("something/else", (new ClassLoaderTemplateResource(classLoader, "something/else", null)).getDescription());
        Assertions.assertEquals("something/else", (new ClassLoaderTemplateResource(classLoader, "//something//else", null)).getDescription());
        Assertions.assertEquals("something/else", (new ClassLoaderTemplateResource(classLoader, "//something//a//..//else", null)).getDescription());
        Assertions.assertEquals("something/else/more", (new ClassLoaderTemplateResource(classLoader, "something/else/more", null)).getDescription());
        Assertions.assertEquals("something/else/more", (new ClassLoaderTemplateResource(classLoader, "something/else//more", null)).getDescription());
        Assertions.assertEquals("something/more", (new ClassLoaderTemplateResource(classLoader, "something/else/../more", null)).getDescription());
        Assertions.assertEquals("something/else/more", (new ClassLoaderTemplateResource(classLoader, "something/else/./more", null)).getDescription());
        Assertions.assertEquals("../something/else/more", (new ClassLoaderTemplateResource(classLoader, "../something/else/./more", null)).getDescription());
        Assertions.assertEquals("something/else/more", (new ClassLoaderTemplateResource(classLoader, "./something/else/./more", null)).getDescription());
        Assertions.assertEquals("something/else/more.html", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null)).getDescription());
        Assertions.assertEquals("../something/else/more.html", (new ClassLoaderTemplateResource(classLoader, "../something/else/more.html", null)).getDescription());
        Assertions.assertEquals("../something/else", (new ClassLoaderTemplateResource(classLoader, "../something/else/more.html/..", null)).getDescription());
        Assertions.assertEquals("something/more_es.properties", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html/../../more_es.properties", null)).getDescription());

        Assertions.assertEquals("", (new ClassLoaderTemplateResource(classLoader, "/", null).relative("/")).getDescription());
        Assertions.assertEquals("something", (new ClassLoaderTemplateResource(classLoader, "/", null).relative("something")).getDescription());
        Assertions.assertEquals("", (new ClassLoaderTemplateResource(classLoader, "/something", null).relative("/")).getDescription());
        Assertions.assertEquals("", (new ClassLoaderTemplateResource(classLoader, "something", null).relative("/")).getDescription());
        Assertions.assertEquals("something/", (new ClassLoaderTemplateResource(classLoader, "something/else", null).relative("/")).getDescription());
        Assertions.assertEquals("something/else/", (new ClassLoaderTemplateResource(classLoader, "something/else/more", null).relative("/")).getDescription());
        Assertions.assertEquals("something/else/less", (new ClassLoaderTemplateResource(classLoader, "something/else/more", null).relative("less")).getDescription());
        Assertions.assertEquals("something/else/more/less", (new ClassLoaderTemplateResource(classLoader, "something/else/more/", null).relative("less")).getDescription());
        Assertions.assertEquals("something/else/more.properties", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null).relative("more.properties")).getDescription());
        Assertions.assertEquals("something/else/more_es.properties", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null).relative("more_es.properties")).getDescription());
        Assertions.assertEquals("something/more_es.properties", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals("something/more_es.properties", (new ClassLoaderTemplateResource(classLoader, "something/more/../else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals("more_es.properties", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null).relative("../../more_es.properties")).getDescription());

        Assertions.assertNull((new ClassLoaderTemplateResource(classLoader, "/", null)).getBaseName());
        Assertions.assertEquals("something", (new ClassLoaderTemplateResource(classLoader, "something", null)).getBaseName());
        Assertions.assertEquals("something", (new ClassLoaderTemplateResource(classLoader, "/something", null)).getBaseName());
        Assertions.assertEquals("else", (new ClassLoaderTemplateResource(classLoader, "something/else", null)).getBaseName());
        Assertions.assertEquals("else", (new ClassLoaderTemplateResource(classLoader, "//something//else", null)).getBaseName());
        Assertions.assertEquals("else", (new ClassLoaderTemplateResource(classLoader, "//something//a//..//else", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "something/else/more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "something/else//more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "something/else/../more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "../something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "./something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "../something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "../something/else/more.html/", null)).getBaseName());
        Assertions.assertEquals("else", (new ClassLoaderTemplateResource(classLoader, "../something/else/more.html/..", null)).getBaseName());
        Assertions.assertEquals("more_es", (new ClassLoaderTemplateResource(classLoader, "something/else/more.html/../../more_es.properties", null)).getBaseName());
        Assertions.assertEquals("more", (new ClassLoaderTemplateResource(classLoader, "more.html", null)).getBaseName());

    }


    @Test
    public void testFileResource() throws Exception {

        Assertions.assertEquals(new File("/").getAbsolutePath(), (new FileTemplateResource("/", null)).getDescription());
        Assertions.assertEquals(new File("something").getAbsolutePath(), (new FileTemplateResource("something", null)).getDescription());
        Assertions.assertEquals(new File("/something").getAbsolutePath(), (new FileTemplateResource("/something", null)).getDescription());
        Assertions.assertEquals(new File("something/else").getAbsolutePath(), (new FileTemplateResource("something/else", null)).getDescription());
        Assertions.assertEquals(new File("//something//else").getAbsolutePath(), (new FileTemplateResource("//something//else", null)).getDescription());
        Assertions.assertEquals(new File("//something//a//..//else").getAbsolutePath(), (new FileTemplateResource("//something//a//..//else", null)).getDescription());
        Assertions.assertEquals(new File("something/else/more").getAbsolutePath(), (new FileTemplateResource("something/else/more", null)).getDescription());
        Assertions.assertEquals(new File("something/else//more").getAbsolutePath(), (new FileTemplateResource("something/else//more", null)).getDescription());
        Assertions.assertEquals(new File("something/else/../more").getAbsolutePath(), (new FileTemplateResource("something/else/../more", null)).getDescription());
        Assertions.assertEquals(new File("something/else/./more").getAbsolutePath(), (new FileTemplateResource("something/else/./more", null)).getDescription());
        Assertions.assertEquals(new File("../something/else/./more").getAbsolutePath(), (new FileTemplateResource("../something/else/./more", null)).getDescription());
        Assertions.assertEquals(new File("./something/else/./more").getAbsolutePath(), (new FileTemplateResource("./something/else/./more", null)).getDescription());
        Assertions.assertEquals(new File("something/else/more.html").getAbsolutePath(), (new FileTemplateResource("something/else/more.html", null)).getDescription());
        Assertions.assertEquals(new File("../something/else/more.html").getAbsolutePath(), (new FileTemplateResource("../something/else/more.html", null)).getDescription());
        Assertions.assertEquals(new File("../something/else/more.html/..").getAbsolutePath(), (new FileTemplateResource("../something/else/more.html/..", null)).getDescription());
        Assertions.assertEquals(new File("something/else/more.html/../../more_es.properties").getAbsolutePath(), (new FileTemplateResource("something/else/more.html/../../more_es.properties", null)).getDescription());

        Assertions.assertEquals(new File("/").getAbsolutePath(), (new FileTemplateResource("/", null).relative("/")).getDescription());
        Assertions.assertEquals(new File("/something").getAbsolutePath(), (new FileTemplateResource("/", null).relative("something")).getDescription());
        Assertions.assertEquals(new File("/").getAbsolutePath(), (new FileTemplateResource("/something", null).relative("/")).getDescription());
        Assertions.assertEquals(new File("/").getAbsolutePath(), (new FileTemplateResource("something", null).relative("/")).getDescription());
        Assertions.assertEquals(new File("something/").getAbsolutePath(), (new FileTemplateResource("something/else", null).relative("/")).getDescription());
        Assertions.assertEquals(new File("something/else/").getAbsolutePath(), (new FileTemplateResource("something/else/more", null).relative("/")).getDescription());
        Assertions.assertEquals(new File("something/else/less").getAbsolutePath(), (new FileTemplateResource("something/else/more", null).relative("less")).getDescription());
        Assertions.assertEquals(new File("something/else/more/less").getAbsolutePath(), (new FileTemplateResource("something/else/more/", null).relative("less")).getDescription());
        Assertions.assertEquals(new File("something/else/more.properties").getAbsolutePath(), (new FileTemplateResource("something/else/more.html", null).relative("more.properties")).getDescription());
        Assertions.assertEquals(new File("something/else/more_es.properties").getAbsolutePath(), (new FileTemplateResource("something/else/more.html", null).relative("more_es.properties")).getDescription());
        Assertions.assertEquals(new File("something/else/../more_es.properties").getAbsolutePath(), (new FileTemplateResource("something/else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals(new File("something/else/../more_es.properties").getAbsolutePath(), (new FileTemplateResource("something/more/../else/more.html", null).relative("../more_es.properties")).getDescription());
        Assertions.assertEquals(new File("something/else/../../more_es.properties").getAbsolutePath(), (new FileTemplateResource("something/else/more.html", null).relative("../../more_es.properties")).getDescription());

        Assertions.assertNull((new FileTemplateResource("/", null)).getBaseName());
        Assertions.assertEquals("something", (new FileTemplateResource("something", null)).getBaseName());
        Assertions.assertEquals("something", (new FileTemplateResource("/something", null)).getBaseName());
        Assertions.assertEquals("else", (new FileTemplateResource("something/else", null)).getBaseName());
        Assertions.assertEquals("else", (new FileTemplateResource("//something//else", null)).getBaseName());
        Assertions.assertEquals("else", (new FileTemplateResource("//something//a//..//else", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("something/else/more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("something/else//more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("something/else/../more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("../something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("./something/else/./more", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("../something/else/more.html", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("../something/else/more.html/", null)).getBaseName());
        Assertions.assertEquals("else", (new FileTemplateResource("../something/else/more.html/..", null)).getBaseName());
        Assertions.assertEquals("more_es", (new FileTemplateResource("something/else/more.html/../../more_es.properties", null)).getBaseName());
        Assertions.assertEquals("more", (new FileTemplateResource("more.html", null)).getBaseName());

    }


    @Test
    public void testURLResource() throws Exception {

        Assertions.assertEquals("http://www.thymeleaf.org/", (new UrlTemplateResource("http://www.thymeleaf.org/", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org", (new UrlTemplateResource("http://www.thymeleaf.org", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something", (new UrlTemplateResource("http://www.thymeleaf.org/something", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/", (new UrlTemplateResource("http://www.thymeleaf.org/something/", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/else", (new UrlTemplateResource("http://www.thymeleaf.org/something/else", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/else.html", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/./else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/./else.html", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/more/../else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/more/../else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/./more/../else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html", null)).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/./more/../else.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html", null)).getDescription());

        Assertions.assertEquals("http://www.thymeleaf.org/", (new UrlTemplateResource("http://www.thymeleaf.org/", null).relative("/")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org", (new UrlTemplateResource("http://www.thymeleaf.org", null).relative("/")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something", (new UrlTemplateResource("http://www.thymeleaf.org", null).relative("/something")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something", (new UrlTemplateResource("http://www.thymeleaf.org", null).relative("something")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something", (new UrlTemplateResource("http://www.thymeleaf.org/more", null).relative("something")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/more/something", (new UrlTemplateResource("http://www.thymeleaf.org/more/", null).relative("something")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/more", (new UrlTemplateResource("http://www.thymeleaf.org/something/else", null).relative("more")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/something/more.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/else.html", null).relative("more.html")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/more.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/else.html", null).relative("../more.html")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/less.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null).relative("../less.html")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/even/less.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null).relative("../even/less.html")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/even/less.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html", null).relative("../even/less.html")).getDescription());
        Assertions.assertEquals("http://www.thymeleaf.org/even/less.html", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html", null).relative("../even/./less.html")).getDescription());

        Assertions.assertNull((new UrlTemplateResource("http://www.thymeleaf.org/", null).getBaseName()));
        Assertions.assertNull((new UrlTemplateResource("http://www.thymeleaf.org", null).getBaseName()));
        Assertions.assertNull((new UrlTemplateResource("http://www.thymeleaf.org", null).getBaseName()));
        Assertions.assertNull((new UrlTemplateResource("http://www.thymeleaf.org", null).getBaseName()));
        Assertions.assertEquals("more", (new UrlTemplateResource("http://www.thymeleaf.org/more", null).getBaseName()));
        Assertions.assertEquals("more", (new UrlTemplateResource("http://www.thymeleaf.org/more/", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/else", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/else.html", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/else.html", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html/", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/more/../else.html/a/..", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html", null).getBaseName()));
        Assertions.assertEquals("else", (new UrlTemplateResource("http://www.thymeleaf.org/something/./more/../else.html?param=a", null).getBaseName()));

    }

}
