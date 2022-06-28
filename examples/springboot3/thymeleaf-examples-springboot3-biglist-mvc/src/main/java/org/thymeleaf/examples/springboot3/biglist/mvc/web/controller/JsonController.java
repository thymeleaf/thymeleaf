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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.examples.springboot3.biglist.mvc.business.repository.PlaylistEntryRepository;
import org.thymeleaf.examples.springboot3.biglist.mvc.business.PlaylistEntry;


@RestController
public class JsonController {


    private PlaylistEntryRepository playlistEntryRepository;



    public JsonController() {
        super();
    }


    @Autowired
    public void setPlaylistEntryRepository(final PlaylistEntryRepository playlistEntryRepository) {
        this.playlistEntryRepository = playlistEntryRepository;
    }



    @RequestMapping("/json")
    public String index() {
        return "Use '/smalllist.json' or '/biglist.json'";
    }


    @RequestMapping("/smalllist.json")
    public Iterator<PlaylistEntry> smallList() {
        return this.playlistEntryRepository.findAllPlaylistEntries();
    }


    @RequestMapping("/biglist.json")
    public Iterator<PlaylistEntry> bigList() {
        return this.playlistEntryRepository.findLargeCollectionPlaylistEntries();
    }

}
