/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.messageresolver.TestMessageResolver;
import org.thymeleaf.templateresolver.TestTemplateResolver;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class ExpressionTest extends TestCase {


    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final Locale LOCALE_EN = new Locale("en", "US");

    private static final String TEMPLATE = "<!DOCTYPE html><html><body><span th:text=\"{%%}\">PLACEHOLDER</span></body></html>";
    private static final String RESULT_PREFIX = "<!DOCTYPE html>\n<html><body><span>";
    private static final String RESULT_SUFFIX = "</span></body></html>";
    
    
    private TemplateEngine templateEngine;
    private IContext contextES;
    private IContext contextEN;
    
    
    
    public ExpressionTest() {
        super();
    }
    
    
    public void testExpression() throws Exception {
        
        test("23 + 43 + 1", "67");
        test("${true}? 'x' : 'y'", "x");
        test("${false}? 'x' : 'y'", "y");
        test("${loceania.admin}", "false");
        test("!${loceania.admin}", "true");
        test("${!loceania.admin}", "true");
        test("${loceania.department.name}", "Marketing");
        test("${loceania.department.name} + ' Department'", "Marketing Department");
        test("${#calendars.monthName(loceania.creationDate)}", "noviembre", this.contextES);
        test("${#calendars.monthName(loceania.creationDate)}", "November", this.contextEN);
        test("${#calendars.monthName(loceania.creationDate)} == 'noviembre'", "true", this.contextES);
        test("${#calendars.monthName(loceania.creationDate)} == 'Noviembre'", "false", this.contextES);
        test("${#calendars.monthName(loceania.creationDate)} != 'Noviembre'", "true", this.contextES);
        test("${loceania}? 'x' : 34", "x");
        test("!${loceania}? 'x' : 34", "34");
        test("${false}? 'x'", "");
        test("${loceania.name}?: 'nobody'", "Leslie Oceania");
        test("${loceania.name} ?: 'nobody'", "Leslie Oceania");
        test("${loceania.name}  ?:'nobody'", "Leslie Oceania");
        test("${null}  ?:'nobody'", "nobody");
        test("${loceania.name != jafrica.name}? (23 - 3) / 10 : 'Number ' + 3 + 2 + '.'", "2");
        test("!${loceania.name != jafrica.name}? (23 - 3) / 10 : 'Number ' + (3 + 2) + '.'", "Number 5.");
        test("'.' + 3 + 2", ".32");
        test("3 + 2 + '.'", "5.");
        test("'Number ' + 3 + 2 + '.'", "Number 32.");
        test("'true'? 'x' : 'y'", "x");
        test("'false'? 'x' : 'y'", "y");
        test("2 + 1", "3");
        test("'2' + '1'", "21");
        test("'2' + 1", "21");
        test("2 + '1'", "21");
        test("-3 -4 - -5 + -10 - 1", "-13");
        test("-3 -4 - (-5 + -10)", "8");
        test("${loceania.priority} &gt;=  ${meurope.priority}", "false");
        test("${loceania.priority} &gt;=  ${jafrica.priority}", "true");
        test("${loceania.priority} &lt;  ${meurope.priority}", "true");
        test("${loceania.priority} &lt;=  ${jafrica.priority}", "true");
        test("${meurope.priority} &gt;  ${loceania.priority}", "true");
        test("${loceania.name} &lt; 'Mare Nostrum'", "true");
        test("#{application.name}", "Thymeleaf test");
        test("'The value is ' + 34 + (- 1 + 88.2)", "The value is 3487.2");
        test("${true} and ${true}", "true");
        test("${true} AND ${true}", "true");
        test("'x' == 'x'", "true");
        test("'4' == 4", "true");
        test("'x' EQ 'x'", "true");
        test("'4' eq 4", "true");
        test("'x' NEQ 'x'", "false");
        test("'4' neq 4", "false");
        test("5 gt 4", "true");
        test("5 GE 4", "true");
        test("5 GT 5", "false");
        test("5 ge 5", "true");
        test("4 lt 5", "true");
        test("4 LE 5", "true");
        test("5 LT 5", "false");
        test("5 le 5", "true");
        test("#{hello.message(${pamerica.name})}", "Hello, Petronila America!");
        test("#{today(${jafrica.creationDate.time},${jafrica.name})}", "Today is 23/09/2010, so hello Jacques Africa!");
        test("#{'title.dept.' + ${loceania.department.name}}", "The almighty Marketing department");
        test("#{'title.user.' + ${meurope.login}(${meurope.name}, ${meurope.department.name})}", "User Markus Europe works for the Engineering and Consulting department");
        test("@{http://a.b.com}", "http://a.b.com");
        test("@{http://a.b.com/xx}", "http://a.b.com/xx");
        test("@{http://a.b.com/xx/yy(p1='zz')}", "http://a.b.com/xx/yy?p1=zz");
        test("@{http://a.b.com/xx/yy(p1='zz', p2=${pamerica.name})}", "http://a.b.com/xx/yy?p1=zz&amp;p2=Petronila+America");
        test("@{http://a.b.com/xx/yy#frag(p1='zz', p2=${pamerica.name})}", "http://a.b.com/xx/yy?p1=zz&amp;p2=Petronila+America#frag");
        test("${loceania.permissions[0]}","Event Organizer");
        test("${loceania.permissions[3 - 2]}", "Marketing Worldwide Head");
        test("${loceania.permissions[__3.3 - 1.3__]}", "Office Master");
        test("#{priority.basic} &gt;= 3","true");
        test("#{priority.basic} &gt;= 4","false");
        test("#{priority.basic} + 10","13");
        test("@{http://a.b.com/xx/yy#frag(p1='zz', p2=((!${pamerica.isAdmin()})? ${pamerica.login} : 'Admin'))}", "http://a.b.com/xx/yy?p1=zz&amp;p2=pamerica#frag");
        test("@{'http://a.b.com/xx/yy' + '#frag'(p1='zz', p2=((!${pamerica.isAdmin()})? ${pamerica.login} : 'Admin'))}", "http://a.b.com/xx/yy?p1=zz&amp;p2=pamerica#frag");
        test("@{('http://a.b.com/xx/yy' + '#frag')(p1='zz', p2=((!${pamerica.isAdmin()})? ${pamerica.login} : 'Admin'))}", "http://a.b.com/xx/yy?p1=zz&amp;p2=pamerica#frag");

        test("${13 + '13'} == (13 + '13')","true");
        test("${13 + '13.0'} == (13 + '13.0')","true");
        test("${'13' + '13'} == ('13' + '13')","true");
        test("${'13' + 13.0} == ('13' + 13.0)","true");
        test("${'13' + '13'} == ('13' + '13')","true");
        test("${13 + 13.0} == (13 + 13.0)","true");
        
        test("${13 == '13.0'}","true");
        test("${13 == '13'} == (13 == '13')","true");
        test("${13 != '13'} == (13 != '13')","true");
        test("${13 == 13.0} == (13 == 13.0)","true");
        test("${13 != 13.0} == (13 != 13.0)","true");
        test("${13 &gt;= '13'} == (13 &gt;= '13')","true");
        test("${13 &gt; '13'} == (13 &gt; '13')","true");
        test("${13 &gt;= 13.0} == (13 &gt;= 13.0)","true");
        test("${13 &gt; 13.0} == (13 &gt; 13.0)","true");
        test("${13 &lt;= '13'} == (13 &lt;= '13')","true");
        test("${13 &lt; '13'} == (13 &lt; '13')","true");
        test("${13 &lt;= 13.0} == (13 &lt;= 13.0)","true");
        test("${13 &lt; 13.0} == (13 &lt; 13.0)","true");
        test("${13 == '13.0'} == (13 == '13.0')","true");
        test("${13 != '13.0'} == (13 != '13.0')","true");
        test("${13 &gt;= '13.0'} == (13 &gt;= '13.0')","true");
        test("${13 &gt; '13.0'} == (13 &gt; '13.0')","true");
        test("${13 &lt;= '13.0'} == (13 &lt;= '13.0')","true");
        test("${13 &lt; '13.0'} == (13 &lt; '13.0')","true");
        
        test("${14 == '13'} == (14 == '13')","true");
        test("${14 != '13'} == (14 != '13')","true");
        test("${14 == 13.0} == (14 == 13.0)","true");
        test("${14 != 13.0} == (14 != 13.0)","true");
        test("${14 &gt;= '13'} == (14 &gt;= '13')","true");
        test("${14 &gt; '13'} == (14 &gt; '13')","true");
        test("${14 &gt;= 13.0} == (14 &gt;= 13.0)","true");
        test("${14 &gt; 13.0} == (14 &gt; 13.0)","true");
        test("${14 &lt;= '13'} == (14 &lt;= '13')","true");
        test("${14 &lt; '13'} == (14 &lt; '13')","true");
        test("${14 &lt;= 13.0} == (14 &lt;= 13.0)","true");
        test("${14 &lt; 13.0} == (14 &lt; 13.0)","true");
        test("${14 == '13.0'} == (14 == '13.0')","true");
        test("${14 != '13.0'} == (14 != '13.0')","true");
        test("${14 &gt;= '13.0'} == (14 &gt;= '13.0')","true");
        test("${14 &gt; '13.0'} == (14 &gt; '13.0')","true");
        test("${14 &lt;= '13.0'} == (14 &lt;= '13.0')","true");
        test("${14 &lt; '13.0'} == (14 &lt; '13.0')","true");
        
        test("${13 == '14'} == (13 == '14')","true");
        test("${13 != '14'} == (13 != '14')","true");
        test("${13 == 14.0} == (13 == 14.0)","true");
        test("${13 != 14.0} == (13 != 14.0)","true");
        test("${13 &gt;= '14'} == (13 &gt;= '14')","true");
        test("${13 &gt; '14'} == (13 &gt; '14')","true");
        test("${13 &gt;= 14.0} == (13 &gt;= 14.0)","true");
        test("${13 &gt; 14.0} == (13 &gt; 14.0)","true");
        test("${13 &lt;= '14'} == (13 &lt;= '14')","true");
        test("${13 &lt; '14'} == (13 &lt; '14')","true");
        test("${13 &lt;= 14.0} == (13 &lt;= 14.0)","true");
        test("${13 &lt; 14.0} == (13 &lt; 14.0)","true");
        test("${13 == '14.0'} == (13 == '14.0')","true");
        test("${13 != '14.0'} == (13 != '14.0')","true");
        test("${13 &gt;= '14.0'} == (13 &gt;= '14.0')","true");
        test("${13 &gt; '14.0'} == (13 &gt; '14.0')","true");
        test("${13 &lt;= '14.0'} == (13 &lt;= '14.0')","true");
        test("${13 &lt; '14.0'} == (13 &lt; '14.0')","true");
        
        test("@{http://a.b.com/xx/yy(p1=(${pamerica.priority} == 1))}", "http://a.b.com/xx/yy?p1=true");
        test("@{http://a.b.com/xx/yy(p1=(${pamerica.login} == 'pamerica'))}", "http://a.b.com/xx/yy?p1=true");
        test("(${currentYear} - #{company.yearfounded})","35");
        
        test("${pamerica.permissions} == ${meurope.permissions}", "true");
        test("${pamerica.permissions} == ${loceania.permissions}", "false");
        test("${loceania.permissions} == ${meurope.permissions}", "false");
        

        test("@{http://a.b.com/xx/yy(p1)}", "http://a.b.com/xx/yy?p1");
        test("@{http://a.b.com/xx/yy(p1, p2=${pamerica.name})}", "http://a.b.com/xx/yy?p1&amp;p2=Petronila+America");
        test("@{http://a.b.com/xx/yy(p1='zz', p2)}", "http://a.b.com/xx/yy?p1=zz&amp;p2");
        test("@{http://a.b.com/xx/yy(p1, p2)}", "http://a.b.com/xx/yy?p1&amp;p2");
        test("@{~/xx/yy}", "/xx/yy");
        test("@{~/xx/yy(p1)}", "/xx/yy?p1");
        test("@{~/xx/yy(p1, p2=${pamerica.name})}", "/xx/yy?p1&amp;p2=Petronila+America");
    
    }

    
    

    @Override
    protected void setUp() throws Exception {
        
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(new TestTemplateResolver(TEMPLATE));
        
        this.contextES = new Context(LOCALE_ES);
        this.contextEN = new Context(LOCALE_EN);
        
        final Department deptAccounting =
            new Department(Integer.valueOf(1), "Accounting and Finance");
        final Department deptEngineering =
            new Department(Integer.valueOf(2), "Engineering and Consulting");
        final Department deptSales =
            new Department(Integer.valueOf(3), "Sales");
        final Department deptMarketing =
            new Department(Integer.valueOf(4), "Marketing");

        final Map<String,Object> objects = new HashMap<String,Object>();
        
        {
            final User user = 
                new User(
                        "loceania", "Leslie Oceania", 
                        deptMarketing, 3, toCalendar(2004, 11, 23), 
                        5.3, false, toArray("Event Organizer", "Marketing Worldwide Head", "Office Master"));
            objects.put(user.getLogin(), user);
        }
        
        {
            final User user = 
                new User(
                        "meurope", "Markus Europe", 
                        deptEngineering, 5, toCalendar(2008, 1, 3), 
                        8.0, true, null);
            objects.put(user.getLogin(), user);
        }
        
        {
            final User user = 
                new User(
                        "jafrica", "Jacques Africa", 
                        deptSales, 3, toCalendar(2010, 9, 23), 
                        4.3, false, toArray("Sales Manager", "Department Director"));
            objects.put(user.getLogin(), user);
        }
        
        {
            final User user = 
                new User(
                        "pamerica", "Petronila America", 
                        deptAccounting, 1, toCalendar(2002, 4, 19), 
                        9.2, false, null);
            objects.put(user.getLogin(), user);
        }

        objects.put("currentYear", Integer.valueOf(2011));
        
        this.contextES.getVariables().putAll(objects);
        this.contextEN.getVariables().putAll(objects);
        
        
        final Properties props = new Properties();
        props.put("application.name", "Thymeleaf test");
        props.put("hello.message", "Hello, {0}!");
        props.put("today", "Today is {0,date,dd/MM/yyyy}, so hello {1}!");
        props.put("title.dept.Marketing", "The almighty Marketing department");
        props.put("title.user.meurope", "User {0} works for the {1} department");
        props.put("priority.basic", "3");
        props.put("company.yearfounded", "1976");
        
        
        this.templateEngine.setMessageResolver(new TestMessageResolver(props));
        
    }

    
    
    private void test(final String expression, final String result) {
        test(expression, result, this.contextEN);
    }

    
    private void test(final String expression, final String result, final IContext context) {
        final String output = this.templateEngine.process(expression, context);
        final String outputResult = extractResult(output);
        Assert.assertEquals(result, outputResult);
    }
    
    
    
    private static String extractResult(final String completeResult) {
        return completeResult.substring(
                RESULT_PREFIX.length(), 
                completeResult.length() - RESULT_SUFFIX.length());
    }

    
    private static String[] toArray(final String... elements) {
        return elements;
    }
    
    private static Calendar toCalendar(int year, int month, int day) {
        return toCalendar(year, month, day, 0, 0);
    }
    
    private static Calendar toCalendar(int year, int month, int day, int hour, int minutes) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        return cal;
    }
    
    
    
    static class User {
        private final String login;
        private final String name;
        private final Department department;
        private final int priority;
        private final Calendar creationDate;
        private final double coefficient;
        private final boolean admin;
        private final String[] permissions;
        public User(String login, String name, Department department,
                int priority, Calendar creationDate, double coefficient, boolean admin, 
                String[] permissions) {
            super();
            this.login = login;
            this.name = name;
            this.department = department;
            this.priority = priority;
            this.creationDate = creationDate;
            this.coefficient = coefficient;
            this.admin = admin;
            this.permissions = permissions;
        }
        public String getLogin() {
            return this.login;
        }
        public String getName() {
            return this.name;
        }
        public Department getDepartment() {
            return this.department;
        }
        public int getPriority() {
            return this.priority;
        }
        public Calendar getCreationDate() {
            return this.creationDate;
        }
        public double getCoefficient() {
            return this.coefficient;
        }
        public boolean isAdmin() {
            return this.admin;
        }
        public String[] getPermissions() {
            return this.permissions;
        }
    }

    
    static class Department {
        private final Integer id;
        private final String name;
        public Department(final Integer id, final String name) {
            super();
            this.id = id;
            this.name = name;
        }
        public Integer getId() {
            return this.id;
        }
        public String getName() {
            return this.name;
        }
    }
    
    
}
