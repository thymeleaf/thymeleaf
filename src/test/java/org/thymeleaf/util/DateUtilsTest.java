/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.junit.Test;

/**
 *
 * @author Christopher Kluwe
 * 
 * @since 2.0.17
 *
 */
//CHECKSTYLE:OFF
public class DateUtilsTest {
    /**
     * constructor.
     */
    public DateUtilsTest() {
        super();
    }

    @Test
    public void testCreateToday() {
        final Calendar cal = Calendar.getInstance();
        final Calendar today = DateUtils.createToday();

        assertThat(today.get(Calendar.YEAR), is(cal.get(Calendar.YEAR)));
        assertThat(today.get(Calendar.MONTH), is(cal.get(Calendar.MONTH)));
        assertThat(today.get(Calendar.DAY_OF_MONTH), is(cal.get(Calendar.DAY_OF_MONTH)));
        assertThat(today.get(Calendar.HOUR), is(0));
        assertThat(today.get(Calendar.MINUTE), is(0));
        assertThat(today.get(Calendar.SECOND), is(0));
        assertThat(today.get(Calendar.MILLISECOND), is(0));
    }
}
//CHECKSTYLE:ON
