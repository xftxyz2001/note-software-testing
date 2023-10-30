package com.example.testcontroller;

public class SampleExceptionHandler implements RequestHandler {
    public Response process(Request request) throws Exception {
        throw new Exception("error processing request");
    }
}
