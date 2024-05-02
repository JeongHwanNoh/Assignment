package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {
    private final IUserInfoService userInfoService;

    @GetMapping(value="userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm start");
        log.info(this.getClass().getName() + ".user/userRegForm end");

        return "user/userRegForm";
    }
    @GetMapping(value="userRegForm2")
    public String userRegForm2() {
        log.info(this.getClass().getName() + ".user/userRegForm start");
        log.info(this.getClass().getName() + ".user/userRegForm end");

        return "user/userRegForm2";
    }

    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".getuserIdExist Start!");
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        log.info("userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getUserIdExists Emd!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().email(email).build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo start!");

        String msg;

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String password = CmmUtil.nvl(request.getParameter("password"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
        String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("addr1 : " + addr1);
        log.info("addr2 : " + addr2);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .addr1(addr1)
                .addr2(addr2)
                .regId(userId)
                .chgId(userId)
                .build();

        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) " + res);

        if (res == 1) {
            msg = "회원가입되었습니다.";
        } else if (res == 2) {
            msg = "이미 가입된 아이디입니다.";

        } else {
            msg = "오류로 인해 회원가입이 실패하였습니다.";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info(this.getClass().getName() + ".insertUserInfo End");

        return dto;
    }
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login start");
        log.info(this.getClass().getName() + ".user/login End!");

        return "user/login";
    }

    @GetMapping(value = "loginSuccess")
    public String loginSuccess() {
        log.info(this.getClass().getName() + ".user/loginSuccess Start!");
        log.info(this.getClass().getName() + ".user/loginSuccess End!");

        return "user/loginSuccess";
    }
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {
        log.info(this.getClass().getName() + ".logout Start!");

        session.setAttribute("SS_USER_ID", "");
        session.removeAttribute("SS_USER_ID");

        MsgDTO dto = MsgDTO.builder()
                .result(1)
                .msg("로그아웃하였습니다.")
                .build();

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }

    @GetMapping(value = "findId")
    public String searchUserId() {
        log.info(this.getClass().getName() + ".user/searchUserId Start!");

        log.info(this.getClass().getName() + ".user/searchUserId End!");

        return "user/findId";
    }

    @GetMapping(value = "mypage")
    public String mypage(HttpSession session, ModelMap model) {
        log.info(this.getClass().getName() + ".user/mypage Start!");

        log.info("Fetching calendar data from the database...");

        // 세션에서 사용자 아이디 가져오기
        String userId = (String) session.getAttribute("SS_USER_ID");
        log.info("User ID: " + userId);

        // 공지사항 리스트 조회하기
        List<CalendarDTO> rList = Optional.ofNullable(userInfoService.getUserList(userId))
                .orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info("확인" + rList);

        log.info(this.getClass().getName() + ".user/mypage End!");

        return "user/mypage";
    }

    @GetMapping(value = "newPassword")
    public String newPassword() {
        log.info(this.getClass().getName() + ".user/searchUserId Start!");

        log.info(this.getClass().getName() + ".user/searchUserId End!");

        return "user/newPassword";
    }


    @ResponseBody
    @PostMapping(value = "searchUserIdProc")        // 아이디 찾기 함수
    public MsgDTO searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".user/searchUserIdProc Start!");

        String msg;

        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("userName : " + userName);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userName(userName)
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        String res = userInfoService.searchUserIdProc(pDTO);

        // 추가된 로그
        log.info("res: " + res);

        if (!Objects.equals(res, "")) {
            msg = userName + " 회원님의 아이디는 " + res + "입니다.";
        } else {
            msg = "회원정보가 일치하지 않습니다.";
        }

        MsgDTO dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".user/searchUserIdProc End!");

        return dto;
    }



    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".loginProc start");

        String msg;

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("user_id : "  + user_id);
        log.info("password : " + password);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(user_id)
                .password(EncryptUtil.encHashSHA256(password)).build();

        int res = userInfoService.getUserLogin(pDTO);

        log.info("res : " + res);

        if (res == 1) {
            msg = "로그인이 성공했습니다.";
            session.setAttribute("SS_USER_ID", user_id);
        } else {
            msg = "아이디와 비밀번호가 올바르지 않습니다.";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();
        log.info(this.getClass().getName() + ".loginProc End!");

        return dto;
    }






    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "passwordsearch")
    public String searchPassword(HttpSession session) {
        log.info(this.getClass().getName() + ".user/searchPassword Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + ".user/searchPassword End!");

        return "user/passwordsearch";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
//    @PostMapping(value = "searchPasswordProc")
//    public String searchPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
//        log.info(this.getClass().getName() + ".searchPasswordProc Start!");
//
//        String userId = CmmUtil.nvl(request.getParameter("userId"));
//        String email = CmmUtil.nvl(request.getParameter("email"));
//        String userName = CmmUtil.nvl(request.getParameter("userName"));
//
//        log.info("userId: " + userId);
//        log.info("email: " + email);
//        log.info("userName: " + userName);
//
//        UserInfoDTO pDTO = UserInfoDTO.builder()
//                .userId(userId)
//                .userName(userName)
//                .email(EncryptUtil.encAES128CBC(email))
//                .build();
//
//        String password = userInfoService.searchPasswordProc(pDTO);
//
//        log.info("Retrieved password: " + password);
//
//        if (password != null && !password.isEmpty()) {
//            session.setAttribute("NEW_PASSWORD_USER_ID", userId);
//            log.info("성공 : " + password);
//
//            return "user/newPassword"; // 사용자 정보를 보여주는 페이지로 이동
//        } else {
//            log.info("실패 : " + password);
//            // 해당하는 사용자를 찾을 수 없을 때의 처리
//            return "user/login"; // 사용자 찾기 페이지로 다시 이동
//        }
//    }

    @ResponseBody
    @PostMapping(value = "searchPasswordProc")
    public MsgDTO searchPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".searchPasswordProc Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));

        log.info("userId: " + userId);
        log.info("email: " + email);
        log.info("userName: " + userName);


        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        String password = userInfoService.searchPasswordProc(pDTO);

        log.info("Retrieved password: " + password);

        MsgDTO dto;
        if (password != null && !password.isEmpty()) {
            session.setAttribute("NEW_PASSWORD_USER_ID", userId); // 성공적으로 비밀번호를 찾은 경우 세션에 아이디 저장
            dto = MsgDTO.builder().msg("success").build(); // 성공적으로 비밀번호를 찾은 경우
        } else {
            dto = MsgDTO.builder().msg("fail").build(); // 비밀번호를 찾지 못한 경우
        }

        log.info(this.getClass().getName() + ".searchPasswordProc End!");
        return dto;
    }




    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
//    @PostMapping(value = "newPasswordProc")
//    public MsgDTO newPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
//        log.info(this.getClass().getName() + ".user/newPasswordProc Start!");
//
//        String msg = ""; // 웹에 보여줄 메시지
//        MsgDTO dto;
//
//        // 세션에서 userId 받아오기
//        String userId = (String) session.getAttribute("NEW_PASSWORD_USER_ID");
//
//        // 세션에서 userId가 null인지 확인
//        if (userId == null || userId.isEmpty()) {
//            msg = "세션이 만료되었습니다. 다시 비밀번호 찾기를 진행해주세요.";
//            model.addAttribute("msg", msg);
//            dto = MsgDTO.builder().msg("fail").build(); // 비밀번호를 찾지 못한 경우
//            return dto; // 비밀번호 찾기 페이지로 이동
//        }
//
//        // 새 비밀번호 받아오기
//        String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호
//
//        log.info("Received userId: " + userId);
//        log.info("Received password: " + password);
//
//        // 신규 비밀번호를 해시로 암호화
//        String hashedPassword = EncryptUtil.encHashSHA256(password);
//
//        UserInfoDTO pDTO = UserInfoDTO.builder()
//                .userId(userId)
//                .password(hashedPassword)
//                .build();
//
//        // 서비스로 DTO 전달
//        userInfoService.newPasswordProc(pDTO);
//
//        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
//        session.setAttribute("NEW_PASSWORD", "");
//        session.removeAttribute("NEW_PASSWORD");
//
//        msg = "비밀번호가 재설정되었습니다.";
//
//        model.addAttribute("msg", msg);
//        dto = MsgDTO.builder().msg("success").build(); // 성공적으로 비밀번호를 찾은 경우
//        log.info(this.getClass().getName() + ".user/newPasswordProc End!");
//
//        log.info("dto : " + dto);
//        return dto;
//    }

    @PostMapping(value = "/newPasswordProc", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public MsgDTO newPasswordProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/newPasswordProc Start!");

        String msg = "";
        MsgDTO dto;

        // 세션에서 userId 받아오기
        String userId = (String) session.getAttribute("NEW_PASSWORD_USER_ID");

        if (userId == null || userId.isEmpty()) {
            msg = "세션이 만료되었습니다. 다시 비밀번호 찾기를 진행해주세요.";
            dto = MsgDTO.builder().msg("fail").build(); // 비밀번호를 찾지 못한 경우
            return dto; // 클라이언트로 바로 JSON 반환
        }

        // 새 비밀번호 받아오기
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("Received userId: " + userId);
        log.info("Received password: " + password);

        // 신규 비밀번호를 해시로 암호화
        String hashedPassword = EncryptUtil.encHashSHA256(password);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .password(hashedPassword)
                .build();

        // 서비스로 DTO 전달
        userInfoService.newPasswordProc(pDTO);

        // 비밀번호 재생성 후 세션 삭제
        session.removeAttribute("NEW_PASSWORD_USER_ID");

        msg = "비밀번호가 재설정되었습니다.";

        dto = MsgDTO.builder().msg("success").build(); // 성공적으로 비밀번호를 재설정한 경우
        log.info(this.getClass().getName() + ".user/newPasswordProc End!");

        log.info("dto : " + dto);
        return dto;
    }




}
