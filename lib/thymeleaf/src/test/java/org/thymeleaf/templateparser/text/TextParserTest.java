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
package org.thymeleaf.templateparser.text;

import java.io.CharArrayReader;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;


/*
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
public class TextParserTest {

    private static int totalTestExecutions = 0;


    @Test
    public void test() throws Exception {

        testDoc(
                "/*hello*/",
                "[T(/*hello*/){1,1}]",
                "[T(/*hello*/){1,1}]");
        testDoc(
                "a/*hello*/a/* bye! */",
                "[T(a){1,1}T(/*hello*/){1,2}T(a){1,11}T(/* bye! */){1,12}]",
                "[T(a/*hello*/a/* bye! */){1,1}]");
        testDoc(
                "/*[#hello/]*/",
                "[SES(hello){1,3}SEE(hello){1,10}]",
                "[T(/*){1,1}SES(hello){1,3}SEE(hello){1,10}T(*/){1,12}]");
        testDoc(
                "/*[#hello]*/.../*[/hello]*/",
                "[OES(hello){1,3}OEE(hello){1,10}T(...){1,13}CES(hello){1,18}CEE(hello){1,25}]",
                "[T(/*){1,1}OES(hello){1,3}OEE(hello){1,10}T(*/.../*){1,11}CES(hello){1,18}CEE(hello){1,25}T(*/){1,26}]");
        testDoc(
                "/*[#]*/.../*[/]*/",
                "[OES(){1,3}OEE(){1,5}T(...){1,8}CES(){1,13}CEE(){1,15}]",
                "[T(/*){1,1}OES(){1,3}OEE(){1,5}T(*/.../*){1,6}CES(){1,13}CEE(){1,15}T(*/){1,16}]");
        testDoc(
                "/*[# one]*/.../*[/]*/",
                "[OES(){1,3}A(one){1,6}(){1,9}(){1,9}OEE(){1,9}T(...){1,12}CES(){1,17}CEE(){1,19}]",
                "[T(/*){1,1}OES(){1,3}A(one){1,6}(){1,9}(){1,9}OEE(){1,9}T(*/.../*){1,10}CES(){1,17}CEE(){1,19}T(*/){1,20}]");
        testDoc(
                ".../*[#hello \nsrc=\"hello\" bee ]*/.../*[#bye alt  /]*//*[/hello]*/...",
                "[T(...){1,1}OES(hello){1,6}A(src){2,1}(=){2,4}(\"hello\"){2,5}A(bee){2,13}(){2,16}(){2,16}OEE(hello){2,17}T(...){2,20}SES(bye){2,25}A(alt){2,31}(){2,34}(){2,34}SEE(bye){2,36}CES(hello){2,42}CEE(hello){2,49}T(...){2,52}]",
                "[T(.../*){1,1}OES(hello){1,6}A(src){2,1}(=){2,4}(\"hello\"){2,5}A(bee){2,13}(){2,16}(){2,16}OEE(hello){2,17}T(*/.../*){2,18}SES(bye){2,25}A(alt){2,31}(){2,34}(){2,34}SEE(bye){2,36}T(*//*){2,38}CES(hello){2,42}CEE(hello){2,49}T(*/...){2,50}]");
        testDoc(
                "/*[hello]*/something;",
                "[T(/*[hello]*/){1,1}T(something;){1,12}]",
                "[T(/*[hello]*/something;){1,1}]");
        testDoc(
                "/*[(hello)]*/something;",
                "[T([(hello)]){1,3}T(;){1,23}]",
                "[T(/*[(hello)]*/something;){1,1}]");
        testDoc(
                "/*[#hello/]*/something;",
                "[SES(hello){1,3}SEE(hello){1,10}T(something;){1,14}]",
                "[T(/*){1,1}SES(hello){1,3}SEE(hello){1,10}T(*/something;){1,12}]");
        testDoc(
                "{/*[hello]*/, {asdasd}}",
                "[T({){1,1}T(/*[hello]*/){1,2}T(, {asdasd}}){1,13}]",
                "[T({/*[hello]*/, {asdasd}}){1,1}]");
        testDoc(
                "{/*[(hello)]*/, {asdasd}}",
                "[T({){1,1}T([(hello)]){1,4}T(, {asdasd}}){1,15}]",
                "[T({/*[(hello)]*/, {asdasd}}){1,1}]");
        testDoc(
                "{/*[(hello)]*/ {asdasd}}",
                "[T({){1,1}T([(hello)]){1,4}T(}){1,24}]",
                "[T({/*[(hello)]*/ {asdasd}}){1,1}]");
        testDoc(
                "/*[(hello)]*/   \n [1,2,3] one;",
                "[T([(hello)]){1,3}T(\n [1,2,3] one;){1,17}]",
                "[T(/*[(hello)]*/   \n [1,2,3] one;){1,1}]");
        testDoc(
                "/*[(hello)]*/ [1, \n      2,3] one;",
                "[T([(hello)]){1,3}T(;){2,15}]",
                "[T(/*[(hello)]*/ [1, \n      2,3] one;){1,1}]");
        testDoc(
                "{/*[(hello)]*/ /*lalala*/",
                "[T({){1,1}T([(hello)]){1,4}T(/*lalala*/){1,16}]",
                "[T({/*[(hello)]*/ /*lalala*/){1,1}]");
        testDoc(
                "{/*[(hello)]*/ /*[lalala]*/ab",
                "[T({){1,1}T([(hello)]){1,4}T(/*[lalala]*/){1,16}T(ab){1,28}]",
                "[T({/*[(hello)]*/ /*[lalala]*/ab){1,1}]");
        testDoc(
                "{/*[(hello)]*/ /*[(lalala)]*/ab",
                "[T({){1,1}T([(hello)]){1,4}T([(lalala)]){1,18}]",
                "[T({/*[(hello)]*/ /*[(lalala)]*/ab){1,1}]");
        testDoc(
                "{/*[(hello)]*/ /*[(lalala)]*/ab, 1",
                "[T({){1,1}T([(hello)]){1,4}T([(lalala)]){1,18}T(, 1){1,32}]",
                "[T({/*[(hello)]*/ /*[(lalala)]*/ab, 1){1,1}]");
        testDoc(
                "{/*[(hello)]*/ [lalala]ab",
                "[T({){1,1}T([(hello)]){1,4}]",
                "[T({/*[(hello)]*/ [lalala]ab){1,1}]");
        testDoc(
                "{/*[(hello)]*/ /*[#lalala/]*/ab",
                "[T({){1,1}T([(hello)]){1,4}SES(lalala){1,18}SEE(lalala){1,26}T(ab){1,30}]",
                "[T({/*[(hello)]*/ /*){1,1}SES(lalala){1,18}SEE(lalala){1,26}T(*/ab){1,28}]");
        testDoc(
                "{/*[(hello)]*/ [#lalala/]ab",
                "[T({){1,1}T([(hello)]){1,4}SES(lalala){1,16}SEE(lalala){1,24}T(ab){1,26}]",
                "[T({/*[(hello)]*/ ){1,1}SES(lalala){1,16}SEE(lalala){1,24}T(ab){1,26}]");
        testDoc(
                "/*[(hello)]*/",
                "[T([(hello)]){1,3}]",
                "[T(/*[(hello)]*/){1,1}]");
        testDoc(
                "",
                "[]");
        testDoc(
                "<div class= s>",
                "[T(<div class= s>){1,1}]");
        testDoc(
                "<html>",
                "[T(<html>){1,1}]");
        testDoc(
                "<html></html>",
                "[T(<html></html>){1,1}]");
        testDoc(
                "<html><body></html>",
                "[T(<html><body></html>){1,1}]");
        testDoc(
                "<html>",
                "[T(<html>){1,1}]");
        testDoc(
                "<html></html>",
                "[T(<html></html>){1,1}]");
        testDoc(
                "<html><body></html>",
                "[T(<html><body></html>){1,1}]");
        testDoc(
                "<html><title><body><p>",
                "[T(<html><title><body><p>){1,1}]");
        testDoc(
                "[[title]][[body][[p]]",
                "[T([[title]][[body][[p]]){1,1}]");
        testDoc(
                "[title][body][p]",
                "[T([title][body][p]){1,1}]");
        testDoc(
                "[[#hello]]...[[/hello]]",
                "[T([){1,1}OES(hello){1,2}OEE(hello){1,9}T(]...[){1,10}CES(hello){1,15}CEE(hello){1,22}T(]){1,23}]");
        testDoc(
                "[#hello]...[/hello]",
                "[OES(hello){1,1}OEE(hello){1,8}T(...){1,9}CES(hello){1,12}CEE(hello){1,19}]");
        testDoc(
                "...[#hello]...[/hello]...",
                "[T(...){1,1}OES(hello){1,4}OEE(hello){1,11}T(...){1,12}CES(hello){1,15}CEE(hello){1,22}T(...){1,23}]");
        testDoc(
                "...[#hello]...[#bye/][/hello]...",
                "[T(...){1,1}OES(hello){1,4}OEE(hello){1,11}T(...){1,12}SES(bye){1,15}SEE(bye){1,20}CES(hello){1,22}CEE(hello){1,29}T(...){1,30}]");
        testDoc(
                "...[#hello src=\"hello\"]...[#bye/][/hello]...",
                "[T(...){1,1}OES(hello){1,4}A(src){1,12}(=){1,15}(\"hello\"){1,16}OEE(hello){1,23}T(...){1,24}SES(bye){1,27}SEE(bye){1,32}CES(hello){1,34}CEE(hello){1,41}T(...){1,42}]");
        testDoc(
                "...[#hello src=\"hello\"]...[#bye alt=\"hello\"/][/hello]...",
                "[T(...){1,1}OES(hello){1,4}A(src){1,12}(=){1,15}(\"hello\"){1,16}OEE(hello){1,23}T(...){1,24}SES(bye){1,27}A(alt){1,33}(=){1,36}(\"hello\"){1,37}SEE(bye){1,44}CES(hello){1,46}CEE(hello){1,53}T(...){1,54}]");
        testDoc(
                "...[#hello   src=\"hello\"  ]...[#bye alt=\"hello\"  /][/hello]...",
                "[T(...){1,1}OES(hello){1,4}A(src){1,14}(=){1,17}(\"hello\"){1,18}OEE(hello){1,27}T(...){1,28}SES(bye){1,31}A(alt){1,37}(=){1,40}(\"hello\"){1,41}SEE(bye){1,50}CES(hello){1,52}CEE(hello){1,59}T(...){1,60}]");
        testDoc(
                "...[#hello \nsrc=\"hello\" ]...[#bye   /][/hello]...",
                "[T(...){1,1}OES(hello){1,4}A(src){2,1}(=){2,4}(\"hello\"){2,5}OEE(hello){2,13}T(...){2,14}SES(bye){2,17}SEE(bye){2,25}CES(hello){2,27}CEE(hello){2,34}T(...){2,35}]");
        testDoc(
                "...[#hello \nsrc=\"hello\" bee ]...[#bye alt  /][/hello]...",
                "[T(...){1,1}OES(hello){1,4}A(src){2,1}(=){2,4}(\"hello\"){2,5}A(bee){2,13}(){2,16}(){2,16}OEE(hello){2,17}T(...){2,18}SES(bye){2,21}A(alt){2,27}(){2,30}(){2,30}SEE(bye){2,32}CES(hello){2,34}CEE(hello){2,41}T(...){2,42}]");
        testDoc(
                "[#hello][/hello]",
                "[OES(hello){1,1}OEE(hello){1,8}CES(hello){1,9}CEE(hello){1,16}]");
        testDoc(
                "[#hello/]",
                "[SES(hello){1,1}SEE(hello){1,8}]");
        testDoc(
                "[#][/]",
                "[OES(){1,1}OEE(){1,3}CES(){1,4}CEE(){1,6}]");
        testDoc(
                "[#/]",
                "[SES(){1,1}SEE(){1,3}]");
        testDoc(
                "...[#   src=\"hello\"  ]...[# alt=\"hello\"  /][/]...",
                "[T(...){1,1}OES(){1,4}A(src){1,9}(=){1,12}(\"hello\"){1,13}OEE(){1,22}T(...){1,23}SES(){1,26}A(alt){1,29}(=){1,32}(\"hello\"){1,33}SEE(){1,42}CES(){1,44}CEE(){1,46}T(...){1,47}]");
        testDoc(
                "...[#   src='hello'  ]...[# alt='hello'  /][/]...",
                "[T(...){1,1}OES(){1,4}A(src){1,9}(=){1,12}('hello'){1,13}OEE(){1,22}T(...){1,23}SES(){1,26}A(alt){1,29}(=){1,32}('hello'){1,33}SEE(){1,42}CES(){1,44}CEE(){1,46}T(...){1,47}]");
        testDoc(
                "...[#   src=hello  ]...[# alt=hello  /][/]...",
                "[T(...){1,1}OES(){1,4}A(src){1,9}(=){1,12}(hello){1,13}OEE(){1,20}T(...){1,21}SES(){1,24}A(alt){1,27}(=){1,30}(hello){1,31}SEE(){1,38}CES(){1,40}CEE(){1,42}T(...){1,43}]");
        testDocError(
                "...[#   src=\"hello\"  ]...[# alt=\"hello\"  /]...",
                null,
                -1, -1);
        testDoc(
                "[#{hello}/]",
                "[T([#{hello}/]){1,1}]");
        testDoc(
                "[#template] if (a < 0) { do this} [/template]",
                "[OES(template){1,1}OEE(template){1,11}T( if (a < 0) { do this} ){1,12}CES(template){1,35}CEE(template){1,45}]");
        testDoc(
                "[#template a='zero' b='one' /]",
                "[SES(template){1,1}A(a){1,12}(=){1,13}('zero'){1,14}A(b){1,21}(=){1,22}('one'){1,23}SEE(template){1,29}]");
        testDoc(
                "[#template a='zero' b='one']\n\naaaaa\n\n[/template]",
                "[OES(template){1,1}A(a){1,12}(=){1,13}('zero'){1,14}A(b){1,21}(=){1,22}('one'){1,23}OEE(template){1,28}T(\n\naaaaa\n\n){1,29}CES(template){5,1}CEE(template){5,11}]");
        testDoc(
                "Hello, World!",
                "[T(Hello, World!){1,1}]");
        testDoc(
                "Hello, World!",
                "[T(ello, Worl){1,1}]",
                1, 10);
        testDoc(
                "[#img src=\"hello\"/]Something",
                "[SES(img){1,1}A(src){1,7}(=){1,10}(\"hello\"){1,11}SEE(img){1,18}T(Something){1,20}]");
        testDoc(
                "[#li a=\"a [# 0]\"]Hello[/li]",
                "[OES(li){1,1}A(a){1,6}(=){1,7}(\"a [# 0]\"){1,8}OEE(li){1,17}T(Hello){1,18}CES(li){1,23}CEE(li){1,27}]");
        testDoc(
                "Hello, [#p]lal$a[/p]",
                "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,11}T(lal$a){1,12}CES(p){1,17}CEE(p){1,20}]");
        testDoc(
                "Hello, [#p]l'al'a[/p]",
                "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,11}T(l'al'a){1,12}CES(p){1,18}CEE(p){1,21}]",
                Boolean.TRUE);
        testDoc(
                "Hello, [#p]l'al'a[/p]",
                "[T(Hello, ){1,1}OES(p){1,8}OEE(p){1,11}T(l'al'a){1,12}CES(p){1,18}CEE(p){1,21}]",
                Boolean.FALSE);
        testDoc(
                "Hello, [#br th:text =   'll'a=2/]",
                "[T(Hello, ){1,1}SES(br){1,8}A(th:text){1,13}( =   ){1,20}('ll'){1,25}A(a){1,29}(=){1,30}(2){1,31}SEE(br){1,32}]");
        testDoc(
                "Hello, [#br th:text = a=b/]",
                "[T(Hello, ){1,1}SES(br){1,8}A(th:text){1,13}( = ){1,20}(a=b){1,23}SEE(br){1,26}]");
        testDoc(
                "Hello, World! [#br/]\n[#div\n l\n     a=\"12 3\" zas    o=\"\"  b=\"lelo\n  = s\"]lala[/div] [#p th=\"lala\" ]liool[/p]",
                "[T(Hello, World! ){1,1}SES(br){1,15}SEE(br){1,19}T(\n" +
                        "){1,21}OES(div){2,1}A(l){3,2}(){3,3}(){3,3}A(a){4,6}(=){4,7}(\"12 3\"){4,8}A(zas){4,15}(){4,18}(){4,18}A(o){4,22}(=){4,23}(\"\"){4,24}A(b){4,28}(=){4,29}(\"lelo\n" +
                        "  = s\"){4,30}OEE(div){5,7}T(lala){5,8}CES(div){5,12}CEE(div){5,17}T( ){5,18}OES(p){5,19}A(th){5,23}(=){5,25}(\"lala\"){5,26}OEE(p){5,33}T(liool){5,34}CES(p){5,39}CEE(p){5,42}]");
        testDoc(
                "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh",
                "[T(kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh){1,1}]",
                Boolean.FALSE);
        testDoc(
                "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; [#p] aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" [/p] khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh" +
                        "kl\njasdl kjaslkj asjqq9\nk fiuh 23kj hdfkjh assd\nflkjh lkjh fdfasdfkjlh dfs" +
                        "llk\nd8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n))sad lkjsalkja aslk" +
                        "la \n&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjh" +
                        "kljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\nsdfkjlh dfs" +
                        "llkd8u u \nhkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\nkjsalkja aslk" +
                        "la &aacute;\n lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\njh",
                "[T(kl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; ){1,1}OES(p){11,13}OEE(p){11,16}T( aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" ){11,17}CES(p){22,42}CEE(p){22,45}T( khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jhkl\n" +
                        "jasdl kjaslkj asjqq9\n" +
                        "k fiuh 23kj hdfkjh assd\n" +
                        "flkjh lkjh fdfasdfkjlh dfsllk\n" +
                        "d8u u hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9\n" +
                        "))sad lkjsalkja aslkla \n" +
                        "&aacute; lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hhjhkljasdl kjaslkj asjqq9k fiuh 23kj hdfkjh assdflkjh lkjh fdfa\n" +
                        "sdfkjlh dfsllkd8u u \n" +
                        "hkkj asyu 4lk vl jhksajhd889p3rk sl a, alkj a9))sad l\n" +
                        "kjsalkja aslkla &aacute;\n" +
                        " lasd &amp; aiass da & asdll . asi ua&$\" khj askjh 1 kh ak hh\n" +
                        "jh){22,46}]",
                Boolean.FALSE);
        testDoc(
                "[#div class \n\n= \n'lala'li=\nlla][/div]",
                "[OES(div){1,1}A(class){1,7}( \n" +
                        "\n" +
                        "= \n" +
                        "){1,12}('lala'){4,1}A(li){4,7}(=\n" +
                        "){4,9}(lla){5,1}OEE(div){5,4}CES(div){5,5}CEE(div){5,10}]",
                Boolean.FALSE);


        System.out.println("TOTAL Test executions: " + totalTestExecutions);
        
        
    }



    static void testDocError(final String input, final String outputBreakDown, final int errorLine, final int errorCol) {
        testDocError(input, outputBreakDown, errorLine, errorCol, null);
    }

    static void testDocError(final String input, final String outputBreakDown, final int errorLine, final int errorCol, final Boolean processComments) {
        try {
            testDoc(input, outputBreakDown, processComments);
            throw new AssertionFailedError(null, "exception", "no exception");
            
        } catch (final TextParseException e) {
            if (errorLine != -1) {
                Assertions.assertEquals(Integer.valueOf(errorLine), e.getLine());
            } else {
                Assertions.assertNull(e.getLine());
            }
            if (errorCol != -1) {
                Assertions.assertEquals(Integer.valueOf(errorCol), e.getCol());
            } else {
                Assertions.assertNull(e.getCol());
            }
        }
    }




    static void testDoc(final String input, final String output) throws TextParseException {
        testDoc(input.toCharArray(), output, 0, input.length(), null);
    }

    static void testDoc(String input, final String output, final int offset, final int len) throws TextParseException {
        testDoc(input.toCharArray(), output, offset, len, null);
    }


    static void testDoc(final String input, final String outputCommentsProcessed, final String outputCommentsUnprocessed) throws TextParseException {
        testDoc(input.toCharArray(), outputCommentsProcessed, outputCommentsUnprocessed, 0, input.length(), null);
    }

    static void testDoc(String input, final String outputCommentsProcessed, final String outputCommentsUnprocessed, final int offset, final int len) throws TextParseException {
        testDoc(input.toCharArray(), outputCommentsProcessed, outputCommentsUnprocessed, offset, len, null);
    }


    static void testDoc(final String input, final String output, final Boolean processComments) throws TextParseException {
        testDoc(input.toCharArray(), output, 0, input.length(), processComments);
    }
    
    static void testDoc(String input, final String output, final int offset, final int len, final Boolean processComments) throws TextParseException {
        testDoc(input.toCharArray(), output, offset, len, processComments);
    }


    static void testDoc(final String input, final String outputCommentsProcessed, final String outputCommentsUnprocessed, final Boolean processComments) throws TextParseException {
        testDoc(input.toCharArray(), outputCommentsProcessed, outputCommentsUnprocessed, 0, input.length(), processComments);
    }

    static void testDoc(String input, final String outputCommentsProcessed, final String outputCommentsUnprocessed, final int offset, final int len, final Boolean processComments) throws TextParseException {
        testDoc(input.toCharArray(), outputCommentsProcessed, outputCommentsUnprocessed, offset, len, processComments);
    }

    
    
    
    static void testDoc(final char[] input, final String output,
            final int offset, final int len, final Boolean processComments) throws TextParseException {
        testDoc(input, output, output, offset, len, processComments);
    }


    static void testDoc(final char[] input, final String outputCommentsProcessed, final String outputCommentsUnprocessed,
                        final int offset, final int len, final Boolean processComments) throws TextParseException {

        final int maxBufferSize = 16384;
        for (int bufferSize = 1; bufferSize <= maxBufferSize; bufferSize++) {
            testDoc(input, outputCommentsProcessed, outputCommentsUnprocessed, offset, len, bufferSize, processComments);
        }

    }


    static void testDoc(
            final char[] input,
            final String output,
            final int offset, final int len, final int bufferSize, final Boolean processComments)
            throws TextParseException {
        testDoc(input, output, output, offset, len, bufferSize, processComments);
    }


    static void testDoc(
            final char[] input,
            final String outputCommentsProcessed, final String outputCommentsUnprocessed,
            final int offset, final int len, final int bufferSize, final Boolean processComments)
            throws TextParseException {
        if (processComments == null || processComments.booleanValue()) {
            testDoc(input, outputCommentsProcessed, offset, len, bufferSize, true);
        }
        if (processComments == null || !processComments.booleanValue()) {
            testDoc(input, outputCommentsUnprocessed, offset, len, bufferSize, false);
        }
    }



    static void testDoc(
            final char[] input, 
            final String output,
            final int offset, final int len, final int bufferSize,
            final boolean processComments)
            throws TextParseException {

        try {

            final TextParser parser = new TextParser(2, bufferSize, processComments, true);

            // TEST WITH TRACING HANDLER AND READER
            {

                final TraceBuilderTextHandler traceHandler = new TraceBuilderTextHandler();
                ITextHandler handlerChain = traceHandler;
                handlerChain = new EventProcessorTextHandler(handlerChain);
                if (processComments) {
                    handlerChain = new CommentProcessorTextHandler(true, handlerChain);
                }

                if (offset == 0 && len == input.length) {
                    parser.parseDocument(new CharArrayReader(input), bufferSize, handlerChain);
                } else { 
                    parser.parseDocument(new CharArrayReader(input, offset, len), bufferSize, handlerChain);
                }

                final List<TextTraceEvent> trace = traceHandler.getTrace();
                final StringBuilder strBuilder = new StringBuilder();
                for (final TextTraceEvent event : trace) {
                    if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_START)) {
                        strBuilder.append("[");
                    } else if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_END)) {
                        strBuilder.append("]");
                    } else {
                        strBuilder.append(event);
                    }
                }

                final String result = strBuilder.toString();
                if (output != null) {
                    Assertions.assertEquals(output, result);
                }
            }

            // TEST WITH TRACING HANDLER AND NO READER WITH PADDING
            {

                final char[] newInput = new char[len + 10];
                newInput[0] = 'X';
                newInput[1] = 'X';
                newInput[2] = 'X';
                newInput[3] = 'X';
                newInput[4] = 'X';
                System.arraycopy(input,offset,newInput,5,len);
                newInput[newInput.length - 1] = 'X';
                newInput[newInput.length - 2] = 'X';
                newInput[newInput.length - 3] = 'X';
                newInput[newInput.length - 4] = 'X';
                newInput[newInput.length - 5] = 'X';

                final TraceBuilderTextHandler traceHandler = new TraceBuilderTextHandler();
                ITextHandler handlerChain = traceHandler;
                handlerChain = new EventProcessorTextHandler(handlerChain);
                if (processComments) {
                    handlerChain = new CommentProcessorTextHandler(true, handlerChain);
                }

                parser.parseDocument(new CharArrayReader(newInput, 5, len), bufferSize, handlerChain);

                final List<TextTraceEvent> trace = traceHandler.getTrace();
                final StringBuilder strBuilder = new StringBuilder();
                for (final TextTraceEvent event : trace) {
                    if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_START)) {
                        strBuilder.append("[");
                    } else if (event.getEventType().equals(TextTraceEvent.EventType.DOCUMENT_END)) {
                        strBuilder.append("]");
                    } else {
                        strBuilder.append(event);
                    }
                }

                final String result = strBuilder.toString();
                if (output != null) {
                    Assertions.assertEquals(output, result);
                }
            }

            
            totalTestExecutions++;
            
        } catch (final AssertionFailedError cf) {
            System.err.println("Error parsing text \"" + new String(input, offset, len) + "\" with buffer size: " + bufferSize);
            throw cf;
        } catch (final Exception e) {
            throw new TextParseException("Error parsing text \"" + new String(input, offset, len) + "\" with buffer size: " + bufferSize, e);
        }
        
    }

    
}
