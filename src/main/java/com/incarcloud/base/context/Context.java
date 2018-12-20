package com.incar.base.context;


import com.incar.base.exception.NoHandlerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context extends Configurable,Dispatcher,RequestHandler,ResourceHandler{
    void handle(HttpServletRequest request, HttpServletResponse response) throws NoHandlerException;
}
