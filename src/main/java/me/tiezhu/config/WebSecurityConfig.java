package me.tiezhu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Created by liushuai on 2017/9/7.
 *
 * 这个貌似是跟spring登录业务配合使用的，暂时用不上
 */
@Deprecated
// @EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // @Bean
    @Override
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("password").roles("USER").build());
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**")
            .authorizeRequests()
            .anyRequest().hasRole("ADMIN")
            .and()
            .httpBasic();
    }
}
