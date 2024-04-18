package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IMailService;
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
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@Controller
public class MailController {

    private final IMailService mailService; // 메일 발송을 위한 서비스 객체를 사용하기

    /**
     * 메일 발송하기폼
     */
    @GetMapping(value = "mailForm")
    public String mailForm() throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + "mailForm Start!");

        return "mail/mailForm";
    }

    /**
     * 메일 발송하기
     */
    @ResponseBody
    @PostMapping(value = "sendMail")
    public MsgDTO sendMail(HttpServletRequest request, ModelMap model) throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".sendMail Start!");

        String msg = ""; // 발송 결과 메시지

        // 웹 URL로부터 전달받는 값들
        String toMail = CmmUtil.nvl(request.getParameter("toMail")); // 받는사람
        String title = CmmUtil.nvl(request.getParameter("title")); // 제목
        String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

        log.info("toMail : " + toMail);
        log.info("title : " + title);
        log.info("contents : " + contents);

        MailDTO pDTO = MailDTO.builder()
                .toMail(toMail)
                .title(EncryptUtil.encHashSHA256(title))
                .contents(EncryptUtil.encAES128CBC(contents))
                .build();

//        MailDTO rDTO = Optional.ofNullable(mailService.doSendMail(pDTO))
//                .orElseGet(() -> MailDTO.builder().build());

        MailDTO rDTO = Optional.ofNullable(mailService.doSendMail(pDTO))
                .map(result -> MailDTO.builder().build())
                .orElseGet(() -> MailDTO.builder().build());


        int res = mailService.doSendMail(pDTO);

            if (res == 1) { //메일발송 성공
            msg = "메일 발송하였습니다.";

        } else { //메일발송 실패
            msg = "메일 발송 실패하였습니다.";
        }

        log.info(msg);

        MsgDTO dto = MsgDTO.builder().msg(msg).build();
        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".sendMail End!");

        return dto;
    }
}
