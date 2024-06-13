package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.RecommendDTO;
//import kopo.poly.service.IRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequestMapping(value = "/main")
@RequiredArgsConstructor
@Controller
public class IndexController {

//    private final IRecommendService recommendService;

    @GetMapping(value = "index")
    public String mainIndex(HttpSession session, ModelMap model) {
//        List<RecommendDTO> randomBooks = recommendService.getRandomBook();
//
//        // 모델에 책 정보 추가
//        model.addAttribute("randomBooks", randomBooks);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".index Start!");

        // 세션에서 사용자 ID 가져오기
        String userId = (String) session.getAttribute("SS_USER_ID");
        model.addAttribute("SS_USER_ID", userId);
        log.info("SS_USER_ID : " + userId);

        log.info(this.getClass().getName() + ".index End!");

        return "main/index";
    }
}
