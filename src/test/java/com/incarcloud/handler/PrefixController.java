package com.incar.handler;

import com.incar.base.Starter;
import com.incar.base.context.Context;
import com.incar.base.exception.NoHandlerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2018/12/6.
 */
@SuppressWarnings(value = "unchecked")
@Controller
public class PrefixController {
    Context context;

    public PrefixController(){
        context= Starter.getContext();
        context.getConfig().withRequestMappingPre("/ics");
    }

    @RequestMapping(value = "/ics/**",method = RequestMethod.GET)
    public void request(HttpServletRequest request, HttpServletResponse response){
        try {
            context.handle(request,response);
        } catch (NoHandlerException e) {
            e.printStackTrace();
        }
    }

}
