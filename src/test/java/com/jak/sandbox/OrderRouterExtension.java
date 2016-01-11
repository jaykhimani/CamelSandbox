package com.jak.sandbox;

import org.apache.camel.builder.RouteBuilder;

public class OrderRouterExtension extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jms:orderQueue").to("mock:orders");
    }
}
