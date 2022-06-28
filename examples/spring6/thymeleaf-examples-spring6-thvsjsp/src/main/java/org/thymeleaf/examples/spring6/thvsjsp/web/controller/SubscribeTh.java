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
package org.thymeleaf.examples.spring6.thvsjsp.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.examples.spring6.thvsjsp.business.entities.Subscription;
import org.thymeleaf.examples.spring6.thvsjsp.business.entities.SubscriptionType;


@Controller
public class SubscribeTh {

    private static final Logger log = LoggerFactory.getLogger(SubscribeTh.class);

    
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
