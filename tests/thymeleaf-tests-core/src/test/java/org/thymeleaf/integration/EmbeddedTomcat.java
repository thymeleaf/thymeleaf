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
package org.thymeleaf.integration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebApplication;

/*
 * Embedded Tomcat serving IntegrationServlet at context path /app, with a fresh copy of the
 * integration/webapp test resources as document root (so that tests can modify templates).
 */
public final class EmbeddedTomcat implements AutoCloseable {

    public static final String CONTEXT_PATH = "/app";

    // Kept as a static field so that the configured level is not lost if the logger is garbage-collected
    private static final Logger TOMCAT_LOGGER = Logger.getLogger("org.apache");
    static {
        TOMCAT_LOGGER.setLevel(Level.WARNING);
    }

    private final Tomcat tomcat;
    private final Path baseDir;
    private final Path docBase;
    private final String baseUrl;


    public static EmbeddedTomcat start(final Function<IWebApplication, ITemplateEngine> engineFactory) throws Exception {
        return new EmbeddedTomcat(engineFactory);
    }


    private EmbeddedTomcat(final Function<IWebApplication, ITemplateEngine> engineFactory) throws Exception {

        final Path targetDir = Paths.get("target");
        Files.createDirectories(targetDir);
        this.baseDir = Files.createTempDirectory(targetDir.toAbsolutePath(), "integration-tomcat-");
        this.docBase = this.baseDir.resolve("webapp");

        final URL webappUrl = EmbeddedTomcat.class.getClassLoader().getResource("integration/webapp");
        copyDirectory(Paths.get(webappUrl.toURI()), this.docBase);

        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir(this.baseDir.toString());
        this.tomcat.setPort(0);
        this.tomcat.getConnector();

        final StandardContext context = (StandardContext) this.tomcat.addContext(CONTEXT_PATH, this.docBase.toString());
        // Leak detection on stop needs --add-opens and only produces warnings here
        context.setClearReferencesThreadLocals(false);
        context.setClearReferencesRmiTargets(false);
        final StandardRoot resources = new StandardRoot(context);
        resources.setCachingAllowed(false); // template changes must be visible immediately
        context.setResources(resources);
        final StandardManager manager = new StandardManager();
        manager.setPathname(null); // no session persistence between restarts
        context.setManager(manager);

        Tomcat.addServlet(context, "integration", new IntegrationServlet(engineFactory));
        context.addServletMappingDecoded("/*", "integration");

        this.tomcat.start();

        this.baseUrl = "http://localhost:" + this.tomcat.getConnector().getLocalPort() + CONTEXT_PATH;

    }


    public String getBaseUrl() {
        return this.baseUrl;
    }


    public Path getDocBase() {
        return this.docBase;
    }


    @Override
    public void close() throws Exception {
        try {
            this.tomcat.stop();
            this.tomcat.destroy();
        } finally {
            // Tomcat stores its base directory in system properties, which later servers in the same JVM would use
            for (final String property : new String[] { "catalina.base", "catalina.home" }) {
                if (this.baseDir.toString().equals(System.getProperty(property))) {
                    System.clearProperty(property);
                }
            }
            deleteDirectory(this.baseDir);
        }
    }


    private static void copyDirectory(final Path source, final Path target) throws IOException {
        try (final Stream<Path> paths = Files.walk(source)) {
            paths.forEach(path -> {
                try {
                    Files.copy(path, target.resolve(source.relativize(path).toString()), StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }


    private static void deleteDirectory(final Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (final Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        }
    }

}
