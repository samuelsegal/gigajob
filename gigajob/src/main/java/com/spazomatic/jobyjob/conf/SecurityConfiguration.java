package com.spazomatic.jobyjob.conf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import com.spazomatic.jobyjob.service.SimpleSocialUsersDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.jdbcAuthentication()
        .dataSource(dataSource)
        .usersByUsernameQuery(
                "select username,password, enabled from users where username=?")
        .authoritiesByUsernameQuery(
                "select username, authority from authorities where username=?");
    }
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
				.antMatchers("/static/**");
	}
    @Override
    protected void configure(HttpSecurity http) throws Exception {
/*
    	http
		.formLogin()
			.loginPage("/signin")
			.loginProcessingUrl("/signin/authenticate")
			.failureUrl("/signin?param.error=bad_credentials")
		.and()
			.logout()
				.logoutUrl("/signout")
				.deleteCookies("JSESSIONID")
		.and()
			.authorizeRequests()
				.antMatchers("/admin/**", "/favicon.ico", 
						"/static/**","/static/img/**", 
						"/auth/**", "/signin/**", 
						"/signup/**", "/disconnect/facebook").permitAll()
				.antMatchers("/**").authenticated()
				.antMatchers("/client/**").access("hasRole('CLIENT')")
				.antMatchers("/provider/**").access("hasRole('PROVIDER')")
		.and()
			.rememberMe()
		.and()
			.apply(
					new SpringSocialConfigurer()
	                .postLoginUrl("/")
	                .alwaysUsePostLoginUrl(true));
 */
    	http
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/authenticate")
                .failureUrl("/login?param.error=bad_credentials")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
        .and()
            .authorizeRequests()
                .antMatchers("/favicon.ico", "/static/**").permitAll()
                .antMatchers("/**").authenticated()
        .and()
            .rememberMe()
        .and()
            .apply(new SpringSocialConfigurer());
                
    }

    @Bean
    public SocialUserDetailsService socialUsersDetailService() {
        return new SimpleSocialUsersDetailService(userDetailsService());
    }
}