package com.incar.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incar.base.Dispatcher;
import com.incar.base.exception.NoHandlerException;
import com.incar.business.MapTrackingStarter;
import org.springframework.beans.factory.annotation.Value;
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
    Dispatcher dispatcher;

    public PrefixController(){
        dispatcher= MapTrackingStarter.getDispatcher();
        dispatcher.getConfig().withRequestMappingPre("/ics");
        dispatcher.getDynamicRequestHandler().withJsonReader(obj->{
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @RequestMapping(value = "/ics/**",method = RequestMethod.GET)
    public void request(HttpServletRequest request, HttpServletResponse response){
        try {
            dispatcher.dispatch(request,response);
        } catch (NoHandlerException e) {
            e.printStackTrace();
        }
    }

}
