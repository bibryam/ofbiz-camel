
OFBiz-Camel connector
=====================

This component allows Camel to interact with OFBiz by executing OFBiz services. It also allows Ofbiz to send messages to Camel endpoints from an Ofbiz service.
It is both a Camel and Ofbiz component - it is a Camel component living inside Ofbiz and managed by Ofbiz as an Ofbiz component.
In addition it has a CamelContainer which manages CamelContext as an Ofbiz Container. It means Camel is treated as part of Ofbiz framework.

The project contains a DemoRoute, demonstrating how to poll files from plugins/ofbiz-camel/data directory and create a note in Ofbiz by executing createNote service.
It also has a sendCamelMessage service which enables sending messages to Camel endpoints from Ofbiz serices.


HOW TO USE:
=====================

1. Copy camel folder in ofbiz/plugins

2. Compile the camel component

3. After running it should read note.txt file and create a note in OFBiz.

ENJOY