/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring.reactive;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring.reactive.data.Album;
import org.thymeleaf.spring.reactive.data.AlbumRepository;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

public final class SpringReactive11Test extends AbstractSpring5ReactiveTest {



    @Test
    public void testDataDrivenEmpty01() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(0), 1));

        testTemplate("reactive11", null, ctx1, "reactive11-01", true);

    }


    @Test
    public void testDataDrivenEmpty02() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();

        try {
            testTemplate("reactive11", null, ctx1, "reactive11-01", true);
            Assertions.assertTrue(false);
        } catch (final TemplateProcessingException e) {
            // When there is no data-driver variable, an exception should be thrown
            Assertions.assertTrue(true);
        }

    }


    @Test
    public void testDataDrivenLarge01() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 1));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge02() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 2));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge03() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 3));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge04() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 7));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge05() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 11));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge06() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 137));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge07() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), 1024));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenLarge08() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums), Integer.MAX_VALUE));

        testTemplate("reactive11", null, ctx1, "reactive11-02", true);

    }


    @Test
    public void testDataDrivenOne01() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 1));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne02() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 2));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne03() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 3));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne04() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 7));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne05() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 11));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne06() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 137));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne07() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), 1024));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenOne08() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(1), Integer.MAX_VALUE));

        testTemplate("reactive11", null, ctx1, "reactive11-03", true);

    }


    @Test
    public void testDataDrivenTwo01() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 1));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo02() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 2));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo03() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 3));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo04() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 7));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo05() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 11));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo06() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 137));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo07() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), 1024));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }


    @Test
    public void testDataDrivenTwo08() throws Exception {

        final List<Album> albums = AlbumRepository.findAllAlbums();

        final Context ctx1 = new Context();
        ctx1.setVariable("albums", new ReactiveDataDriverContextVariable(Flux.fromIterable(albums).take(2), Integer.MAX_VALUE));

        testTemplate("reactive11", null, ctx1, "reactive11-04", true);

    }



}
