package kopo.poly.service;

import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IUserInfoService {

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

    void updateUserInfo(UserInfoDTO pDTO) throws Exception;
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;
}
