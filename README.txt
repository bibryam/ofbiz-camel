
OFBiz-Camel connector
=====================

This component allows Camel and OFBiz to interact with each other.

 - It allows Ofbiz services to reach to 200+ external systems using Camel connectors.
 - It also allows external systems to send messages/events to OFBiz services using Camel that runs withing OFBiz.

The project contains a DemoRoute, demonstrating how to poll files from plugins/ofbiz-camel/data directory and create a note in OFBiz by executing createNote service.
It also has a sendCamelMessage service which enables sending messages to external systems from Ofbiz services.

![project view](https://raw.githubusercontent.com/bibryam/ofbiz-camel/master/camel-ofbiz-integration.png)

Tested to work with Apache OFBiz 16.11.04 and Apache Camel 2.21.0.

HOW TO USE:
=====================

1. Copy the project in ofbiz/plugins folder

2. Compile and run

3. After running it should read note.txt file and create a note in OFBiz database using services.

4. For any additional help, reach out to me [here](http://www.ofbizian.com/p/about.html)

ENJOY

Licensed under The MIT License.
