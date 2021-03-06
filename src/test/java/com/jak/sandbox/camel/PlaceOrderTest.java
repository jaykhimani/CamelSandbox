package com.jak.sandbox.camel;

import org.apache.activemq.util.IOHelper;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class PlaceOrderTest extends CamelSpringTestSupport {

    @Test
    public void testPlacingOrders() throws Exception {
        // Try placing two file orders
        MockEndpoint result = getMockEndpoint("mock:orders");
        result.reset();
        result.expectedMessageCount(2);

        // clear out any old orders
        deleteDirectory("target/placeorder");
        IOHelper.mkdirs(new File("target/placeorder"));

        // place 2 file based orders
        IOHelper.copyFile(new File("src/data/message1.xml"), new File("target/placeorder/message1.xml"));
        IOHelper.copyFile(new File("src/data/message2.csv"), new File("target/placeorder/message2.csv"));

        result.assertIsSatisfied();

        // Now try placing an order via HTTP
        result.reset();
        result.expectedMessageCount(1);
        result.expectedBodiesReceived(new Order("gearbox", 5));

        String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<order name=\"gearbox\" amount=\"5\"/>";
        Object response = template.requestBody("jetty:http://localhost:8888/placeorder", body);
        // convert the response to a String
        String responseString = context.getTypeConverter().convertTo(String.class, response);
        assertStringContains("OK", responseString);

        // ensure that the order got through to the mock endpoint
        result.assertIsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }
}
