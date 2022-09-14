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
package org.thymeleaf.standard.expression;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionBenchmarkDefinitions {



    private ExpressionBenchmarkDefinitions() {
        super();
    }


    
    public static Map<String,String> createExpressionsMap() {
        
        final Map<String,String> expressionsMap = new LinkedHashMap<String,String>();

        expressionsMap.put("${x.y.z}", "${x.y.z}");

        expressionsMap.put("${x.y.z[0]}", "${x.y.z[0]}");

        expressionsMap.put("${x.y.z[0 + a]}", "${x.y.z[0 + a]}");

        expressionsMap.put("${x.y.z['a' + 'b']}", "${x.y.z['a' + 'b']}");

        expressionsMap.put("#{m.n}", "#{m.n}");

        expressionsMap.put("#{m.n(12,34,11)}", "#{m.n(12,34,11)}");

        expressionsMap.put("#{m.n('a','as',11)}", "#{m.n('a','as',11)}");

        expressionsMap.put("#{m.n('a','as','its great!')}", "#{m.n('a','as','its great!')}");

        expressionsMap.put("@{http://a.b.com}", "@{'http://a.b.com'}");

        expressionsMap.put("@{http://a.b.com/xx}", "@{'http://a.b.com/xx'}");

        expressionsMap.put("@{http://a.b.com/xx/yy(p1='zz')}",
                "@{'http://a.b.com/xx/yy'(p1='zz')}");

        expressionsMap.put("@{http://a.b.com/xx/yy(p1='zz', p2=${x.y.z})}",
                "@{'http://a.b.com/xx/yy'(p1='zz',p2=${x.y.z})}");

        expressionsMap.put("@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}",
                "@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}");

        expressionsMap.put("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : #{m.n}",
                "${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : #{m.n}");

        expressionsMap.put("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : (#{m.n} ?: ${x3.y3.z3})",
                "${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : (#{m.n} ?: ${x3.y3.z3})");

        expressionsMap.put("${x0.y0.z0}? (#{m0.n0} ? ${x3.y3.z3} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2})",
                "${x0.y0.z0}? (#{m0.n0}? ${x3.y3.z3} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}?: (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1})",
                "${x0.y0.z0('a', 'b')} ?: (#{m0.n0('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : ${m1.n1})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : ${null}");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ?: ${m1.n1})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0('a','as','its great!')} ?: ${m1.n1}) : ${null}");

        expressionsMap.put("${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2}) : @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}",
                "${x0.y0.z0}? (${x1.y1.z1}? ${x2.y2.z2} : ${null}) : @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}");

        expressionsMap.put("${x0.y0.z0}? (${x1.y1.z1}? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}) : (#{m.n} ?: ${x3.y3.z3})",
                "${x0.y0.z0}? (${x1.y1.z1}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${null}) : (#{m.n} ?: ${x3.y3.z3})");

        expressionsMap.put("${x0.y0.z0}? (#{m0.n0} ? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2})",
                "${x0.y0.z0}? (#{m0.n0}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1}? ${x2.y2.z2} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (#{m0.n0} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? @{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})})",
                "${x0.y0.z0('a', 'b')}? (#{m0.n0}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? @{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}? (@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})} ? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})",
                "${x0.y0.z0('a', 'b')}? (@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}? ${x3.y3.z3[0 + a]} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        expressionsMap.put("@{http://a.b.com/xx/yy#frag(p1='zz', p2=${x.y.z})}? (#{m0.n0('a','as','its great!')} ? @{http://a1.b1.com/xx/yy#frag(p1=(${x1.y1.z1(0,'a',241)} ?: ${x2.y2.z2['a' + 'b']}), p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']})",
                "@{'http://a.b.com/xx/yy#frag'(p1='zz',p2=${x.y.z})}? (#{m0.n0('a','as','its great!')}? @{'http://a1.b1.com/xx/yy#frag'(p1=(${x1.y1.z1(0,'a',241)} ?: ${x2.y2.z2['a' + 'b']}),p2=${x.y.z})} : ${m1.n1}) : (${x1.y1.z1(0,'a',241)}? ${x2.y2.z2['a' + 'b']} : ${null})");

        expressionsMap.put("${x0.y0.z0('a', 'b')}?: (#{m0.n0('a','as','its great!')} ? ${x3.y3.z3[0 + a]} : @{http://a.b.com/xx/yy#frag(p1=(${'zz' + 23}), p2=${x.y.z})})",
                "${x0.y0.z0('a', 'b')} ?: (#{m0.n0('a','as','its great!')}? ${x3.y3.z3[0 + a]} : @{'http://a.b.com/xx/yy#frag'(p1=${'zz' + 23},p2=${x.y.z})})");

        return expressionsMap;

    }

    
    
    
    
}
