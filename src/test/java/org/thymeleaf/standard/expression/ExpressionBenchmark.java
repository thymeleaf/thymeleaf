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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.StopWatch;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class ExpressionBenchmark {

    
    
    public ExpressionBenchmark() {
        super();
    }
    
    
    public static void main(String[] args) throws Exception {
        
        final List<String> msgs = new ArrayList<String>();
        final List<String> processedMsgs = new ArrayList<String>();

        msgs.add("${x.y.z}");
        processedMsgs.add("${x.y.z}");

        msgs.add("${x.y.z[0]}");
        processedMsgs.add("${x.y.z[0]}");

        msgs.add("${x.y.z[0 + a]}");
        processedMsgs.add("${x.y.z[0 + a]}");

        msgs.add("${x.y.z['a' + 'b']}");
        processedMsgs.add("${x.y.z['a' + 'b']}");

        msgs.add("#{m.n}");
        processedMsgs.add("#{'m.n'}");

        msgs.add("#{m.n(12,34,11)}");
        processedMsgs.add("#{'m.n'(12,34,11)}");

        msgs.add("#{m.n('a','as',11)}");
        processedMsgs.add("#{'m.n'('a','as',11)}");

        msgs.add("#{m.n('a','as','its great!')}");
        processedMsgs.add("#{'m.n'('a','as','its great!')}");

        msgs.add("@{http://a.b.com}");
        processedMsgs.add("@{'http://a.b.com'}");

        msgs.add("@{http://a.b.com/xx}");
        processedMsgs.add("@{'http://a.b.com/xx'}");

        msgs.add("@{http://a.b.com/xx/yy(p1='zz')}");
        processedMsgs.add("@{'http://a.b.com/xx/yy'(p1='zz')}");

        msgs.add("@{http://a.b.com/xx/yy(p1='zz', p2=${x.y.z})}");
        processedMsgs.add("@{'http://a.b.com/xx/yy'(p1='zz',p2=${x.y.z})}");

        msgs.add("@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}");
        processedMsgs.add("@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}");

        msgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : #{m.n}");
        processedMsgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : #{'m.n'}");

        msgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : (#{m.n} ?: ${x3.y3.z3})");
        processedMsgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : (#{'m.n'} ?: ${x3.y3.z3})");

        msgs.add("${x0.y0.z0}? (#{m0.n0} ? ${x3.y3.z3} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2})");
        processedMsgs.add("${x0.y0.z0}? (#{'m0.n0'}? ${x3.y3.z3} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}?: (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1})");
        processedMsgs.add("${x0.y0.z0('a', 'b')} ?: (#{'m0.n0'('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1})");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : ${null}");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ?: ${m1.n1})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'('a','as','its great!')} ?: ${m1.n1}) : ${null}");

        msgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}");
        processedMsgs.add("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}");

        msgs.add("${x0.y0.z0}? (${x1.y1.z1}? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}) : (#{m.n} ?: ${x3.y3.z3})");
        processedMsgs.add("${x0.y0.z0}? (${x1.y1.z1}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${null}) : (#{'m.n'} ?: ${x3.y3.z3})");

        msgs.add("${x0.y0.z0}? (#{m0.n0} ? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2})");
        processedMsgs.add("${x0.y0.z0}? (#{'m0.n0'}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (#{'m0.n0'}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}? (@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})");
        processedMsgs.add("${x0.y0.z0('a', 'b')}? (@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        msgs.add("@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}? (#{m0.n0('a','as','its great!')} ? @{http://a1.b1.com/xx/yy#frag(p1=(${x1.y1.z1(0,'a',241)} ?: ${x2.y2.z2['a' + 'b']}), p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})");
        processedMsgs.add("@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}? (#{'m0.n0'('a','as','its great!')}? @{'http://a1.b1.com/xx/yy#frag'(p1=(${x1.y1.z1(0,'a',241)} ?: ${x2.y2.z2['a' + 'b']}),p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        msgs.add("${x0.y0.z0('a', 'b')}?: (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : @{http://a.b.com/xx/yy#frag(p1=(${'zz' + 23}), p2=${x.y.z})})");
        processedMsgs.add("${x0.y0.z0('a', 'b')} ?: (#{'m0.n0'('a','as','its great!')}? ${x3.y3.z3[0 + a]} : @{'http://a.b.com/xx/yy#frag'(p1=${'zz' + 23},p2=${x.y.z})})");

        

        final StandardExpressionExecutor executor  = new StandardExpressionExecutor(OgnlExpressionEvaluator.INSTANCE);
        final StandardExpressionParser parser = new StandardExpressionParser(executor);

        for (int i = 0; i < msgs.size(); i++) {
            final Expression expression = 
                parser.parseExpression(null, msgs.get(i), false);
            Assert.assertNotNull(expression);
            final String exp = expression.getStringRepresentation();
            Assert.assertEquals(exp, processedMsgs.get(i));
        }
        
        
        
        final StopWatch sw = new StopWatch();
        
        sw.start();
        
        
        for (int x = 0; x < 1000; x++)
            for (int i = 0; i < msgs.size(); i++)
                parser.parseExpression(null, msgs.get(i), false);

        sw.stop();
        
        System.out.println("First pass: " + sw.toString());
        
        sw.reset();
        sw.start();
        
        for (int x = 0; x < 1000; x++)
            for (int i = 0; i < msgs.size(); i++)
                parser.parseExpression(null, msgs.get(i), false);


        sw.stop();
        
        System.out.println("Second pass: " + sw.toString());
        
    }

    
    
    
    
}
