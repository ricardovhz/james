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
package org.apache.james.mailetcontainer.lib.jmx;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.james.lifecycle.api.Disposable;
import org.apache.james.mailetcontainer.api.MailProcessor;
import org.apache.james.mailetcontainer.api.MailProcessorList;
import org.apache.james.mailetcontainer.api.MailProcessorListListener;

/**
 * {@link MailProcessorListListener} implementation which register MBeans for its child {@link MailProcessor} 
 * and keep track of the stats
 *
 */
public class JMXMailProcessorListListener implements MailProcessorListListener, Disposable{

    private MailProcessorList mList;
    private MBeanServer mbeanserver;
    private List<ObjectName> mbeans = new ArrayList<ObjectName>();
    private Map<MailProcessor, MailProcessorManagement> mMap = new HashMap<MailProcessor, MailProcessorManagement>();
    public JMXMailProcessorListListener(MailProcessorList mList) throws MalformedObjectNameException, JMException {
        this.mList = mList;
        
        mbeanserver = ManagementFactory.getPlatformMBeanServer();
        registerMBeans();
    }
    
    
    /**
     * Unregister all JMX MBeans
     */
    private void unregisterMBeans() {
        List<ObjectName> unregistered = new ArrayList<ObjectName>();
        for (int i = 0; i < mbeans.size(); i++) {
            ObjectName name = mbeans.get(i);
            
            try {
                mbeanserver.unregisterMBean(name);
                unregistered.add(name);
            } catch (javax.management.JMException e) {
                //logger.error("Unable to unregister mbean " + name, e);
            }
        }
        mbeans.removeAll(unregistered);
    }


    /**
     * Register all JMX MBeans
     * @throws JMException 
     * @throws MalformedObjectNameException 
     */
    private void registerMBeans() throws MalformedObjectNameException, JMException {
       
        String baseObjectName = "org.apache.james:type=component,name=processor,";

        String[] processorNames = mList.getProcessorNames();
        for (int i = 0; i < processorNames.length; i++) {
            String processorName = processorNames[i];
            registerProcessorMBean(baseObjectName, processorName);
        }
    }
    
    /**
     * Register a JMX MBean for a {@link MailProcessor}
     * 
     * @param baseObjectName
     * @param processorName
     * @throws JMException 
     * @throws MalformedObjectNameException 
     */
    private void registerProcessorMBean(String baseObjectName, String processorName) throws MalformedObjectNameException, JMException {
        String processorMBeanName = baseObjectName + "processor=" + processorName;
        
        MailProcessorManagement processorDetail = new MailProcessorManagement(processorName);
        registerMBean(processorMBeanName, processorDetail);
        mMap.put(mList.getProcessor(processorName), processorDetail);

    }
    

    private void registerMBean(String mBeanName, Object object) throws MalformedObjectNameException, JMException{
         ObjectName objectName = new ObjectName(mBeanName);
       
        mbeanserver.registerMBean(object, objectName);
        mbeans.add(objectName);
       
    }

    /*
     * (non-Javadoc)
     * @see org.apache.james.mailetcontainer.api.MailProcessorListListener#afterProcessor(org.apache.james.mailetcontainer.api.MailProcessor, java.lang.String, long, javax.mail.MessagingException)
     */
    public void afterProcessor(MailProcessor processor, String mailName, long processTime, MessagingException e) {
        MailProcessorManagement m = mMap.get(processor);
        if (m != null) {
            m.update(processTime, e == null);
        }
    }


    /*
     * (non-Javadoc)
     * @see org.apache.james.lifecycle.api.Disposable#dispose()
     */
    public void dispose() {
        unregisterMBeans();      
        mMap.clear();
    }

}
