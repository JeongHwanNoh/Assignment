package kopo.poly.service.impl;

import kopo.poly.dto.RecommendDTO;
import kopo.poly.service.IRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public List<RecommendDTO> getRandomBook() {
        List<RecommendDTO> cachedBooks = (List<RecommendDTO>) redisTemplate.opsForValue().get("random_books");

        if (cachedBooks != null) {
            return cachedBooks;
        }

        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=책";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        ResponseEntity<String> responseEntity = new RestTemplate().exchange(apiUrl, HttpMethod.GET, null, String.class);

        List<RecommendDTO> books = parseJsonResponse(responseEntity.getBody());

        redisTemplate.opsForValue().set("random_books", books, redisExpireTime, TimeUnit.SECONDS);

        return books;
    }

    private List<RecommendDTO> parseJsonResponse(String responseBody) {
        return null;
    }
}
