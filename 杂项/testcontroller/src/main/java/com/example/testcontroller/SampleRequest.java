package com.example.testcontroller;

public class SampleRequest implements Request {
    private String name;

    public SampleRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}