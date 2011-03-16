package thymeleafexamples.stsm.web.controller;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import thymeleafexamples.stsm.business.entities.Feature;
import thymeleafexamples.stsm.business.entities.Row;
import thymeleafexamples.stsm.business.entities.Seedbed;
import thymeleafexamples.stsm.business.entities.Type;
import thymeleafexamples.stsm.business.entities.Variety;
import thymeleafexamples.stsm.business.services.VarietyService;


@Controller
public class SeedbedMngController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private VarietyService varietyService;
    
    
    
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
    
    
    
    @RequestMapping({"/","/seedbedmng"})
    public String showSeedbedMng(final Seedbed seedbed) {
        
        if (seedbed == null) {
            System.out.println("Seedbed is null!!");
        }
        System.out.println(seedbed);
        seedbed.setDatePlanted(Calendar.getInstance().getTime());
        return "seedbedmng";
        
    }
    

    
    @RequestMapping(value="/seedbedmng", params={"addRow"})
    public String addRow(final Seedbed seedbed) {
        seedbed.getRows().add(new Row());
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
