package com.jak.sandbox.camel;

import org.springframework.stereotype.Component;

@Component
public class OrderHelper {
    public boolean isCsv(String body) {
        return !body.contains("<?xml");
    }

    public boolean isXml(String body) {
        return !isCsv(body);
    }
}
