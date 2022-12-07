package org.blokaj.configurations;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.blokaj.security.AuthenticationEntryPoint;
import org.blokaj.security.AuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true, jsr250Enabled = true)
public class AuthenticationSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @NonNull
    public final PasswordEncoder passwordEncoder;

    @NonNull
    public final UserDetailsService userDetailsService;

    @NonNull
    private final AuthenticationEntryPoint authenticationTokenComponent;

    @NonNull
    private final AuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .csrf()
                .disable()
            .exceptionHandling().authenticationEntryPoint(authenticationTokenComponent)
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers("/api/v1/login/**","/api/v1/refresh-token/**").permitAll()
                .antMatchers("/docs/api/v1/v3/api-docs/**","/docs/api/v1/swagger-ui/**","/docs/api/v1/swagger-ui.html").permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().cacheControl();
    }

}
