package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping(value = "/library")
@RequiredArgsConstructor
@Controller
public class LibraryController {

    @GetMapping(value = "search")
    public String LibraryController(HttpSession session, ModelMap model)
            throws Exception {
        
        log.info(this.getClass().getName() + ".noticeList Start!");
        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList End!");

        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        model.addAttribute("SS_USER_ID", SS_USER_ID);
        // templates/notice/noticeList.html
        return "library/search";
    }
}