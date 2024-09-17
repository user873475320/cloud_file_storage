package ru.storage.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors ->
                        cors.configurationSource(request ->
                                new CorsConfiguration().applyPermitDefaultValues()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/auth/login", "/auth/process_login", "/auth/registration", "/auth/process_registration").not().authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(s -> s
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/?loginAgain"))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/process_login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/auth/login?error"))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout"))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getServletPath().equals("/auth/login") || request.getServletPath().equals("/") || request.getServletPath().equals("/auth/registration")) {
                                response.sendRedirect("/home?logoutFirst");
                            } else {
                                response.sendRedirect("/error/403");
                            }
                        })
                        .authenticationEntryPoint(((request, response, authException) ->
                                response.sendRedirect("/?loginFirst"))));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
