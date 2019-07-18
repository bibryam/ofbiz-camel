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
package org.apache.ofbiz.camel.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

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
		context = createCamelContext();
		ContainerConfig.Configuration cfg = ContainerConfig.getConfiguration(name, configFile);

		String packageName = ContainerConfig.getPropertyValue(cfg, "package", "org.apache.ofbiz.camel.route");

		try {
			Class<?>[] classes = getClasses(packageName);
			for (Class<?> classStr : classes) {
				System.out.println("RouteBuilder found during scanning " + classStr.getName());

				RouteBuilder routeBuilder = createRoutes(classStr.getName());
				addRoutesToContext(routeBuilder);

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// RouteBuilder routeBuilder = createRoutes();
		// addRoutesToContext(routeBuilder);
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

	private RouteBuilder createRoutes(String routeBuilderClassName) throws ContainerException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try {
			Class<?> c = loader.loadClass(routeBuilderClassName);
			return (RouteBuilder) c.newInstance();
		} catch (Exception e) {
			Debug.logError(e, "Cannot get instance of the camel route builder: " + routeBuilderClassName, module);
			throw new ContainerException(e);
		}
	}

	private LocalDispatcher createDispatcher() throws ContainerException {
		Delegator delegator = DelegatorFactory.getDelegator("default");
		return ServiceContainer.getLocalDispatcher("camel-dispatcher", delegator);
//        return dispatcherFactory.createLocalDispatcher("camel-dispatcher", delegator);
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base
	 *                    directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<? extends RouteBuilder>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<? extends RouteBuilder>> classes = new ArrayList<Class<? extends RouteBuilder>>();
		final String classExtension = ".class";
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".class") || dir.isDirectory();
			}
		});
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				String className = file.getName();
				className = className.substring(0, className.length() - classExtension.length());
				Class<?> cls = Class.forName(packageName + '.' + className);
				if (RouteBuilder.class.isAssignableFrom(cls)) {
					classes.add((Class<? extends RouteBuilder>) cls);
				}

			}
		}
		return classes;
	}

}
