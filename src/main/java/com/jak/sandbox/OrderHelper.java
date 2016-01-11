package com.jak.sandbox;

public class OrderHelper {
    public boolean isCsv(String body) {
        return !body.contains("<?xml");
    }

    public boolean isXml(String body) {
        return !isCsv(body);
    }
}
