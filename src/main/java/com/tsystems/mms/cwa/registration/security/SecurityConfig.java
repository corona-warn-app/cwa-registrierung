package com.tsystems.mms.cwa.registration.security;

import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;


/**
 * Configures Basic Auth for export method call.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Configuration
@EnableWebSecurity
@Profile(value = {"!test"})
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${config.role.partner_export}")
    private String roleCSV = "CWA_CSV_EXPORT_USER";

    @Value("${config.role.attachment_export}")
    private String roleFile = "CWA_ATTACHMENT_EXPORT_USER";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/export/attachment**").hasAuthority(roleFile)
                .antMatchers("/export").hasAuthority(roleCSV)
                .anyRequest().permitAll();
    }

    @Override
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    @Bean
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return super.keycloakAuthenticationProvider();
    }

}


