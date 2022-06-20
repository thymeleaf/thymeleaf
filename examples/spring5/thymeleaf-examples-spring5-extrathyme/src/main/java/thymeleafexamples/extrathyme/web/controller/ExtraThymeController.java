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
package thymeleafexamples.extrathyme.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import thymeleafexamples.extrathyme.business.entities.Team;
import thymeleafexamples.extrathyme.business.services.TeamService;


@Controller
public class ExtraThymeController {

    @Autowired
    private TeamService teamService;
    
    
    
    public ExtraThymeController() {
        super();
    }
    
    
    @ModelAttribute("teams")
    public List<Team> populateTeams() {
        return this.teamService.findAllTeams();
    }
    
    
    
    @RequestMapping({"/","/extrathyme"})
    public String show() {
        return "extrathyme";
    }
    
}
