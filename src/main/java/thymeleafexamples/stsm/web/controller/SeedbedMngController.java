package thymeleafexamples.stsm.web.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import thymeleafexamples.stsm.business.entities.Feature;
import thymeleafexamples.stsm.business.entities.Seedbed;
import thymeleafexamples.stsm.business.entities.Type;


@Controller
public class SeedbedMngController {

    @Autowired
    private MessageSource messageSource;

    
    
    @InitBinder
    public void initDateBinder(final WebRequest request, final WebDataBinder dataBinder) {
        
        final String dateformat = 
            this.messageSource.getMessage("date.format", null, request.getLocale());
        
        final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        sdf.setLenient(false);
        
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
        
    }
    
    
    
    @ModelAttribute("types")
    public List<Type> populateTypes() {
        return Arrays.asList(Type.ALL);
    }
    
    @ModelAttribute("features")
    public List<Feature> populateFeatures() {
        return Arrays.asList(Feature.ALL);
    }
    
    
    
    @RequestMapping({"/","/seedbedmng"})
    public String showSeedbedMng(final Seedbed seedbed, final Map<String,Object> model) {
        
        if (seedbed == null) {
            System.out.println("Seedbed is null!!");
        }
        System.out.println(seedbed);
        seedbed.setDatePlanted(Calendar.getInstance().getTime());
        return "seedbedmng";
        
    }
    
    
}
