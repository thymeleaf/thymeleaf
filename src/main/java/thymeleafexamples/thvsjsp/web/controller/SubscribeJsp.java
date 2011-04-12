package thymeleafexamples.thvsjsp.web.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import thymeleafexamples.thvsjsp.business.entities.Subscription;
import thymeleafexamples.thvsjsp.business.entities.SubscriptionType;


@Controller
public class SubscribeJsp {

    private static final Logger log = Logger.getLogger(SubscribeJsp.class);

    
    public SubscribeJsp() {
        super();
    }
    
    
    
    
    @ModelAttribute("allTypes")
    public SubscriptionType[] populateTypes() {
        return new SubscriptionType[] { SubscriptionType.ALL_EMAILS, SubscriptionType.DAILY_DIGEST };
    }

    
    @RequestMapping({"/subscribejsp"})
    public String showSubscription(final Subscription subscription) {
        return "subscribejsp";
    }
    
    
    
    @RequestMapping(value="/subscribejsp", params={"save"})
    public String subscribe(final Subscription subscription, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "subscribejsp";
        }
        log.info("JUST ADDED SUBSCRIPTION: " + subscription);
        model.clear();
        return "redirect:/subscribejsp";
    }

    
}
