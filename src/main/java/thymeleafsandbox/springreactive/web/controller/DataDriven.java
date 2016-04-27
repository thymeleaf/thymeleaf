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
package thymeleafsandbox.springreactive.web.controller;

import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import thymeleafsandbox.springreactive.business.PlaylistEntry;
import thymeleafsandbox.springreactive.business.repository.PlaylistEntryRepository;


@Controller
public class DataDriven {


    @Autowired
    private PlaylistEntryRepository playlistEntryRepository;



    public DataDriven() {
        super();
    }


    @RequestMapping("/datadriven-flow-buffered.thymeleaf")
    public String dataDrivenFlowBufferedThymeleaf(final Model model) {

        final Publisher<PlaylistEntry> playlistFlow = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();

        model.addAttribute("dataSource", playlistFlow);

        return "thymeleaf/datadriven";

    }

    @RequestMapping("/datadriven-noflow-buffered.thymeleaf")
    public String dataDrivenNoFlowBufferedThymeleaf(final Model model) {

        final Publisher<PlaylistEntry> playlistFlow = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();
        final List<PlaylistEntry> playlistEntries = Flux.from(playlistFlow).toList().get();

        model.addAttribute("dataSource", playlistEntries);

        return "thymeleaf/datadriven";

    }


    // NOTE When a Publisher (a "flow") is used, there will always buffering, but without a size limit in bytes
    @RequestMapping("/datadriven-flow-unbuffered.thymeleaf")
    public String dataDrivenFlowUnbufferedThymeleaf(final Model model) {

        final Publisher<PlaylistEntry> playlistFlow = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();

        model.addAttribute("dataSource", playlistFlow);

        return "thymeleaf/datadriven-unbuffered";

    }

    @RequestMapping("/datadriven-noflow-unbuffered.thymeleaf")
    public String dataDrivenNoFlowUnbufferedThymeleaf(final Model model) {

        final Publisher<PlaylistEntry> playlistFlow = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();
        final List<PlaylistEntry> playlistEntries = Flux.from(playlistFlow).toList().get();

        model.addAttribute("dataSource", playlistEntries);

        return "thymeleaf/datadriven-unbuffered";

    }


    @RequestMapping("/datadriven.freemarker")
    public String playlistEntryListFreeMarker(final Model model) {

        final Publisher<PlaylistEntry> playlistFlow = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();
        final List<PlaylistEntry> playlistEntries = Flux.from(playlistFlow).toList().get();

        model.addAttribute("dataSource", playlistEntries);

        return "freemarker/datadriven";

    }

}
