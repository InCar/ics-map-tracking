package com.incar.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incar.base.config.Config;
import com.incar.base.exception.NoHandlerException;
import com.incar.business.MapTrackingStarter;
import com.incar.handler.impl.html.HTMLHandler;
import com.incar.handler.impl.json.JSONHandler;
import com.incar.handler.impl.json.JSONReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

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
        dispatcher.getConfig().withMappingPre(prefix);
        try {
            dispatcher.dispatch(request,response);
        } catch (NoHandlerException e) {
            e.printStackTrace();
        }
    }

}
