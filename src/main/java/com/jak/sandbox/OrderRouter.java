package com.jak.sandbox;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

public class OrderRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        JaxbDataFormat jaxb = new JaxbDataFormat("com.jak.sandbox");

        // Receive orders from two end points
        String jmsIncomingOrderQueue = "jms:incomingOrderQueue";
        from("file:src/data?noop=true").to(jmsIncomingOrderQueue);
        from("jetty:http://localhost:8888/placeorder").inOnly().to(jmsIncomingOrderQueue).transform().constant("OK");


        // Do the normalization
        String jmsOrderQueue = "jms:orderQueue";
        from(jmsIncomingOrderQueue).convertBodyTo(String.class).choice()
                .when().method("orderHelper", "isXml").unmarshal(jaxb).to(jmsOrderQueue)
                .when().method("orderHelper", "isCsv").unmarshal().csv().to("bean:normalizer").to(jmsOrderQueue);

    }
}
