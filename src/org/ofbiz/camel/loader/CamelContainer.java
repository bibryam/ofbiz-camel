/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.camel.loader;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;

/**
 * A container for Apache Camel.
 */
public class CamelContainer implements Container {

    private static final String module = CamelContainer.class.getName();
    private static final String CONTAINER_NAME = "camel-container";
    private CamelContext context;
    private ContainerConfig.Container config;

    @Override
    public void init(String[] args, String configFile) throws ContainerException {
        Debug.logInfo("Initializing camel container", module);

        config = readContainerConfig(configFile);
        context = createCamelContext();
        RouteBuilder routeBuilder = createRoutes();

        addRoutesToContext(routeBuilder);
    }

    @Override
    public boolean start() throws ContainerException {
        Debug.logInfo("Starting camel container", module);

        try {
            context.start();
        } catch (Exception e) {
            throw new ContainerException(e);
        }
        return true;
    }

    @Override
    public void stop() throws ContainerException {
        Debug.logInfo("Stopping camel container", module);

        try {
            context.stop();
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private void addRoutesToContext(RouteBuilder routeBuilder) throws ContainerException {
        try {
            context.addRoutes(routeBuilder);
        } catch (Exception e) {
            Debug.logError(e, "Cannot add routes: " + routeBuilder, module);
            throw new ContainerException(e);
        }
    }

    private DefaultCamelContext createCamelContext() throws ContainerException {
        LocalDispatcher dispatcher = createDispatcher();
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("dispatcher", dispatcher);
        return new DefaultCamelContext(registry);
    }

    private ContainerConfig.Container readContainerConfig(String configFile) throws ContainerException {
        ContainerConfig.Container cc = ContainerConfig.getContainer(CONTAINER_NAME, configFile);
        if (cc == null) {
            throw new ContainerException(CONTAINER_NAME + " configuration not found in container config!");
        }
        return cc;
    }

    private RouteBuilder createRoutes() throws ContainerException {
        String routeName = ContainerConfig.getPropertyValue(config, "route-name", "org.ofbiz.camel.route.DemoRoute");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            Class<?> c = loader.loadClass(routeName);
            return (RouteBuilder) c.newInstance();
        } catch (Exception e) {
            Debug.logError(e, "Cannot get instance of the camel route: " + routeName, module);
            throw new ContainerException(e);
        }
    }

    private LocalDispatcher createDispatcher() throws ContainerException {
        String delegatorName = ContainerConfig.getPropertyValue(config, "delegator-name", "default");
        Delegator delegator = DelegatorFactory.getDelegator(delegatorName);
        String dispatcherName = ContainerConfig.getPropertyValue(config, "dispatcher-name", "camel-dispatcher");
        return GenericDispatcher.getLocalDispatcher(dispatcherName, delegator);
    }
}
