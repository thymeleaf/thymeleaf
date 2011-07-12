/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.extrathyme.dialects.score;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.attr.IAttrProcessor;
import org.thymeleaf.processor.tag.ITagProcessor;
import org.thymeleaf.processor.value.IValueProcessor;
import org.thymeleaf.spring3.dialect.SpringThymeleafDialect;

public class ScoreDialect extends AbstractDialect {

    
    public String getPrefix() {
        return "score";
    }

    public boolean isLenient() {
        return false;
    }
    @Override
    public Set<IValueProcessor> getValueProcessors() {
        return SpringThymeleafDialect.createSpringThymeleafValueProcessorsSet();
    }

    @Override
    public Set<IAttrProcessor> getAttrProcessors() {
        final Set<IAttrProcessor> attrProcessors = new HashSet<IAttrProcessor>();
        attrProcessors.add(new ClassForPositionAttrProcessor());
        attrProcessors.add(new RemarkForPositionAttrProcessor());
        return attrProcessors;
    }

    @Override
    public Set<ITagProcessor> getTagProcessors() {
        final Set<ITagProcessor> tagProcessors = new HashSet<ITagProcessor>();
        tagProcessors.add(new HeadlinesTagProcessor());
        return tagProcessors;
    }

}
