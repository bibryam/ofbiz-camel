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
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.ofbiz.base.container.Container;
import org.apache.ofbiz.base.container.ContainerConfig;
import org.apache.ofbiz.base.container.ContainerException;
import org.apache.ofbiz.base.start.StartupCommand;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.LocalDispatcherFactory;
import org.apache.ofbiz.service.ServiceContainer;

import java.util.List;

/**
 * A container for Apache Camel.
 */
public class CamelContainer implements Container {
    private static final String module = CamelContainer.class.getName();
//    private static LocalDispatcherFactory dispatcherFactory;
    private static ProducerTemplate producerTemplate;
    private CamelContext context;
    private String name;

    @Override
    public void init(List<StartupCommand> ofbizCommands, String name, String configFile) throws ContainerException {
        this.name = name;
//        ContainerConfig.Configuration cfg = ContainerConfig.getConfiguration(name, configFile);
//        ContainerConfig.Configuration.Property dispatcherFactoryProperty = cfg.getProperty("dispatcher-factory");
//        if (dispatcherFactoryProperty == null || UtilValidate.isEmpty(dispatcherFactoryProperty.value)) {
//            throw new ContainerException("Unable to initialize container " + name + ": dispatcher-factory property is not set");
//        }
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        try {
//            Class<?> c = loader.loadClass(dispatcherFactoryProperty.value);
//            dispatcherFactory = (LocalDispatcherFactory) c.newInstance();
//        } catch (Exception e) {
//            throw new ContainerException(e);
//        }

        context = createCamelContext();

        RouteBuilder routeBuilder = createRoutes();
        addRoutesToContext(routeBuilder);
        producerTemplate = context.createProducerTemplate();

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

    @Override
    public String getName() {
        return name;
    }

    public static ProducerTemplate getProducerTemplate() {
        if (producerTemplate == null) {
            throw new RuntimeException("ProducerTemplate not initialized");
        }
        return producerTemplate;
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


    private RouteBuilder createRoutes() throws ContainerException {
        String routeName = "org.ofbiz.camel.route.DemoRoute";
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
        Delegator delegator = DelegatorFactory.getDelegator("default");
        return ServiceContainer.getLocalDispatcher("camel-dispatcher", delegator);
//        return dispatcherFactory.createLocalDispatcher("camel-dispatcher", delegator);
    }
}
