package com.example.testcontroller;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultController implements Controller {

    private Map requestHandlers = new HashMap();

    protected RequestHandler getHandler(Request request) {
        if (!requestHandlers.containsKey(request.getName())) {
            String message = "Cannot find handler for request name " + "[" + request.getName() + "]";
            throw new RuntimeException(message);
        }
        return (RequestHandler) requestHandlers.get(request.getName());
    }

    @Override
    public Response processRequest(Request request) {
        Response response;
        try {
            response = getHandler(request).process(request);
        } catch (Exception exception) {
            response = new ErrorResponse(request, exception);
        }
        return response;
    }

    @Override
    public void addHandler(Request request, RequestHandler requestHandler) {
        if (requestHandlers.containsKey(request.getName())) {
            throw new RuntimeException(
                    "A request handler has already been registered for request name [" + request.getName() + "]");
        } else {
            requestHandlers.put(request.getName(), requestHandler);
        }

    }

}
