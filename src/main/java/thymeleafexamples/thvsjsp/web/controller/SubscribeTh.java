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
public class SubscribeTh {

    private static final Logger log = Logger.getLogger(SubscribeTh.class);

    
    public SubscribeTh() {
        super();
    }
    
    
    
    
    @ModelAttribute("allTypes")
    public SubscriptionType[] populateTypes() {
        return new SubscriptionType[] { SubscriptionType.ALL_EMAILS, SubscriptionType.DAILY_DIGEST };
    }

    
    @RequestMapping({"/subscribeth"})
    public String showSubscription(final Subscription subscription) {
        return "subscribeth";
    }
    
    
    
    @RequestMapping(value="/subscribeth", params={"save"})
    public String subscribe(final Subscription subscription, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "subscribeth";
        }
        log.info("JUST ADDED SUBSCRIPTION: " + subscription);
        model.clear();
        return "redirect:/subscribeth";
    }

    
}
