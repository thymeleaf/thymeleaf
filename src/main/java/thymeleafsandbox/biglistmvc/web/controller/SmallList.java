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
package thymeleafsandbox.biglistmvc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import thymeleafsandbox.biglistmvc.business.repository.PlaylistEntryRepository;


@Controller
public class SmallList {


    @Autowired
    private PlaylistEntryRepository playlistEntryRepository;



    public SmallList() {
        super();
    }



    @RequestMapping("/smalllist.thymeleaf")
    public String smallListThymeleaf(final Model model) {
        model.addAttribute("entries", this.playlistEntryRepository.findAllPlaylistEntries());
        return "thymeleaf/smalllist";
    }


    @RequestMapping("/smalllist.freemarker")
    public String smallListFreeMarker(final Model model) {
        model.addAttribute("entries", this.playlistEntryRepository.findAllPlaylistEntries());
        return "freemarker/smalllist";
    }


}
