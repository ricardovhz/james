/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/


package org.apache.james.transport.mailets;

import junit.framework.TestCase;
import org.apache.james.test.mock.mailet.MockMail;
import org.apache.james.test.mock.mailet.MockMailContext;
import org.apache.james.test.mock.mailet.MockMailetConfig;
import org.apache.james.test.mock.util.MailUtil;
import org.apache.mailet.Mailet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

public class SetMimeHeaderTest extends TestCase {

    private Mailet mailet;

    private final String HEADER_NAME = "JUNIT";

    private final String HEADER_VALUE = "test-value";

    private String headerName = "defaultHeaderName";

    private String headerValue = "defaultHeaderValue";

    public SetMimeHeaderTest(String arg0) throws UnsupportedEncodingException {
        super(arg0);
    }

    private void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    private void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    private void setupMailet() throws MessagingException {
        mailet = new SetMimeHeader();
        MockMailetConfig mci = new MockMailetConfig("Test",
                new MockMailContext());
        mci.setProperty("name", HEADER_NAME);
        mci.setProperty("value", HEADER_VALUE);

        mailet.init(mci);
    }

    // test if the Header was add
    public void testHeaderIsPresent() throws MessagingException {
        MimeMessage mockedMimeMessage = MailUtil.createMimeMessage(headerName, headerValue);
        MockMail mockedMail = MailUtil.createMockMail2Recipients(mockedMimeMessage);
        setupMailet();

        mailet.service(mockedMail);

        assertEquals(HEADER_VALUE, mockedMail.getMessage().getHeader(
                HEADER_NAME)[0]);

    }

    // test if the Header was replaced
    public void testHeaderIsReplaced() throws MessagingException {
        setHeaderName(HEADER_NAME);
        setHeaderValue(headerValue);

        MimeMessage mockedMimeMessage = MailUtil.createMimeMessage(headerName, headerValue);
        MockMail mockedMail = MailUtil.createMockMail2Recipients(mockedMimeMessage);
        setupMailet();

        mailet.service(mockedMail);

        assertEquals(HEADER_VALUE, mockedMail.getMessage().getHeader(
                HEADER_NAME)[0]);

    }
}
