package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        return "userRegForm2";
    }

    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserExist(HttpServletRequest request) throws Exception {
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

        MsgDTO dto = MsgDTO.builder().result(1).msg("로그아웃하였습니다.").build();

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }

    @GetMapping(value = "findId")
    public String searchUserId() {
        log.info(this.getClass().getName() + ".user/searchUserId Start!");

        log.info(this.getClass().getName() + ".user/searchUserId End!");

        return "user/findId";
    }

    @PostMapping(value = "searchUserIdProc")
    public String searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".user/searchUserIdProc Start!");

        log.info(this.getClass().getName() + ".getuserIdExist Start!");


        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String password = CmmUtil.nvl(request.getParameter("password"));
        String email = CmmUtil.nvl(request.getParameter("email"));

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());


        log.info(this.getClass().getName() + ".user/searchUserIdProc End!");

        return "user/searchUserIdResult";

    }


    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "searchPassword")
    public String searchPassword(HttpSession session) {
        log.info(this.getClass().getName() + ".user/searchPassword Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + ".user/searchPassword End!");

        return "user/searchPassword";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "searchPasswordProc")
    public String searchPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/searchPasswordProc Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        model.addAttribute("rDTO", rDTO);

        session.setAttribute("NEW_PASSWORD", userId);

        log.info(this.getClass().getName() + ".user/searchPasswordProc End!");

        return "user/newPassword";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "newPasswordProc")
    public String newPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".user/newPasswordProc Start!");

        String msg = ""; // 웹에 보여줄 메시지

        String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));
        String userId = CmmUtil.nvl(request.getParameter("userId"));

        if (newPassword.length() > 0) { //정상 접근

            String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호

            log.info("password : " + password);

            UserInfoDTO pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .password(EncryptUtil.encHashSHA256(password))
                    .build();

            UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                    .orElseGet(() -> UserInfoDTO.builder().build());

            userInfoService.newPasswordProc(pDTO);

            // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
            session.setAttribute("NEW_PASSWORD", "");
            session.removeAttribute("NEW_PASSWORD");

            msg = "비밀번호가 재설정되었습니다.";

        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
        }

        model.addAttribute("msg", msg);


        log.info(this.getClass().getName() + ".user/newPasswordProc End!");

        return "user/newPasswordResult";

    }
}