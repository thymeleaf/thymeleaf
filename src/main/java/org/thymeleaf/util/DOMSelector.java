package org.thymeleaf.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;

final class DOMSelector {

    private static final String selectorPatternStr =
            "(/{1,2})([^/\\s]*?)(?:\\[(.*?)\\](?:\\[(.*?)\\])?)?";
    private static final Pattern selectorPattern =
            Pattern.compile(selectorPatternStr);
    
    private final boolean descendMoreThanOneLevel;
    private final String selectorName;
    private final boolean text;
    private Map<String,String> attributes = null;
    private Integer index = null; // will be -1 if last()
    private boolean attributesBeforeIndex = false;
    
    private final DOMSelector next;
    
    
    
    public DOMSelector(final String selectorSpec) {
        
        super();

        String selectorSpecStr =
            (selectorSpec.trim().startsWith("/")? selectorSpec.trim() : "/" + selectorSpec.trim());
        
        final int selectorSpecStrLen = selectorSpecStr.length();
        int firstNonSlash = 0;
        while (firstNonSlash < selectorSpecStrLen && selectorSpecStr.charAt(firstNonSlash) == '/') {
            firstNonSlash++;
        }
        
        if (firstNonSlash >= selectorSpecStrLen) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
        }
        
        final int selEnd = selectorSpecStr.substring(firstNonSlash).indexOf('/');
        if (selEnd != -1) {
            final String tail = selectorSpecStr.substring(firstNonSlash).substring(selEnd);
            selectorSpecStr = selectorSpecStr.substring(0, firstNonSlash + selEnd);
            this.next = new DOMSelector(tail);
        } else {
            this.next = null;
        }
        System.out.println("Processing: " + selectorSpecStr);
        final Matcher matcher = selectorPattern.matcher(selectorSpecStr);
        if (!matcher.matches()) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
        }
        
        final String rootGroup = matcher.group(1);
        final String selectorNameGroup = matcher.group(2);
        final String index1Group = matcher.group(3);
        final String index2Group = matcher.group(4);
        
        if (rootGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
        }
        
        if ("//".equals(rootGroup)) {
            this.descendMoreThanOneLevel = true;
        } else if ("/".equals(rootGroup)) {
            this.descendMoreThanOneLevel = false;
        } else {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
        }
        
        if (selectorNameGroup == null) {
            throw new TemplateProcessingException(
                    "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
        }
        
        this.selectorName = Node.normalizeName(selectorNameGroup);
        this.text = this.selectorName.equals("text()");
        
        if (index1Group != null) {
            
            Integer ind = parseIndex(index1Group);
            if (ind == null) {
                Map<String,String> attribs = parseAttributes(selectorSpec, index1Group);
                if (attribs == null) {
                    throw new TemplateProcessingException(
                            "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
                }
                this.attributes = attribs;
                this.attributesBeforeIndex = true;
            } else {
                this.index = ind;
                this.attributesBeforeIndex = false;
            }

            if (index2Group != null) {
                
                if (ind != null) {
                    Map<String,String> attribs = parseAttributes(selectorSpec, index2Group);
                    if (attribs == null) {
                        throw new TemplateProcessingException(
                                "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
                    }
                    this.attributes = attribs;
                } else {
                    ind = parseIndex(index1Group);
                    if (ind == null) {
                        throw new TemplateProcessingException(
                                "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
                    }
                    this.index = ind;
                }
                
            }
            
        }
        
        System.out.println(toString());
        
    }
    
    
    
    private static Integer parseIndex(final String indexGroup) {
        if ("last()".equals(indexGroup.toLowerCase())) {
            return Integer.valueOf(-1);
        }
        try {
            return Integer.valueOf(indexGroup);
        } catch (final Exception e) {
            return null;
        }
    }
    

    
    private static Map<String,String> parseAttributes(final String selectorSpec, final String indexGroup) {
        final Map<String,String> attributes = new HashMap<String, String>();
        parseAttributes(selectorSpec, attributes, indexGroup);
        return attributes;
    }

    
    private static void parseAttributes(final String selectorSpec, final Map<String,String> attributes, final String indexGroup) {
        
        String att = null;
        final int andPos = indexGroup.indexOf(" and "); 
        if (andPos != -1) {
            att = indexGroup.substring(0,andPos);
            final String tail = indexGroup.substring(andPos + 5);
            parseAttributes(selectorSpec, attributes, tail);
        } else {
            att = indexGroup;
        }
            
        parseAttribute(selectorSpec, attributes, att);
        
    }

    
    
    private static void parseAttribute(final String selectorSpec, final Map<String,String> attributes, final String attributeSpec) {
        
        final int eqPos = attributeSpec.indexOf("="); 
        if (eqPos != -1) {
            final String attName = attributeSpec.substring(0, eqPos).trim();
            final String attValue = attributeSpec.substring(eqPos + 1).trim();
            if (!attName.startsWith("@")) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            if (!attValue.startsWith("\"") || !attValue.endsWith("\"")) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            attributes.put(attName.substring(1), attValue.substring(1, attValue.length() - 1));
        } else {
            final String attName = attributeSpec.trim();
            if (!attName.startsWith("@")) {
                throw new TemplateProcessingException(
                        "Invalid syntax in DOM selector: \"" + selectorSpec + "\"");
            }
            attributes.put(attName.substring(1), null);
        }
    }

    
    final List<Node> select(final Node node) {
        return null;
    }

    
    @Override
    public final String toString() {
        return this.descendMoreThanOneLevel + " | " + this.selectorName + 
                " | " + this.attributesBeforeIndex + "  | " + this.attributes + "  | " + this.index +
                " >> " + this.next;
    }

    
    
    
    public static void main(String[] args) {
        
        try {
            
            final String[] msgs = new String[] {
                    "aa", "/aa", "//aaaa", "///as",
                    "//div[@lala=\"nope\" and @leor]", "//div[1][aaa]", "//div[last()][@aaa]",
                    "//div[last()][@aaa]/p/ul[@class=\"hey\"]//li"
            };

            
            for (final String msg : msgs) {
                try {
                    new DOMSelector(msg);
                } catch (Exception e) {
                    
                }
            }
            
            
        } catch (Exception e) {
            
        }
    
    }
   
    
}