package thymeleafexamples.stsm.web.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

    
    @RequestMapping({"/","/home"})
    public String showHomePage(final Map<String,Object> model) {
        return "home";
    }
    
    
}
