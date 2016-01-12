package com.jak.sandbox.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderRouter extends RouteBuilder {
    public static final Logger LOG = LoggerFactory.getLogger(OrderRouter.class);

    @Override
    public void configure() throws Exception {
        LOG.info("Configuring");
        JaxbDataFormat jaxb = new JaxbDataFormat("com.jak.sandbox.camel");

        // Receive orders from two end points
        String jmsIncomingOrderQueue = "jms:incomingOrderQueue";
        from("file:src/data?noop=true").to(jmsIncomingOrderQueue);
        from("jetty:http://localhost:8888/placeorder").inOnly().to(jmsIncomingOrderQueue).transform().constant("OK");


        // Do the normalization
        String jmsOrderQueue = "jms:orderQueue";
        from(jmsIncomingOrderQueue).convertBodyTo(String.class).choice()
                .when().method("orderHelper", "isXml").unmarshal(jaxb).to(jmsOrderQueue)
                .when().method("orderHelper", "isCsv").unmarshal().csv().to("bean:orderNormalizer").to(jmsOrderQueue);

    }
}
