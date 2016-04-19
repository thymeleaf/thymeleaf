/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafsandbox.springreactive.application;


import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.boot.HttpServer;
import org.springframework.http.server.reactive.boot.ReactorHttpServer;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.ResponseStatusExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;



public class Application {


    public static void main(String[] args) throws Exception {

        /*
         * Initialization: ApplicationContext
         */
        final AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SpringReactiveWebConfig.class);

        /*
         * Initialization: DispatcherHandler
         */
        final DispatcherHandler dispatcherHandler = new DispatcherHandler();
        dispatcherHandler.setApplicationContext(context);

        /*
         * Initialization: CharacterEncodingFilter
         */
        // TODO Apparently no implementation of WebFilter for CharacterEncoding yet
        final WebFilter[] filters = new WebFilter[] {};

        /*
         * Initialization HttpHandler
         */
        final HttpHandler httpHandler =
                WebHttpHandlerBuilder.webHandler(dispatcherHandler)
                        .filters(filters)
                        .exceptionHandlers(new ResponseStatusExceptionHandler())
                        .build();

        /*
         * Initialization HttpServer
         */
        final HttpServer server = new ReactorHttpServer();
        server.setPort(8080);
        server.setHandler(httpHandler);
        server.afterPropertiesSet();
        server.start();

        /*
         * Server listening wait and shutdown setup
         */
        CompletableFuture<Void> stop = new CompletableFuture<>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop.complete(null);
        }));
        synchronized (stop) {
            stop.wait();
        }

    }


    private Application() {
        super();
    }


}