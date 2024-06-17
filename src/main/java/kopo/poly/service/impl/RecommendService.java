package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.service.IRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendService implements IRecommendService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${redis.expire.time}")
    private long redisExpireTime;

    // Naver 책 검색 API 요청
    public List<RecommendDTO> getRandomBook() {
        log.info("getRandomBooks() 호출");

        List<RecommendDTO> cachedBooks = (List<RecommendDTO>) redisTemplate.opsForValue().get("random_books");

        if (cachedBooks != null) {
            log.info("캐시에서 책 목록을 가져옴: {}", cachedBooks);
            return cachedBooks;
        }

        String randomKeyword = getRandomKeyword();
        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + randomKeyword + "&display=5";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        log.info("API 요청 URL: {}", apiUrl);
        log.info("API 요청 헤더: {}", headers);

        ResponseEntity<String> responseEntity = new RestTemplate().exchange(apiUrl, HttpMethod.GET, entity, String.class);

        log.info("API 응답 코드: {}", responseEntity.getStatusCode());
        log.info("API 응답 본문: {}", responseEntity.getBody());

        List<RecommendDTO> books = parseJsonResponse(responseEntity.getBody());

        if (books != null) {
            log.info("책 목록 파싱 성공: {}", books);
            redisTemplate.opsForValue().set("random_books", books, redisExpireTime, TimeUnit.SECONDS);
        } else {
            log.warn("책 목록 파싱 실패");
        }

        return books;
    }

    // JSON 응답 파싱
    private List<RecommendDTO> parseJsonResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode root = mapper.readTree(responseBody);
            JsonNode items = root.path("items");

            List<RecommendDTO> books = mapper.readValue(items.toString(), new TypeReference<List<RecommendDTO>>() {});

            log.info("응답 본문 파싱 결과: {}", books);

            return books;
        } catch (IOException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
    private String getRandomKeyword() {
        String[] keywords = {"경제", "역사", "자기계발", "과학"};
        String[] bookRelatedKeywords = {"경제", "역사", "자기계발", "과학"};

        int randomIndex = ThreadLocalRandom.current().nextInt(keywords.length);
        String keyword = keywords[randomIndex];

        // 책과 관련된 키워드만 선택하도록 조건 추가
        while (!Arrays.asList(bookRelatedKeywords).contains(keyword)) {
            randomIndex = ThreadLocalRandom.current().nextInt(keywords.length);
            keyword = keywords[randomIndex];
        }

        return keyword;
    }

}
