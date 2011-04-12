package thymeleafexamples.thvsjsp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class Index {


    
    public Index() {
        super();
    }
    
    @RequestMapping({"/"})
    public String showSubscription() {
        return "index";
    }
    
}
