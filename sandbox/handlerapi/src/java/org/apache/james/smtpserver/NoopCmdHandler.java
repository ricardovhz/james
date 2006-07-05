/***********************************************************************
 * Copyright (c) 1999-2006 The Apache Software Foundation.             *
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

package org.apache.james.smtpserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.james.util.mail.dsn.DSNStatus;

/**
  * Handles NOOP command
  */
public class NoopCmdHandler extends AbstractCommandHandler {

    /**
     * The name of the command handled by the command handler
     */
    private final static String COMMAND_NAME = "NOOP";

    /**
     * Handler method called upon receipt of a NOOP command.
     * Just sends back an OK and logs the command.
     *
     * @see org.apache.james.smtpserver.CommandHandler#onCommand(SMTPSession)
     */
    public void onCommand(SMTPSession session) {
        String responseString = "250 "+DSNStatus.getStatus(DSNStatus.SUCCESS,DSNStatus.UNDEFINED_STATUS)+" OK";
        session.writeResponse(responseString);
    }
    
    /**
     * @see org.apache.james.smtpserver.CommandHandler#getImplCommands()
     */
    public List getImplCommands() {
        ArrayList implCommands = new ArrayList();
        implCommands.add("NOOP");
        
        return implCommands;
    }
}
