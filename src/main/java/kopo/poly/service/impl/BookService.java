package kopo.poly.service.impl;

import kopo.poly.dto.BookDTO;
import kopo.poly.repository.BookRepository;
import kopo.poly.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService implements IBookService {

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Override
    public List<BookDTO> searchBooks(String searchKeyword) {


        log.info(this.getClass().getName() + ".searchBook Start!");

        // 네이버 책 검색 API 호출 URL 생성
        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + searchKeyword;

        // 네이버 책 검색 API 호출 시 필요한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 네이버 책 검색 API 호출 및 응답 받기
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        log.info("response : " + response);


        // 응답에서 아이템 리스트 추출
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        log.info("items : " + items);

        log.info(this.getClass().getName() + ".searchBook End");

        // 아이템 리스트를 BookDTO 리스트로 변환하여 반환
        return items.stream().map(item -> BookDTO.builder()
                        .title((String) item.get("title"))
                        .author((String) item.get("author"))
                        .description((String) item.get("description"))
                        .imageUrl((String) item.get("image"))
                        .build())
                .collect(Collectors.toList());

    }
}