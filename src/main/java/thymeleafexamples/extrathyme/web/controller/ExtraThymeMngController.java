package thymeleafexamples.extrathyme.web.controller;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import thymeleafexamples.extrathyme.business.entities.Feature;
import thymeleafexamples.extrathyme.business.entities.Row;
import thymeleafexamples.extrathyme.business.entities.SeedStarter;
import thymeleafexamples.extrathyme.business.entities.Type;
import thymeleafexamples.extrathyme.business.entities.Variety;
import thymeleafexamples.extrathyme.business.services.SeedStarterService;
import thymeleafexamples.extrathyme.business.services.VarietyService;


@Controller
public class ExtraThymeMngController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VarietyService varietyService;
    
    @Autowired
    private SeedStarterService seedStarterService;
    
    
    
    public ExtraThymeMngController() {
        super();
    }
    
    
    
    
    @InitBinder
    public void initDateBinder(final WebDataBinder dataBinder, final Locale locale) {
        final String dateformat = 
            this.messageSource.getMessage("date.format", null, locale);
        final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        sdf.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
    }

    
    @InitBinder
    public void initVarietyBinder(final WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Variety.class, new VarietyPropertyEditor(this.varietyService));
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
    public List<Variety> populateVarieties() {
        return this.varietyService.findAll();
    }
    
    @ModelAttribute("allSeedStarters")
    public List<SeedStarter> populateSeedStarters() {
        return this.seedStarterService.findAll();
    }
    
    
    
    @RequestMapping({"/","/extrathyme"})
    public String showSeedstarters(final SeedStarter seedStarter) {
        seedStarter.setDatePlanted(Calendar.getInstance().getTime());
        return "extrathyme";
    }
    
    
    
    @RequestMapping(value="/extrathyme", params={"save"})
    public String saveSeedstarter(final SeedStarter seedStarter, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "extrathyme";
        }
        this.seedStarterService.add(seedStarter);
        model.clear();
        return "redirect:/extrathyme";
    }
    

    
    @RequestMapping(value="/extrathyme", params={"addRow"})
    public String addRow(final SeedStarter seedStarter, final BindingResult bindingResult) {
        seedStarter.getRows().add(new Row());
        return "extrathyme";
    }
    
    
    @RequestMapping(value="/extrathyme", params={"removeRow"})
    public String removeRow(final SeedStarter seedStarter, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
        seedStarter.getRows().remove(rowId.intValue());
        return "extrathyme";
    }

    
    
    
    
    static class VarietyPropertyEditor extends PropertyEditorSupport {

        private final VarietyService varietyService;
        
        public VarietyPropertyEditor(final VarietyService varietyService) {
            super();
            this.varietyService = varietyService;
        }
        
        
        @Override
        public String getAsText() {
            final Variety value = (Variety) getValue();
            return (value != null ? value.getId().toString() : "");
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            final Integer varietyId = Integer.valueOf(text);
            setValue(this.varietyService.findById(varietyId));
        }
        
        
    }
    
}
