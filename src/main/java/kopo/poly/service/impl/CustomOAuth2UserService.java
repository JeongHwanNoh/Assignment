package kopo.poly.service.impl;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import kopo.poly.util.EncryptUtil;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("CustomOAuth2UserService")
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserInfoRepository userInfoRepository;

    /**
     * 소셜 로그인을 위한 로직
     */
    @Override
    @SneakyThrows
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.info("OAuth2UserRequest: {}", userRequest);
        log.info("Client Registration: {}", userRequest.getClientRegistration());
        log.info("Access Token: {}", userRequest.getAccessToken().getTokenValue());
        log.info("Redirect URI: {}", userRequest.getClientRegistration().getRedirectUri());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String accessToken = userRequest.getAccessToken().getTokenValue();

//        String providerId = oAuth2User.getName();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        UserInfoEntity pEntity = UserInfoEntity.builder()
                .userId(email) // userId를 email로 설정
                .userName(name)
                .email(EncryptUtil.encAES128CBC(email))
                .regId(email)
                .chgId(email)
                .addr1(("소셜 로그인 처리되었습니다."))
                .addr2(("소셜 로그인 처리되었습니다."))
                .genre(("없음"))
                .provider(provider)
                .accessToken(accessToken)
//                .providerId(providerId)
                .build();

        saveOrUpdateUser(pEntity);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "name");
    }

    /**
     * 소셜 로그인 사용자의 최신 프로필 정보 반영
     */
    private void saveOrUpdateUser(UserInfoEntity pEntity) {
        Optional<UserInfoEntity> rEntityOptional = userInfoRepository.findByEmail(pEntity.getEmail());
        if (rEntityOptional.isPresent()) {
            UserInfoEntity existingUser = rEntityOptional.get();

            // 기존 사용자 정보를 유지하면서 소셜 로그인 정보로 업데이트
            UserInfoEntity updatedUser = UserInfoEntity.builder()
                    .userId(existingUser.getUserId()) // 기존 userId 유지
                    .userName(pEntity.getUserName()) // 소셜 로그인에서 가져온 사용자명으로 업데이트
                    .email(existingUser.getEmail()) // 기존 이메일 유지
                    .provider(pEntity.getProvider()) // 소셜 로그인 제공자 정보 업데이트
//                    .providerId(pEntity.getProviderId()) // 소셜 로그인 제공자 ID 업데이트
                    .regId(existingUser.getRegId()) // 기존 등록자 ID 유지
                    .regDt(existingUser.getRegDt()) // 기존 등록일시 유지
                    .chgId(existingUser.getUserId()) // 현재 사용자로 변경 ID 설정
                    .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss")) // 현재 시각으로 변경일시 설정
                    .addr1((existingUser.getAddr1()))
                    .addr2((existingUser.getAddr2()))
                    .genre(existingUser.getGenre())
                    .accessToken(pEntity.getAccessToken())
                    .build();

            userInfoRepository.save(updatedUser);
        } else {
            userInfoRepository.save(pEntity);
        }
    }

//    public void revokeAccessToken(UserInfoEntity pEntity) {
//        Optional<UserInfoEntity> rEntityOptional = userInfoRepository.findByAccessToken(pEntity.getAccessToken());
//        if (rEntityOptional.isPresent()) {
//            UserInfoEntity existingUser = rEntityOptional.get();
//
//            UserInfoEntity updatedUser = UserInfoEntity.builder()
//                    .accessToken(EncryptUtil.encHashSHA256(existingUser.getAccessToken("")))
//                            .build();
//            userInfoRepository.save(existingUser);
//
//            log.info("Access Token for user {} has been revoked.", existingUser.getUserId());
//        } else {
//            log.warn("No user found with the given access token.");
//        }


    public void revokeAccessToken(String accessToken) {
        String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Access Token has been revoked successfully.");
        } else {
            log.warn("Failed to revoke Access Token. Response: {}", response.getBody());
        }
    }


}

