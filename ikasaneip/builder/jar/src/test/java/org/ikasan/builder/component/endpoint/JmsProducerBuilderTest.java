/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.component.endpoint.JmsProducerBuilder;
import org.ikasan.builder.component.endpoint.JmsProducerBuilderImpl;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.endpoint.jms.spring.producer.SpringMessageProducerConfiguration;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Test;
import org.springframework.jms.core.IkasanJmsTemplate;

import javax.naming.Context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>ComponentBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class JmsProducerBuilderTest {

    @Test
    public void test_jmsproducerbuilder_build() {

        JmsTemplateProducer jmsTemplateProducer = new JmsTemplateProducer(new IkasanJmsTemplate());
        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(jmsTemplateProducer);

        Producer jmsProducer = jmsProducerBuilder
                .setDestinationJndiName("jms.queue.test")
                .setConnectionFactoryName("TestConnectionFactory")
                .setConnectionFactoryUsername("TestUsername")
                .setConnectionFactoryPassword("TestPassword")
                .setSessionAcknowledgeMode(2)
                .setSessionAcknowledgeModeName("TRANSACTED")
                .setSessionTransacted(true)
                .setPubSubDomain(true)
                .setDeliveryPersistent(true)
                .setDeliveryMode(2)
                .setPriority(2)
                .setExplicitQosEnabled(true)
                .setMessageIdEnabled(true)
                .setMessageTimestampEnabled(true)
                .setPubSubNoLocal(true)
                .setReceiveTimeout(2000l)
                .setTimeToLive(2000l)
                .build();

        assertTrue("instance should be a JmsProducer", jmsProducer instanceof JmsTemplateProducer);
        SpringMessageProducerConfiguration configuration = (
                (ConfiguredResource< SpringMessageProducerConfiguration>) jmsProducer).getConfiguration();
        assertEquals("DestinationJndiName should be 'jms.queue.test'", "jms.queue.test",
                configuration.getDestinationJndiName());
        assertEquals("ConnectionFactoryName should be 'TestConnectionFactory'", "TestConnectionFactory",
                configuration.getConnectionFactoryName());
        assertEquals("ConnectionFactoryUsername should be 'TestUsername'", "TestUsername",
                configuration.getConnectionFactoryUsername());
        assertEquals("ConnectionFactoryPassword should be 'TestPassword'", "TestPassword",
                configuration.getConnectionFactoryPassword());
        assertEquals("SessionAcknowledgeMode should be '2'", 2,
                configuration.getSessionAcknowledgeMode().intValue());
        assertEquals("SessionAcknowledgeModeName should be 'TRANSACTED'", "TRANSACTED",
                configuration.getSessionAcknowledgeModeName());
        assertTrue("SessionTransacted should be 'true'",
                configuration.getSessionTransacted());
        assertTrue("PubSubDomain should be 'true'",
                configuration.getPubSubDomain());
        assertTrue("DeliveryPersistent should be 'true'",
                configuration.getDeliveryPersistent());
        assertEquals("DeliveryMode should be 'true'",2,
                configuration.getDeliveryMode().intValue());
        assertEquals("DeliveryMode should be 'true'",2,
                configuration.getDeliveryMode().intValue());
        assertEquals("Priority should be 'true'",2,
                configuration.getPriority().intValue());
        assertTrue("ExplicitQosEnabled should be 'true'",
                configuration.getExplicitQosEnabled());
        assertTrue("MessageIdEnabled should be 'true'",
                configuration.getMessageIdEnabled());
        assertTrue("MessageTimestampEnabled should be 'true'",
                configuration.getMessageTimestampEnabled());
        assertTrue("PubSubNoLocal should be 'true'",
                configuration.getPubSubNoLocal());
        assertEquals("ReceiveTimeout should be 'true'",2000l,
                configuration.getReceiveTimeout().longValue());
        assertEquals("TimeToLive should be 'true'",2000l,
                configuration.getTimeToLive().longValue());

    }


    @Test
    public void test_jmsproducerbuilder_build_verify_properties() {
        JmsTemplateProducer jmsTemplateProducer = new JmsTemplateProducer(new IkasanJmsTemplate());
        JmsProducerBuilder jmsProducerBuilder = new JmsProducerBuilderImpl(jmsTemplateProducer);

        Producer jmsProducer = jmsProducerBuilder
                .setDestinationJndiPropertyFactoryInitial("testinitialFactory")
                .setDestinationJndiPropertyUrlPkgPrefixes("testurlpkg")
                .setDestinationJndiPropertyProviderUrl("testiurl")
                .setDestinationJndiPropertySecurityCredentials("testicredentails")
                .setDestinationJndiPropertySecurityPrincipal("testprinciple")
                .setConnectionFactoryJndiPropertyFactoryInitial("testinitialFactory")
                .setConnectionFactoryJndiPropertyProviderUrl("testiurl")
                .setConnectionFactoryJndiPropertySecurityCredentials("testicredentails")
                .setConnectionFactoryJndiPropertySecurityPrincipal("testprinciple")
                .setConnectionFactoryJndiPropertyUrlPkgPrefixes("testurlpkg")
                .build();

        assertTrue("instance should be a JmsProducer", jmsProducer instanceof JmsTemplateProducer);
        SpringMessageProducerConfiguration configuration = (
                (ConfiguredResource< SpringMessageProducerConfiguration>) jmsProducer).getConfiguration();
        assertEquals("ConnectionFactoryJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'",
                "testinitialFactory",
                configuration.getConnectionFactoryJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY));
        assertEquals("ConnectionFactoryJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'",
                "testurlpkg",
                configuration.getConnectionFactoryJndiProperties().get(Context.URL_PKG_PREFIXES));
        assertEquals("ConnectionFactoryJndiProperties(PROVIDER_URL) should be 'testiurl'",
                "testiurl",
                configuration.getConnectionFactoryJndiProperties().get(Context.PROVIDER_URL));
        assertEquals("ConnectionFactoryJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'",
                "testicredentails",
                configuration.getConnectionFactoryJndiProperties().get(Context.SECURITY_CREDENTIALS));
        assertEquals("ConnectionFactoryJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'",
                "testprinciple",
                configuration.getConnectionFactoryJndiProperties().get(Context.SECURITY_PRINCIPAL));
        assertEquals("DestinationJndiProperties(INITIAL_CONTEXT_FACTORY) should be 'testinitialFactory'",
                "testinitialFactory",
                configuration.getDestinationJndiProperties().get(Context.INITIAL_CONTEXT_FACTORY));
        assertEquals("DestinationJndiProperties(URL_PKG_PREFIXES) should be 'testurlpkg'",
                "testurlpkg",
                configuration.getDestinationJndiProperties().get(Context.URL_PKG_PREFIXES));
        assertEquals("DestinationJndiProperties(java.naming.provider.url) should be 'testurl'",
                "testiurl",
                configuration.getDestinationJndiProperties().get(Context.PROVIDER_URL));
        assertEquals("DestinationJndiProperties(SECURITY_CREDENTIALS) should be 'testicredentails'",
                "testicredentails",
                configuration.getDestinationJndiProperties().get(Context.SECURITY_CREDENTIALS));
        assertEquals("DestinationJndiProperties(SECURITY_PRINCIPAL) should be 'testprinciple'",
                "testprinciple",
                configuration.getDestinationJndiProperties().get(Context.SECURITY_PRINCIPAL));

    }

}