package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequestMapping(value = "/reg/v1")
@RequiredArgsConstructor
@RestController
public class UserRegController {

    private final IUserInfoService userInfoService;

    // Spring Security에서 제공하는 비밀번호 암호화 객체(해시 함수)
    private final PasswordEncoder bCryptPasswordEncoder;

    @PostMapping(value = "getUserIdExists")
    public ResponseEntity<CommonResponse> getUserIdExists(@RequestBody UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO = userInfoService.getUserIdExists(pDTO);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }

    @PostMapping(value = "insertUserInfo")
    public ResponseEntity<CommonResponse> insertUserInfo(@Valid @RequestBody UserInfoDTO pDTO,
                                                         BindingResult bindingResult) {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩되었는지 체크
            return CommonResponse.getErrors(bindingResult); // 유효성 검증 결과에 따른 에러 메시지 전달
        }

        int res = 0; // 회원가입 결과
        String msg = ""; //회원가입 결과에 대한 메시지를 전달할 변수
        MsgDTO dto; // 결과 메시지 구조

        log.info("pDTO : " + pDTO);

        try {

            // 웹으로 입력받은 정보와 비밀번호 생성하기
            UserInfoDTO nDTO = UserInfoDTO.createUser(
                    pDTO, bCryptPasswordEncoder.encode(pDTO.password()));

            res = userInfoService.insertUserInfo(nDTO);

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msg = "회원가입되었습니다.";
            } else if (res == 2) {
                msg = "이미 가입된 아이디입니다.";
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());

        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();

            log.info(this.getClass().getName() + ".insertUserInfo End!");
        }

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }
}