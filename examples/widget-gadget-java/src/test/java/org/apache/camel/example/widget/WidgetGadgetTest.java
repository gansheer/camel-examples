/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.widget;

import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelConfiguration;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.main.MainConfigurationProperties;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.main.junit5.CamelMainTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A unit test checking that the Widget and Gadget use-case from the Enterprise Integration Patterns book works
 * properly using Apache ActiveMQ.
 */
class WidgetGadgetTest extends CamelMainTestSupport {
    public static class BrokerConfiguration implements CamelConfiguration {

        @org.apache.camel.BindToRegistry("activemq")
        public JmsComponent createActiveMQbroker() throws Exception {
            ConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
            return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
        }
    }

    @Test
    void should_distribute_orders() {
        NotifyBuilder notify = new NotifyBuilder(context)
                .whenCompleted(1).wereSentTo("activemq:queue:widget")
                .and().whenCompleted(1).wereSentTo("activemq:queue:gadget")
                .create();

        assertTrue(
            notify.matches(10, TimeUnit.SECONDS),
            "One order should be distributed to widget and and the other to gadget"
        );
    }

    @Override
    protected void configure(MainConfigurationProperties configuration) {
        configuration.addConfiguration(BrokerConfiguration.class);
        configuration.addRoutesBuilder(new WidgetGadgetRoute());
        configuration.addRoutesBuilder(new CreateOrderRoute());
    }
}
