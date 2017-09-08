package me.tiezhu.http;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 为了测试passport能不能接入
 */
@Component
@EnableWebMvc
public class PassportInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("in mvc interceptor");
        System.out.println("cookies:" + request.getCookies());

        return true;
    }
}
