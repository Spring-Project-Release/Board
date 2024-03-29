package com.release.core.config;

import com.release.core.config.auth.MyLoginSuccessHandler;
import com.release.core.config.auth.MyLogoutSuccessHandler;
import com.release.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private static final Logger log = LogManager.getLogger(SecurityConfig.class);


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //Make the below setting as * to allow connection from any hos
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    /*
@Bean
public WebSecurityCustomizer confiure(){
    return (web) -> web.ignoring()
            .requestMatchers(toH2Console())
            .requestMatchers("/static/**");
}
 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(new OrRequestMatcher(
                                        new AntPathRequestMatcher("user/login"),
                                        new AntPathRequestMatcher("user/join")
                                        // 여기에 추가적인 URL 패턴을 필요한 만큼 나열할 수 있습니다.
                                )).permitAll()
                                .requestMatchers(new OrRequestMatcher(
                                        new AntPathRequestMatcher("user/info"),
                                        new AntPathRequestMatcher("user/editUserName"),
                                        new AntPathRequestMatcher("user/editUserPassword"),
                                        new AntPathRequestMatcher("user/delete"),
                                        new AntPathRequestMatcher("post-write"),
                                        new AntPathRequestMatcher("post-edit"),
                                        new AntPathRequestMatcher("post-delete"),
                                        new AntPathRequestMatcher("bookmark-save"),
                                        new AntPathRequestMatcher("bookmark-load"),
                                        new AntPathRequestMatcher("bookmark-delete")
                                        // 여기에 추가적인 URL 패턴을 필요한 만큼 나열할 수 있습니다.
                                )).authenticated()
                                .requestMatchers(
                                        new AntPathRequestMatcher("user/admin/**")
                                        // 여기에 추가적인 URL 패턴을 필요한 만큼 나열할 수 있습니다.
                                ).hasAnyAuthority("ADMIN")
                                // 개발 완료 시 비활성화
                                //.requestMatchers(PathRequest.toH2Console()).permitAll()
                                //.requestMatchers("/posts/**", "/api/v1/posts/**").hasRole(User.UserRole.USER.name())
                                //.requestMatchers("/admins/**", "/api/v1/admins/**").hasRole(User.UserRole.ADMIN.name())
                                .anyRequest().permitAll()
                )
                /*
                .exceptionHandling((exception)->
                        exception
                                .accessDeniedHandler(new MyAccessDeniedHandler(userRepository))           // 인가 실패
                                .authenticationEntryPoint(new MyAuthenticationEntryPoint()) // 인증 실패
                )
                 */
                .formLogin((formLogin) ->
                        formLogin
                                //.loginPage("/custom-login")
                                .usernameParameter("userEmail")
                                .passwordParameter("userPassword")
                                .failureUrl("/login?fail")
                                .successHandler(new MyLoginSuccessHandler(userRepository))
                )
                .logout((logoutConfig) ->
                        logoutConfig
                                //.logoutUrl("/custom-logout")
                                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                                .logoutSuccessHandler(new MyLogoutSuccessHandler())

                );
        return httpSecurity.build();
    }
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }
     */
}