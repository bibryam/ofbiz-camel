package org.ofbiz.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class DemoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file://hot-deploy/camel/data")
                .convertBodyTo(String.class)

                .setHeader("CamelOfbiz.Parameters.note", body())
                .setHeader("CamelOfbiz.Parameters.noteName", header(Exchange.FILE_NAME))

                .to("ofbiz://createNote?dispatcher=#dispatcher");
    }
}
