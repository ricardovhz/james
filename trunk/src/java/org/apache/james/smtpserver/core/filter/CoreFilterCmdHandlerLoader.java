/***********************************************************************
 * Copyright (c) 2006 The Apache Software Foundation.                  *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.james.smtpserver.core.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.james.smtpserver.CommandsHandler;


/**
 * This class represent the base filter command handlers which are shipped with james.
 */
public class CoreFilterCmdHandlerLoader implements CommandsHandler {

    private final Object DATABASEFILTERCMDHANDLER = DataFilterCmdHandler.class.getName();
    private final Object EHLOBASEFILTERCMDHANDLER = EhloFilterCmdHandler.class.getName();
    private final Object HELOBASEFILTERCMDHANDLER = HeloFilterCmdHandler.class.getName();
    private final Object MAILBASEFILTERCMDHANDLER = MailFilterCmdHandler.class.getName();
    private final Object RCPTBASEFILTERCMDHANDLER = RcptFilterCmdHandler.class.getName();
   
    /**
     * @see org.apache.james.smtpserver.CommandsHandler#getCommands()
     */
    public Map getCommands() {
        Map commands = new HashMap();
        
        // Insert the basecommands in the Map
        commands.put("DATA", DATABASEFILTERCMDHANDLER);
        commands.put("EHLO", EHLOBASEFILTERCMDHANDLER);
        commands.put("HELO", HELOBASEFILTERCMDHANDLER);
        commands.put("MAIL", MAILBASEFILTERCMDHANDLER);
        commands.put("RCPT", RCPTBASEFILTERCMDHANDLER);
        
        return commands;
    }
}
