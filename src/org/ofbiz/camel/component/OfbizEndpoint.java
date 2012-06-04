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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.ofbiz.service.LocalDispatcher;

/**
 * Represents a Ofbiz endpoint.
 */
public class OfbizEndpoint extends DefaultEndpoint {
    private String remaining;
    private LocalDispatcher dispatcher;

    public OfbizEndpoint(String uri, OfbizComponent component, String remaining) {
        super(uri, component);
        this.remaining = remaining;
    }

    public Producer createProducer() throws Exception {
        return new OfbizProducer(this, remaining);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported for Ofbiz endpoint");
    }

    public boolean isSingleton() {
        return true;
    }

    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
