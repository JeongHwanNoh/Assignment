package kopo.poly.config;

import jakarta.servlet.http.HttpSession;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.impl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Optional;

import static javax.swing.text.html.CSS.getAttribute;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserInfoRepository userInfoRepository;


    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info(this.getClass().getName() + ".PasswordEncoder Start!");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info(this.getClass().getName() + ".filterChain Start!");

        http.csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (필요에 따라 설정)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/notice/**").authenticated()
                        .requestMatchers("/user/mypage").authenticated()
                        .requestMatchers("/review/**").authenticated()
                        .requestMatchers("/calendar/**").authenticated()
                        .requestMatchers("/search/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login") // 사용자 정의 로그인 페이지 경로 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))// 사용자 정의 OAuth2UserService 지정
                        .successHandler(oAuth2AuthenticationSuccessHandler()) // 인증 성공 후 핸들러 지정
                )
                .formLogin(login -> login // 로그인 페이지 설정
                        .loginPage("/user/login") // 폼 기반 로그인 활성화 및 로그인 페이지 지정
                        .loginProcessingUrl("/login/v1/loginProc")
                        .usernameParameter("userId") // 로그인 ID로 사용할 html의 input객체의 name 값
                        .passwordParameter("password") // 로그인 패스워드로 사용할 html의 input객체의 name 값
                        .successHandler(ss())
                        .successForwardUrl("/login/v1/loginSuccess") // Web MVC, Controller 사용할 때 적용 / 로그인 성공 URL
                        .failureForwardUrl("/login/v1/loginFail") // Web MVC, Controller 사용할 때 적용 / 로그인 실패 URL
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")  // 로그아웃 시 세션 쿠키 삭제
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                );


        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler ss() {
        return (request, response, authentication) -> {

            HttpSession session = request.getSession();

            String userId = (String) session.getAttribute("SS_USER_ID");

            session.setAttribute("SS_USER_ID", userId);

        };
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String userId = oauthUser.getAttribute("email");

            session.setAttribute("SS_USER_ID", userId);

            response.sendRedirect("/main/index");
        };
    }


    //        @Bean
//    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
//        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
//                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
//
//
//        // 로그아웃 성공 후 리디렉트할 URL 설정
//        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("https://www.mylibra.store/user/login");
//
//        return oidcLogoutSuccessHandler;
//    }
    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            // 세션 무효화 및 로그아웃 처리
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String userId = oauthUser.getAttribute("email");

            // 데이터베이스에서 사용자 정보 조회
            Optional<UserInfoEntity> userEntityOptional = userInfoRepository.findByUserId(userId);

            if (userEntityOptional.isPresent()) {
                UserInfoEntity userEntity = userEntityOptional.get();
                String accessToken = userEntity.getAccessToken();

                // 액세스 토큰을 취소
                customOAuth2UserService.revokeAccessToken(accessToken);
                log.info("Access Token for user {} has been revoked.", userEntity.getUserId());
            } else {
                log.warn("No user found with email: {}", userId);
            }

            // 로그아웃 후 리디렉션
            response.sendRedirect("/main/index");
        };
    }



}
