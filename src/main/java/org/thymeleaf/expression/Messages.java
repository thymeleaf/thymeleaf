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
package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class Messages {

    private static String[] NO_PARAMETERS = new String[0];
    
    
    private Arguments arguments;
    
    
    public String msg(final String messageKey) {
        return msgWithParams(messageKey, NO_PARAMETERS);
    }
    
    public String msg(final String messageKey, final Object messageParameter0) {
        return msgWithParams(messageKey, new Object[] { messageParameter0 });
    }

    public String msg(final String messageKey, final Object messageParameter0, final Object messageParameter1) {
        return msgWithParams(messageKey, new Object[] { messageParameter0, messageParameter1 });
    }

    public String msg(final String messageKey, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return msgWithParams(messageKey, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public String msgWithParams(final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                this.arguments, messageKey, messageParameters, true);
    }

    
    
    
    public String msgOrNull(final String messageKey) {
        return msgOrNullWithParams(messageKey, NO_PARAMETERS);
    }
    
    public String msgOrNull(final String messageKey, final Object messageParameter0) {
        return msgOrNullWithParams(messageKey, new Object[] { messageParameter0 });
    }

    public String msgOrNull(final String messageKey, final Object messageParameter0, final Object messageParameter1) {
        return msgOrNullWithParams(messageKey, new Object[] { messageParameter0, messageParameter1 });
    }

    public String msgOrNull(final String messageKey, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return msgOrNullWithParams(messageKey, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public String msgOrNullWithParams(final String messageKey, final Object[] messageParameters) {
        return MessageResolutionUtils.resolveMessageForTemplate(
                this.arguments, messageKey, messageParameters, false);
    }

    
    
    
    
    
    public String[] arrayMsg(final Object[] messageKeys) {
        return arrayMsgWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public String[] arrayMsg(final Object[] messageKeys, final Object messageParameter0) {
        return arrayMsgWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public String[] arrayMsg(final Object[] messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return arrayMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public String[] arrayMsg(final Object[] messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return arrayMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public String[] arrayMsgWithParams(final Object[] messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        final String[] result = new String[messageKeys.length];
        for (int i = 0; i < messageKeys.length; i++) {
            result[i] = 
                MessageResolutionUtils.resolveMessageForTemplate(
                    this.arguments, (String)messageKeys[i], messageParameters, true);
        }
        return result;
    }
    
    
    
    
    
    public String[] arrayMsgOrNull(final Object[] messageKeys) {
        return arrayMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public String[] arrayMsgOrNull(final Object[] messageKeys, final Object messageParameter0) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public String[] arrayMsgOrNull(final Object[] messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public String[] arrayMsgOrNull(final Object[] messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return arrayMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public String[] arrayMsgOrNullWithParams(final Object[] messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        final String[] result = new String[messageKeys.length];
        for (int i = 0; i < messageKeys.length; i++) {
            result[i] = 
                MessageResolutionUtils.resolveMessageForTemplate(
                    this.arguments, (String)messageKeys[i], messageParameters, false);
        }
        return result;
    }


    
    

    
    public List<String> listMsg(final List<String> messageKeys) {
        return listMsgWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public List<String> listMsg(final List<String> messageKeys, final Object messageParameter0) {
        return listMsgWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public List<String> listMsg(final List<String> messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return listMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public List<String> listMsg(final List<String> messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return listMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public List<String> listMsgWithParams(final List<String> messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return doMsg(true, messageKeys, messageParameters);
    }



    
    
    
    public List<String> listMsgOrNull(final List<String> messageKeys) {
        return listMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public List<String> listMsgOrNull(final List<String> messageKeys, final Object messageParameter0) {
        return listMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public List<String> listMsgOrNull(final List<String> messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return listMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public List<String> listMsgOrNull(final List<String> messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return listMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public List<String> listMsgOrNullWithParams(final List<String> messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return doMsg(false, messageKeys, messageParameters);
    }

    

    
    
    
    public Set<String> setMsg(final Set<String> messageKeys) {
        return setMsgWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public Set<String> setMsg(final Set<String> messageKeys, final Object messageParameter0) {
        return setMsgWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public Set<String> setMsg(final Set<String> messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return setMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public Set<String> setMsg(final Set<String> messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return setMsgWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public Set<String> setMsgWithParams(final Set<String> messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return new LinkedHashSet<String>(doMsg(true, messageKeys, messageParameters));
    }

    
    
    
    
    public Set<String> setMsgOrNull(final Set<String> messageKeys) {
        return setMsgOrNullWithParams(messageKeys, NO_PARAMETERS);
    }
    
    public Set<String> setMsgOrNull(final Set<String> messageKeys, final Object messageParameter0) {
        return setMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0 });
    }

    public Set<String> setMsgOrNull(final Set<String> messageKeys, final Object messageParameter0, final Object messageParameter1) {
        return setMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1 });
    }

    public Set<String> setMsgOrNull(final Set<String> messageKeys, final Object messageParameter0, final Object messageParameter1, final Object messageParameter2) {
        return setMsgOrNullWithParams(messageKeys, new Object[] { messageParameter0, messageParameter1, messageParameter2 });
    }
    
    public Set<String> setMsgOrNullWithParams(final Set<String> messageKeys, final Object[] messageParameters) {
        Validate.notNull(messageKeys, "Message keys cannot be null");
        return new LinkedHashSet<String>(doMsg(false, messageKeys, messageParameters));
    }

    
    
    
    
    
    
    private List<String> doMsg(final boolean returnStringAlways, final Iterable<String> messageKeys, final Object... messageParameters) {
        final List<String> result = new ArrayList<String>();
        for (final String messageKey : messageKeys) {
            result.add(
                    MessageResolutionUtils.resolveMessageForTemplate(
                        this.arguments, messageKey, messageParameters, returnStringAlways));
        }
        return result;
    }
    
    

    
    public Messages(final Arguments arguments) {
        super();
        Validate.notNull(arguments, "Arguments cannot be null");
        this.arguments = arguments;
    }
    
}
