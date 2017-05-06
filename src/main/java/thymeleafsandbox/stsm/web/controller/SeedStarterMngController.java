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
package thymeleafsandbox.stsm.web.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import thymeleafsandbox.stsm.business.entities.Feature;
import thymeleafsandbox.stsm.business.entities.Row;
import thymeleafsandbox.stsm.business.entities.SeedStarter;
import thymeleafsandbox.stsm.business.entities.Type;
import thymeleafsandbox.stsm.business.entities.Variety;
import thymeleafsandbox.stsm.business.services.SeedStarterService;
import thymeleafsandbox.stsm.business.services.VarietyService;


@Controller
public class SeedStarterMngController {


    private VarietyService varietyService;
    private SeedStarterService seedStarterService;
    
    
    
    public SeedStarterMngController() {
        super();
    }


    @Autowired
    public void setVarietyService(final VarietyService varietyService) {
        this.varietyService = varietyService;
    }


    @Autowired
    public void setSeedStarterService(final SeedStarterService seedStarterService) {
        this.seedStarterService = seedStarterService;
    }




    @ModelAttribute("allTypes")
    public List<Type> populateTypes() {
        return Arrays.asList(Type.ALL);
    }
    
    @ModelAttribute("allFeatures")
    public List<Feature> populateFeatures() {
        return Arrays.asList(Feature.ALL);
    }
    
    @ModelAttribute("allVarieties")
    public Flux<Variety> populateVarieties() {
        return this.varietyService.findAll();
    }
    
    @ModelAttribute("allSeedStarters")
    public Flux<SeedStarter> populateSeedStarters() {
        return this.seedStarterService.findAll();
    }


    /*
     * NOTE that in this reactive version of STSM we cannot select the controller method to be executed
     * depending on the presence of a specific request parameter (using the "param" attribute of the
     * @RequestMapping annotation) because WebFlux does not include as "request parameters" data
     * coming from forms (see https://jira.spring.io/browse/SPR-15508 ). Doing so would mean blocking
     * for the time the framework needs for reading the request payload, which goes against the
     * general reactiveness of the architecture.
     *
     * So the ways to access data from form are, either include then as a part of form-backing bean
     * (in this case SeedStarter), or using exchange.getFormData(). In this case, modifying a model entity
     * like SeedStarter because of a very specific need of the user interface (adding the "save",
     * "addRow" or "removeRow" parameters in order to modify the form's structure from the server) would
     * not be very elegant, so instead we will read exchange.getFormData() and direct to a different
     * inner (private) controller method depending on the presence of these fields in the form data
     * coming from the client.
     */

    @RequestMapping({"/","/seedstartermng"})
    public Mono<String> doSeedstarter(
            final SeedStarter seedStarter, final BindingResult bindingResult, final ModelMap model,
            final ServerWebExchange exchange) {

        return exchange.getFormData().flatMap(
                formData -> {
                    if (formData.containsKey("save")) {
                        return saveSeedstarter(seedStarter,  bindingResult, model);
                    }
                    if (formData.containsKey("addRow")) {
                        return addRow(seedStarter, bindingResult);
                    }
                    if (formData.containsKey("removeRow")) {
                        final int rowId = Integer.parseInt(formData.getFirst("removeRow"));
                        return removeRow(seedStarter, bindingResult, rowId);
                    }
                    return showSeedstarters(seedStarter);
                });

    }


    private Mono<String> showSeedstarters(final SeedStarter seedStarter) {
        seedStarter.setDatePlanted(Calendar.getInstance().getTime());
        return Mono.just("seedstartermng");
    }


    private Mono<String> saveSeedstarter(final SeedStarter seedStarter, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return Mono.just("seedstartermng");
        }
        return this.seedStarterService.add(seedStarter)
                .then(Mono.fromRunnable(() -> model.clear()))
                .then(Mono.just("redirect:/seedstartermng"));
    }


    private Mono<String> addRow(final SeedStarter seedStarter, final BindingResult bindingResult) {
        seedStarter.getRows().add(new Row());
        return Mono.just("seedstartermng");
    }
    
    
    private Mono<String> removeRow(
            final SeedStarter seedStarter,
            final BindingResult bindingResult,
            final int rowId) {
        seedStarter.getRows().remove(rowId);
        return Mono.just("seedstartermng");
    }


}
