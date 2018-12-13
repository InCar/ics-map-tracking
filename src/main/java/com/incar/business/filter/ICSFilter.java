package com.incar.business.filter;

import com.incar.base.config.Config;
import com.incar.base.Dispatcher;
import com.incar.base.exception.NoHandlerException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(displayName = "ics-map-tracking",filterName = "ics-map-tracking",urlPatterns = "/*",asyncSupported=true)
public class ICSFilter extends HttpFilter{
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(Config.ENABLE_FILTER){
            response.setCharacterEncoding(Config.FILTER_CONFIG.getEncoding());
            Dispatcher dispatcher= new Dispatcher(Config.FILTER_CONFIG);
            try {
                dispatcher.dispatch(request,response);
            } catch (NoHandlerException e) {
                super.doFilter(request, response, chain);
            }
        }else {
            super.doFilter(request, response, chain);
        }
    }
}
