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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.context.WebContext;

/*
 * Maps request paths to template names, setting the model the way controllers would.
 */
final class IntegrationRoutes {

    static final String EXPLICIT_LOCALE_PATH = "/locale/explicit";

    static final List<Person> PEOPLE =
            Collections.unmodifiableList(Arrays.asList(
                    new Person("Ana", 34, new Address("Lugo")),
                    new Person("Bea", 28, null),
                    new Person("Carl", 45, new Address("Vigo"))));

    private static final Pattern RESTRICTED_TEMPLATE_NAME = Pattern.compile("[a-z-]+");
    private static final String RESTRICTED_PREFIX = "/restricted/";


    static String route(final String path, final HttpServletRequest request, final WebContext context) {

        switch (path) {
            case "/hello":
                context.setVariable("name", "World");
                return "hello";
            case "/messages":
            case EXPLICIT_LOCALE_PATH:
                context.setVariable("user", "Ana");
                return "messages";
            case "/fragments":
                context.setVariable("name", "Ana");
                context.setVariable("cond", Boolean.TRUE);
                return "fragments";
            case "/layout-page":
                context.setVariable("name", "Ana");
                return "layout-page";
            case "/layout-page-empty":
                context.setVariable("name", "Ana");
                return "layout-page-empty";
            case "/decoupled":
                context.setVariable("name", "Ana");
                return "decoupled";
            case "/decoupled-js":
                context.setVariable("name", "Ana");
                return "decoupled-js";
            case "/links":
                context.setVariable("url", "/dyn/path");
                context.setVariable("v", "a b&c");
                context.setVariable("list", Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)));
                return "links";
            case "/variables":
                request.setAttribute("reqAttr", "from-request");
                context.setVariable("ctxVar", "from-context");
                return "variables";
            case "/session/set":
                context.getExchange().getSession().setAttributeValue("user", request.getParameter("user"));
                return "session";
            case "/session/native":
                // Session created through the native request after the exchange has been built
                request.getSession(true).setAttribute("user", request.getParameter("user"));
                return "session";
            case "/session/invalidate":
                // Session invalidated through the native request after the exchange has been built
                request.getSession(true).invalidate();
                return "session";
            case "/session/show":
                return "session";
            case "/form":
                return "form";
            case "/ognl":
                context.setVariable("person", PEOPLE.get(0));
                context.setVariable("nobody", PEOPLE.get(1));
                context.setVariable("people", PEOPLE);
                context.setVariable("date", LocalDate.of(2026, 11, 11));
                context.setVariable("map", Collections.singletonMap("key", "v"));
                return "ognl";
            case "/report.txt":
                context.setVariable("people", PEOPLE);
                return "report.txt";
            case "/app.js":
                context.setVariable("people", PEOPLE);
                return "app.js";
            case "/inline-js":
                context.setVariable("person", PEOPLE.get(0));
                context.setVariable("text", "</script><b>&'\"");
                context.setVariable("map", Collections.singletonMap("k", "v"));
                return "inline-js";
            case "/big":
                context.setVariable("count", Integer.valueOf(request.getParameter("n")));
                return "big";
            case "/missing":
                return "does-not-exist";
            case "/removed-object":
                return "removed-object";
            case "/cache":
                return "cache";
            case "/second":
                return "second-only";
            default:
                if (path.startsWith(RESTRICTED_PREFIX)) {
                    final String name = path.substring(RESTRICTED_PREFIX.length());
                    if (RESTRICTED_TEMPLATE_NAME.matcher(name).matches()) {
                        context.setVariable("name", "Ana");
                        return "restricted/" + name;
                    }
                }
                return null;
        }

    }


    private IntegrationRoutes() {
        super();
    }

}
