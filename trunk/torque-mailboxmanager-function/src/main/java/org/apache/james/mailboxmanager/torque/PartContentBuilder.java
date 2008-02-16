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

package org.apache.james.mailboxmanager.torque;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.james.mailboxmanager.MessageResult.Content;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeTokenStream;

public class PartContentBuilder {
    
    private MimeTokenStream parser;
    
    public PartContentBuilder() {
        parser = new MimeTokenStream();
    }
    
    public void parse(final InputStream in) {
        parser.setRecursionMode(MimeTokenStream.M_RECURSE);
        parser.parse(in);
    }
    
    public void to(int position) throws IOException, MimeException {
        try {
            for (int count=0;count<position;) {
                final int state = parser.next();
                switch (state) {
                    case MimeTokenStream.T_START_BODYPART:
                        count++;
                        break;
                    case MimeTokenStream.T_START_MULTIPART:
                        if (count>0 && count<position) {
                            ignoreInnerMessage();
                        }
                        break;
                    case MimeTokenStream.T_END_OF_STREAM:
                        throw new PartNotFoundException(position);
                }
            }
        }
        catch (IllegalStateException e) {
            throw new MimeException("Cannot find part " + position, e);
        }
    }
    
    private void ignoreInnerMessage() throws IOException, MimeException {
        for (int state = parser.next(); state != MimeTokenStream.T_END_MULTIPART; state = parser.next()) {
            switch (state) {
                case MimeTokenStream.T_END_OF_STREAM:
                    throw new IOException("Unexpected EOF");
                    
                case MimeTokenStream.T_START_MULTIPART:
                    ignoreInnerMessage();
                    break;
            }
        }
    }
    
    public Content getFullContent() throws IOException, MimeException {
        final List headers = getHeaders();
        final byte[] content = bodyContent();
        return new FullContent(content, headers);
    }
    
    public Content getBodyContent() throws IOException, MimeException {
        final byte[] content = bodyContent();
        return new ByteContent(content);
    }

    private byte[] bodyContent() throws IOException, MimeException {
        parser.setRecursionMode(MimeTokenStream.M_NO_RECURSE);
        for ( int state = parser.getState(); 
                state != MimeTokenStream.T_BODY && state != MimeTokenStream.T_START_MULTIPART; 
                state = parser.next()) {
            if (state == MimeTokenStream.T_END_OF_STREAM) {
                throw new IOException("Unexpected EOF");
            }
        }
        final byte[] content = MessageUtils.toByteArray(parser.getInputStream());
        return content;
    }
    
    public List getHeaders() throws IOException, MimeException {
        final List results = new ArrayList();
        for (int state = parser.getState(); state != MimeTokenStream.T_END_HEADER; state = parser.next()) {
            switch (state) {
                case MimeTokenStream.T_END_OF_STREAM:
                    throw new IOException("Unexpected EOF");
                    
                case MimeTokenStream.T_FIELD:
                    final String fieldValue = parser.getFieldValue().trim();
                    final String fieldName = parser.getFieldName();
                    Header header = new Header(fieldName, fieldValue);
                    results.add(header);
                    break;
            }
        }
        return results;
    }
    
    public static final class PartNotFoundException extends MimeException {

        private static final long serialVersionUID = 7519976990944851574L;
        
        private final int position;
        
        public PartNotFoundException(int position) {
            super("Part " + position + " not found.");
            this.position = position;
        }

        public final int getPosition() {
            return position;
        }
        
    }
}
