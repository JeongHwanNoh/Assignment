package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/library")
@RequiredArgsConstructor
@Controller
public class LibraryController {

    @Value("${kakao.api.key}")
    private String kakaoMapApiKey;

    @GetMapping(value = "search")
    public String LibraryController(HttpSession session, ModelMap model)
            throws Exception {

        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        model.addAttribute("SS_USER_ID", SS_USER_ID);
        model.addAttribute("kakaoMapApiKey", kakaoMapApiKey); // API 키 추가

        return "library/search";
    }
}