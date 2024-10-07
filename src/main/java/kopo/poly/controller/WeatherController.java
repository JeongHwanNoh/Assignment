package kopo.poly.controller;

import kopo.poly.dto.WeatherDTO;
import kopo.poly.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final IWeatherService weatherService;

    @Value("${weather.api.key}")
    private String weatherKey; // API 키를 주입합니다.

    @GetMapping("test")
    public ModelAndView getWeatherPage() throws Exception {

        // API를 호출하여 날씨 정보를 가져옵니다.
        WeatherDTO weather = weatherService.getWeather("Seoul", weatherKey);

        // ModelAndView 객체를 생성하고, 뷰 이름과 모델 데이터를 설정합니다.
        ModelAndView mav = new ModelAndView();
        mav.setViewName("weather/test"); // weather/test.html 파일을 렌더링
        mav.addObject("weather", weather); // 모델 데이터 추가

        return mav;
    }
}
