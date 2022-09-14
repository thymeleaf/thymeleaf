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
package org.thymeleaf.templateengine.stsm.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.thymeleaf.templateengine.stsm.model.Variety;
import org.thymeleaf.templateengine.stsm.model.repository.VarietyRepository;


public class StringToVarietyConverter implements Converter<String,Variety> {

    @Autowired
    private VarietyRepository varietyRepository;



    public StringToVarietyConverter() {
        super();
    }



    public Variety convert(final String source) {
        final Integer varietyId = Integer.valueOf(source);
        return this.varietyRepository.findById(varietyId);
    }


}
