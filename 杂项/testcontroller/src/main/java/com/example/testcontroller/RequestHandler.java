package com.example.testcontroller;

public interface RequestHandler {
    Response process(Request request) throws Exception;
}
