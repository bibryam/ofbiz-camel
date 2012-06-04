/**
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
package org.ofbiz.camel.component;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeExchangeException;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.IntrospectionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The Ofbiz producer.
 */
public class OfbizProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(OfbizProducer.class);

    private final OfbizEndpoint ofbizEndpoint;
    private final String remaining;


    public OfbizProducer(OfbizEndpoint ofbizEndpoint, String remaining) {
        super(ofbizEndpoint);
        this.ofbizEndpoint = ofbizEndpoint;
        this.remaining = remaining;
    }

    public void process(Exchange exchange) throws Exception {
        String serviceName = getServiceName(exchange);
        Map<String, Object> headers = exchange.getIn().getHeaders();
        Map<String, ? extends Object> parameters = getServiceParameters(headers);

        LOG.info("Calling Ofbiz service [{}] with parameters [{}]", serviceName, parameters);
        Map<String, Object> result = ofbizEndpoint.getDispatcher().runSync(serviceName, parameters);
        LOG.info("Ofbiz service [{}] result [{}]", serviceName, result);

        exchange.getOut().setBody(result);
    }

    private String getServiceName(Exchange exchange) {
        String serviceName = exchange.getIn().getHeader(OfbizConstants.CAMEL_OFBIZ_SERVICE, this.remaining, String.class);
        if (serviceName == null) {
            throw new RuntimeExchangeException("Missing Ofbiz service name", exchange);
        }

        return serviceName;
    }

    private Map<String, Object> getServiceParameters(Map<String, Object> headers) {
        return IntrospectionSupport.extractProperties(headers, OfbizConstants.CAMEL_OFBIZ_PARAMETERS);
    }
}
