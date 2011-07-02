package thymeleafexamples.sayhello.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SayHelloController {

    public SayHelloController() {
        super();
    }

    
    @ModelAttribute("planets")
    public List<String> populatePlanets() {
        return Arrays.asList(new String[] {
                "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"
        });
    }
    
    @RequestMapping({"/","/sayhello"})
    public String showSayHello() {
        return "sayhello";
    }
    
    
}
