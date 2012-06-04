
OFBiz-Camel connector
=====================

This component allows Camel to interact with Ofbiz by executing Ofbiz services. It also allows Ofbiz to send messages to Camel endpoints from an Ofbiz service.
It is both a Camel and Ofbiz component - it is a Camel component living inside Ofbiz and managed by Ofbiz as an Ofbiz component.
In addition it has a CamelContainer which manages CamelContext as an Ofbiz Container. It means Camel is treated as part of Ofbiz framework.

The project contains a DemoRoute, demonstrating how to poll files from hot-deploy/camel/data directory and create a note in Ofbiz by executing createNote service.
It also has a sendCamelMessage service which enables sending messages to Camel endpoints from Ofbiz serices.


HOW TO USE:
=====================

1. Copy camel folder in ofbiz/hot-deploy

2. Register the CamelContainer in ofbiz/framework/base/config/ofbiz-containers.xml by adding the following lines:

    <!-- load Camel Routing Engine -->
    <container name="camel-container" class="org.ofbiz.camel.loader.CamelContainer">
        <property name="route-name" value="org.ofbiz.camel.route.DemoRoute"/>
        <property name="dispatcher-name" value="camel-dispatcher"/>
        <property name="delegator-name" value="default"/>
    </container>

3. Camel requires concurrentlinkedhashmap-lru-1.2.jar but Ofbiz contains an older version of the library. Simply replace clhm-release-1.0-lru.jar with concurrentlinkedhashmap-lru-1.2.jar in framework/base/lib folder.

4. Compile the camel component in hot-deploy (Ofbiz uses ant as build tool, so do: ant clean jar) then run ofbiz

ENJOY