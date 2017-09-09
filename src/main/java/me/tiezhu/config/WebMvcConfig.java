package me.tiezhu.config;

import me.tiezhu.http.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 这个是mvc的配置，如果加上了会改变默认静态资源mapping
 * FIXME 先拿掉吧
 */
@Deprecated
// @Configuration
// @EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private PassportInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/*");
    }
}
