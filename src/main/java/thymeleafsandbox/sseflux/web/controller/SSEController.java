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
package thymeleafsandbox.sseflux.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import thymeleafsandbox.sseflux.business.PlaylistEntry;
import thymeleafsandbox.sseflux.business.repository.PlaylistEntryRepository;


@Controller
public class SSEController {


    private PlaylistEntryRepository playlistEntryRepository;



    public SSEController() {
        super();
    }


    @Autowired
    public void setPlaylistEntryRepository(final PlaylistEntryRepository playlistEntryRepository) {
        this.playlistEntryRepository = playlistEntryRepository;
    }



    @RequestMapping("/")
    public String index() {
        return "index";
    }


    @RequestMapping("/events")
    public String events(final Model model) {

        final Flux<PlaylistEntry> playlistStream = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();

        final IReactiveDataDriverContextVariable dataDriver =
                new ReactiveDataDriverContextVariable(playlistStream, 1000, 1);

        model.addAttribute("data", dataDriver);

        return "events";

    }


}
