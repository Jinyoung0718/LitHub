package com.sjy.LitHub.global.config.securityconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjy.LitHub.account.repository.user.UserRepository;
import com.sjy.LitHub.global.config.securityconfig.oauth2config.ClientRegistrationRepository;
import com.sjy.LitHub.global.exception.CustomAccessDeniedHandler;
import com.sjy.LitHub.global.exception.CustomAuthenticationEntryPoint;
import com.sjy.LitHub.global.exception.CustomJwtExceptionFilter;
import com.sjy.LitHub.global.security.filter.JwtFilter;
import com.sjy.LitHub.global.security.filter.LoginFilter;
import com.sjy.LitHub.global.security.handler.CustomLogoutHandler;
import com.sjy.LitHub.global.security.handler.CustomLogoutSuccessHandler;
import com.sjy.LitHub.global.security.oauth2.handler.CustomOAuth2FailureHandler;
import com.sjy.LitHub.global.security.oauth2.handler.CustomOAuth2SuccessHandler;
import com.sjy.LitHub.global.security.oauth2.service.CustomOAuth2UserService;
import com.sjy.LitHub.global.security.service.TokenService;
import com.sjy.LitHub.global.security.util.JwtUtil;
import com.sjy.LitHub.global.security.util.RedisBlacklistUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final CustomJwtExceptionFilter customJwtExceptionFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final RedisBlacklistUtil redisBlacklistUtil;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public LoginFilter loginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, UserRepository userRepository, TokenService tokenService) {
        return new LoginFilter(authenticationManager, objectMapper, userRepository, tokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginFilter loginFilter) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(redisBlacklistUtil, tokenService, jwtUtil);

        http
                .cors(cors -> cors.configurationSource(corsConfig));

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                    .requestMatchers("/api/info/temp-check").permitAll()
                    .requestMatchers("/v3/api-docs", "/swagger-ui/**").permitAll()
                    .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                    .anyRequest().authenticated()
                );

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(customJwtExceptionFilter, JwtFilter.class);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);


        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)  // 인증 실패 시 실행
                        .accessDeniedHandler(accessDeniedHandler)  // 권한 부족 시 실행
                );

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService)))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                        .clientRegistrationRepository(clientRegistrationRepository.clientRegistrationRepository())
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .permitAll());

        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}