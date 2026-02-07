package Nhom5.ThachTrongKhang.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import Nhom5.ThachTrongKhang.services.OAuthService;
import Nhom5.ThachTrongKhang.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
        private final UserService userService;
        private final OAuthService oAuthService;

        @Bean
        public UserDetailsService userDetailsService() {
                return userService;
        }

        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
        }

        @Bean
        public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
                return http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/css/**", "/js/**", "/",
                                                                "/oauth/**", "/register", "/error")
                                                .permitAll()
                                                .requestMatchers("/books/add", "/books/edit/**", "/books/delete/**")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers("/cart/**", "/cart", "/books/add-to-cart")
                                                .hasAuthority("USER")
                                                .requestMatchers("/api/v1/books/**").authenticated()
                                                .requestMatchers("/api/v1/categories").authenticated()
                                                .requestMatchers("/books/**", "/books")
                                                .authenticated()
                                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**"))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .deleteCookies("JSESSIONID")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .permitAll())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/")
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .oauth2Login(oauth2Login -> oauth2Login
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/")
                                                .failureUrl("/login?error")
                                                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                                                .userService(oAuthService))
                                                .successHandler((request, response, authentication) -> {
                                                        var oauthUser = authentication.getPrincipal();
                                                        String email = null;
                                                        String name = null;
                                                        
                                                        if (oauthUser instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User) {
                                                                var oauth2User = (org.springframework.security.oauth2.core.user.DefaultOAuth2User) oauthUser;
                                                                email = oauth2User.getAttribute("email");
                                                                name = oauth2User.getAttribute("name");
                                                        }
                                                        
                                                        if (email != null && name != null) {
                                                                userService.saveOauthUser(email, name);
                                                        }
                                                        response.sendRedirect("/");
                                                })
                                                .permitAll())
                                .rememberMe(rememberMe -> rememberMe
                                                .key("hutech")
                                                .rememberMeCookieName("hutech")
                                                .tokenValiditySeconds(24 * 60 * 60)
                                                .userDetailsService(userDetailsService()))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .accessDeniedPage("/403"))
                                .sessionManagement(sessionManagement -> sessionManagement
                                                .maximumSessions(1)
                                                .expiredUrl("/login"))
                                .httpBasic(httpBasic -> httpBasic
                                                .realmName("hutech"))
                                .build();
        }
}
