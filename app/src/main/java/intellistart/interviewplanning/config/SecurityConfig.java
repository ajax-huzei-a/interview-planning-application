package intellistart.interviewplanning.config;

import intellistart.interviewplanning.security.JwtRequestFilter;
import intellistart.interviewplanning.security.JwtUserDetailsService;
import intellistart.interviewplanning.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange()
                .pathMatchers("/authenticate", "/oauth2/facebook/v15.0").permitAll()
                .pathMatchers("/candidate/**").hasRole("CANDIDATE")
                .pathMatchers("/interviewer/**").hasRole("INTERVIEWER")
                .pathMatchers("/booking", "/booking/**", "/users", "/users/**", "/dashboard").hasRole("COORDINATOR")
                .anyExchange().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable();
        http.addFilterAt(jwtRequestFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService jwtUserDetailsService
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(jwtUserDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUserDetailsService, jwtUtil);
    }
}
