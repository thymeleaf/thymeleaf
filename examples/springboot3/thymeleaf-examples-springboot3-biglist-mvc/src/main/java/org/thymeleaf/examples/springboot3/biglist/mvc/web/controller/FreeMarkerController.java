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
package org.thymeleaf.examples.springboot3.biglist.mvc.web.controller;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.examples.springboot3.biglist.mvc.business.repository.PlaylistEntryRepository;
import org.thymeleaf.examples.springboot3.biglist.mvc.business.PlaylistEntry;


@Controller
public class FreeMarkerController {


    private PlaylistEntryRepository playlistEntryRepository;



    public FreeMarkerController() {
        super();
    }


    @Autowired
    public void setPlaylistEntryRepository(final PlaylistEntryRepository playlistEntryRepository) {
        this.playlistEntryRepository = playlistEntryRepository;
    }



    @RequestMapping("/freemarker")
    public String index() {
        return "freemarker/index";
    }


    @RequestMapping("/smalllist.freemarker")
    public String smallList(final Model model) {
        model.addAttribute("entries", this.playlistEntryRepository.findAllPlaylistEntries());
        return "freemarker/smalllist";
    }


    @RequestMapping("/biglist.freemarker")
    public String bigListFreeMarker(final Model model) {

        final Iterator<PlaylistEntry> playlistEntries = this.playlistEntryRepository.findLargeCollectionPlaylistEntries();
        model.addAttribute("dataSource", playlistEntries);

        return "freemarker/biglist";

    }

}
