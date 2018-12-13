package com.incar;

import javafx.application.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description
 * @Author ThomasChan
 * @Date 2018/12/13 11:06 AM
 * @Version 1.0
 */

@RestController
public class ShutdownController implements ApplicationContextAware {

    private ApplicationContext context;

    @RequestMapping("/shutdown")
    public void shutdown(HttpServletResponse response) throws IOException {
        ((ConfigurableApplicationContext) context).close();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }
}
