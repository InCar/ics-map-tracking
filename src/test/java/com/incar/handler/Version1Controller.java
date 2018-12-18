package com.incar.handler;

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
public class Version1Controller {

    @Value("${ics.prefix}")
    private String prefix;

    @RequestMapping(value = "/**",method = RequestMethod.GET)
    public void request(HttpServletRequest request, HttpServletResponse response){
        com.incar.base.Dispatcher dispatcher = MapTrackingStarter.getDispatcher();
        dispatcher.getConfig().withRequestMappingPre(prefix);
        try {
            dispatcher.dispatch(request,response);
        } catch (NoHandlerException e) {
            e.printStackTrace();
        }
    }

}
