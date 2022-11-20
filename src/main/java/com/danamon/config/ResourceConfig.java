package com.danamon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
@EnableWebSecurity
public class ResourceConfig extends WebSecurityConfigurerAdapter{

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();

        // (optional) Send a 401 message to the browser (w/o this, you'll see a blank page)
//        Okta.configureResourceServer401ResponseBody(http);
//        http.build();
    }
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable();
////                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
////                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
////                .antMatchers("/api/test/**").permitAll()
////                .anyRequest().authenticated();
//
//        // http....;
//
//        return http.build();
//    }
}

