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
    
    
    @ModelAttribute("allTeams")
    public List<Team> populateTeams() {
        return this.teamService.findAll();
    }
    
    
    
    @RequestMapping({"/","/extrathyme"})
    public String show() {
        return "extrathyme";
    }
    
}
