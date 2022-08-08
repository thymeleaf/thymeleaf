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
package org.thymeleaf.templateengine.dataprefix.stsm.context;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.DataBinder;
import org.thymeleaf.templateengine.dataprefix.stsm.model.Variety;
import org.thymeleaf.templateengine.dataprefix.stsm.model.repository.VarietyRepository;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.testing.templateengine.spring6.context.web.SpringMVCWebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.testable.ITest;


public class STSMWebProcessingContextBuilder extends SpringMVCWebProcessingContextBuilder {


    
    public STSMWebProcessingContextBuilder() {
        super();
        setApplicationContextConfigLocation(null);
    }

    
    @Override
    protected void initBinder(
            final String bindingVariableName, final Object bindingObject,
            final ITest test, final DataBinder dataBinder, final Locale locale, 
            final Map<String,Object> variables) {
        
        final ITestMessages messages = test.getMessages();
        if (messages == null) {
            throw new TestEngineExecutionException(
                    "Test \"" + test.getName() + "\" returns no messages object.");
        }
        
        final String dateformat = messages.computeMessage(locale, "date.format", null);
        final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        sdf.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
        
        dataBinder.registerCustomEditor(Variety.class, new VarietyPropertyEditor(new VarietyRepository()));
        
    }
    
    
    
    static class VarietyPropertyEditor extends PropertyEditorSupport {

        private final VarietyRepository varietyRepository;
        
        public VarietyPropertyEditor(final VarietyRepository varietyRepository) {
            super();
            this.varietyRepository = varietyRepository;
        }
        
        
        @Override
        public String getAsText() {
            final Variety value = (Variety) getValue();
            return (value != null ? value.getId().toString() : "");
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            final Integer varietyId = Integer.valueOf(text);
            setValue(this.varietyRepository.findById(varietyId));
        }
        
        
    }
    
    
}
