package kopo.poly.service;

import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import kopo.poly.dto.UserInterestsDTO;

import java.util.List;

public interface IUserInfoService extends UserDetailsService{

    UserInfoDTO getUserInfo(String userId) throws Exception;

    List<UserInfoDTO> getUserList(String userId);
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 주소 중복 체크 및 인증 값
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    int getUserLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디, 비밀번호 찾기에 활용
    String searchUserIdProc(UserInfoDTO pDTO) throws Exception;

    String searchPasswordProc(UserInfoDTO pDTO) throws Exception;

    //     비밀번호 재설정
    void newPasswordProc(UserInfoDTO pDTO) throws Exception;

    void newPassword2Proc(UserInfoDTO pDTO) throws Exception;

    void saveKeywordsForUser(String userId, List<String> keywords) throws Exception;

    /**
     * 키워드 검색 로직
     *
     * @param userId 회원 아이디
     * @return 해당 회원의 키워드 리스트
     */

    List<UserInterestsDTO> getKeywordList(String userId) throws Exception;
    /**
     * 키워드 수정 로직
     *
     * @param userId 회원 아이디
     */
    void updateKeywords(String userId, List<String> keywords) throws Exception;

    void updateUserInfo(UserInfoDTO pDTO) throws Exception;
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
