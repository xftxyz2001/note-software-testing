package com.example.testcontroller;

public class SampleHandler implements RequestHandler {
    public Response process(Request request) throws Exception {
        return new SampleResponse();
    }
}