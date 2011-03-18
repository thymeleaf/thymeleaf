package thymeleafexamples.stsm.web.controller;

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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import thymeleafexamples.stsm.business.entities.Feature;
import thymeleafexamples.stsm.business.entities.Row;
import thymeleafexamples.stsm.business.entities.Seedbed;
import thymeleafexamples.stsm.business.entities.Type;
import thymeleafexamples.stsm.business.entities.Variety;
import thymeleafexamples.stsm.business.services.SeedbedService;
import thymeleafexamples.stsm.business.services.VarietyService;


@Controller
public class SeedbedMngController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VarietyService varietyService;
    
    @Autowired
    private SeedbedService seedbedService;
    
    
    
    public SeedbedMngController() {
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
    
    
    
    @ModelAttribute("types")
    public List<Type> populateTypes() {
        return Arrays.asList(Type.ALL);
    }
    
    @ModelAttribute("features")
    public List<Feature> populateFeatures() {
        return Arrays.asList(Feature.ALL);
    }
    
    @ModelAttribute("varieties")
    public List<Variety> populateVarieties() {
        return this.varietyService.findAll();
    }
    
    @ModelAttribute("seedbeds")
    public List<Seedbed> populateSeedbeds() {
        return this.seedbedService.findAll();
    }
    
    
    
    @RequestMapping({"/","/seedbedmng"})
    public String showSeedbed(final Seedbed seedbed) {
        seedbed.setDatePlanted(Calendar.getInstance().getTime());
        return "seedbedmng";
    }
    
    
    
    @RequestMapping(value="/seedbedmng", params={"save"})
    public String saveSeedbed(final Seedbed seedbed, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            System.out.println("***ERRORS!!!");
            for (final ObjectError error : bindingResult.getAllErrors()) {
                System.out.println("---------------------------");
                System.out.println("   Code: " + error.getCode());
                System.out.println("   Codes: " + Arrays.asList(error.getCodes()));
                System.out.println("   Arguments: " + Arrays.asList(error.getArguments()));
                System.out.println("   Default message: " + error.getDefaultMessage());
                System.out.println("   Object name: " + error.getObjectName());
            }
            return "seedbedmng";
        }
        this.seedbedService.add(seedbed);
        model.put("seedbeds", populateSeedbeds());
        System.out.println("\n" + seedbed + "\n");
        return "redirect:seedbedmng";
    }
    

    
    @RequestMapping(value="/seedbedmng", params={"addRow"})
    public String addRow(final Seedbed seedbed, final BindingResult bindingResult) {
        seedbed.getRows().add(new Row());
        return "seedbedmng";
    }
    
    
    @RequestMapping(value="/seedbedmng", params={"removeRow"})
    public String removeRow(final Seedbed seedbed, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer rowId = Integer.valueOf(req.getParameter("removeRow"));
        seedbed.getRows().remove(rowId.intValue());
        return "seedbedmng";
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
