package me.tiezhu.config;

import me.tiezhu.http.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 这里是WebMvcAutoConfigurationAdapter的补充配置
 * 只要不@EnableWebMvc，这个配置会作为默认配置的一部分被加载
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PassportInterceptor()).addPathPatterns("/*");
    }
}
