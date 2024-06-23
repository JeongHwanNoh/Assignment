//package kopo.poly.controller;
//
//import jakarta.servlet.http.HttpSession;
//import kopo.poly.dto.RecommendDTO;
//import kopo.poly.dto.WeatherDTO;
//import kopo.poly.service.IRecommendService;
//import kopo.poly.service.IWeatherService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Slf4j
//@RequestMapping(value = "/main")
//@RequiredArgsConstructor
//@Controller
//public class IndexController {
//
//    private final IRecommendService recommendService;
//
//    private final IWeatherService weatherService;
//
//    @Value("${weather.api.key}")
//    private String weatherKey; // API 키를 주입합니다.
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate; // RedisTemplate 설정 필요
//
//    @GetMapping(value = "index")
//    public String mainIndex(HttpSession session, ModelMap model) {
//
//        WeatherDTO weather = weatherService.getWeather("Seoul", weatherKey);
//
//
//        List<RecommendDTO> randomBooks = recommendService.getRandomBook();
//
//        // 모델에 책 정보 추가
//        List<RecommendDTO> recommendedBooks = (List<RecommendDTO>) redisTemplate.opsForValue().get("random_books");
//
//        model.addAttribute("randomBooks", recommendedBooks);
//
//        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
//        log.info(this.getClass().getName() + ".index Start!");
//
//        // 세션에서 사용자 ID 가져오기
//        String userId = (String) session.getAttribute("SS_USER_ID");
//        model.addAttribute("SS_USER_ID", userId);
//        log.info("SS_USER_ID : " + userId);
//
//        log.info(this.getClass().getName() + ".index End!");
//
//        return "main/index";
//    }
//}
package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.dto.WeatherDTO;
import kopo.poly.service.IRecommendService;
import kopo.poly.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final IRecommendService recommendService;

    private final IWeatherService weatherService;

    @Value("${weather.api.key}")
    private String weatherKey; // API 키를 주입합니다.

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // RedisTemplate 설정 필요

    @GetMapping(value = "index")
    public String mainIndex(HttpSession session, ModelMap model) {
        WeatherDTO weather = null; // 날씨 정보를 초기화합니다.

        try {
            // 날씨 정보 가져오기
            weather = weatherService.getWeather("Seoul", weatherKey);
            model.addAttribute("weather", weather); // 모델에 날씨 정보 추가
        } catch (Exception e) {
            log.error("Failed to get weather data: ", e); // 예외 로그 출력
            // 예외 발생 시 기본값 설정하거나 사용자에게 알림
            model.addAttribute("weatherError", "날씨 정보를 가져오는 데 실패했습니다.");
        }

        List<RecommendDTO> randomBooks = recommendService.getRandomBook();

        List<RecommendDTO> recommendedBooks = (List<RecommendDTO>) redisTemplate.opsForValue().get("random_books");
        model.addAttribute("randomBooks", recommendedBooks);

        log.info(this.getClass().getName() + ".index Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        model.addAttribute("SS_USER_ID", userId);
        log.info("SS_USER_ID : " + userId);

        log.info(this.getClass().getName() + ".index End!");

        return "main/index";
    }
}