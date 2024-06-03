package kopo.poly.service.impl;

import kopo.poly.dto.BookDTO;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.ReviewDTO;
import kopo.poly.repository.BookRepository;
import kopo.poly.repository.entity.BookEntity;
import kopo.poly.repository.entity.CalendarEntity;
import kopo.poly.service.IBookService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        log.info(this.getClass().getName() + ".searchBooks Start");

        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + searchKeyword;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        List<BookDTO> bookList = items.stream().map(item -> {
            BookDTO book = BookDTO.builder()
                    .title((String) item.get("title"))
                    .author((String) item.get("author"))
                    .description((String) item.get("description"))
                    .imageUrl((String) item.get("image"))
                    .isbn((String) item.get("isbn"))
                    .build();
            log.info("BookDTO: " + book);
            return book;
        }).collect(Collectors.toList());

        log.info(this.getClass().getName() + ".searchBooks End");
        return bookList;
    }

    @Override
    public BookDTO getBookDetail(String isbn) {
        log.info(this.getClass().getName() + ".getBookDetail Start");

        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + isbn;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        if (response.getBody() != null) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

            if (items != null && !items.isEmpty()) {
                Map<String, Object> firstItem = items.get(0);
                return BookDTO.builder()
                        .title((String) firstItem.get("title"))
                        .author((String) firstItem.get("author"))
                        .description((String) firstItem.get("description"))
                        .imageUrl((String) firstItem.get("image"))
                        .isbn((String) firstItem.get("isbn"))
                        .build();
            }
        }

        log.info(this.getClass().getName() + ".getBookDetail End");
        return null;
    }



    @Override
    public void insertBookInfo(BookDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".InsertBookInfo Start!");

        String description = CmmUtil.nvl(pDTO.description());
        String imageUrl = CmmUtil.nvl(pDTO.imageUrl());
        String title = CmmUtil.nvl(pDTO.title());
        String author = CmmUtil.nvl(pDTO.author());
        String userId = CmmUtil.nvl(pDTO.userId());
        String isbn = CmmUtil.nvl(pDTO.isbn());

        log.info("title : " + title);
        log.info("description : " + description);
        log.info("userId : " + userId);
        log.info("imageUrl : " + imageUrl);
        log.info("author : " + author);
        log.info("isbn" + isbn);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        BookEntity pEntity = BookEntity.builder()
                .title(title).description(description).userId(userId)
                .imageUrl(imageUrl).author(author).isbn(isbn)
                .build();

        // 공지사항 저장하기
        bookRepository.save(pEntity);

        log.info(this.getClass().getName() + ".InsertReviewInfo End!");
    }


    @Override
    public void deleteBookInfo(BookDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteBookInfo Start!");

        Long bookSeq = pDTO.bookSeq();

        log.info("bookSeq : " + bookSeq);

        bookRepository.deleteById(String.valueOf(bookSeq));


        log.info(this.getClass().getName() + ".deleteBookInfo End!");

    }

    @Override
    public List<BookDTO> getBookList(String userId) {
        log.info("Fetching calendar data for user: {}", userId);

        List<BookEntity> rList = bookRepository.findAllByUserIdOrderByBookSeqDesc(userId);

        List<BookDTO> nList = rList.stream()
                .map(bookEntity -> BookDTO.builder()
                        .isbn(bookEntity.getIsbn())
                        .bookSeq(bookEntity.getBookSeq())
                        .title(bookEntity.getTitle())
                        .userId(bookEntity.getUserId())
                        .author(bookEntity.getAuthor())
                        .imageUrl(bookEntity.getImageUrl())
                        .description(bookEntity.getDescription())
                        .build())
                .collect(Collectors.toList());

        log.info("Calendar data fetched successfully for user: {}", userId);

        return nList;
    }

}