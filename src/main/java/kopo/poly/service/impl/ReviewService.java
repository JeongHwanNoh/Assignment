package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.BookDTO;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.ReviewDTO;
import kopo.poly.repository.BookRepository;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.repository.ReviewRepository;
import kopo.poly.repository.entity.NoticeEntity;
import kopo.poly.repository.entity.ReviewEntity;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.INoticeService;
import kopo.poly.service.IReviewService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    //api 키

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Override
    public List<ReviewDTO> searchBooks(String searchKeyword) {
        log.info(this.getClass().getName() + ".searchBook Start!");

        //키워드 검색

        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + searchKeyword;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);


        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        log.info("response : " + response);

        //데이터 검색 후 아이템 변환

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        log.info("items : " + items);

        log.info(this.getClass().getName() + ".searchBook End");


        //검색에 맞는 데이터 추가

        List<ReviewDTO> bookList = items.stream().map(item -> {
            ReviewDTO book = ReviewDTO.builder()
                    .title((String) item.get("title"))
                    .author((String) item.get("author"))
                    .imageUrl((String) item.get("image"))
                    .build();
            log.info("ReviewDTO : " + book);
            return book;
        }).collect(Collectors.toList());

        log.info(this.getClass().getName() + ".searchBooks End");

        return bookList;
    }

    @Override
    public List<ReviewDTO> getReviewList() {


        //독후감 작성 리스트

        log.info(this.getClass().getName() + ".getReviewList Start!");

        List<ReviewEntity> rList = reviewRepository.findAllByOrderByReviewSeqDesc();

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        List<ReviewDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<>() {
                });

        log.info(this.getClass().getName() + ".getReviewList End!");

        return nList;
    }

    @Transactional
    @Override
    public ReviewDTO getReviewInfo(ReviewDTO pDTO, boolean type) {

        log.info(this.getClass().getName() + ".getReviewInfo Start!");

        ReviewEntity rEntity = reviewRepository.findByReviewSeq(pDTO.reviewSeq());


        // 엔티티의 값들을 DTO에 맞게 넣어주기
        ReviewDTO rDTO = new ObjectMapper().convertValue(rEntity, ReviewDTO.class);

        log.info("확인 " + rEntity);

        log.info(this.getClass().getName() + ".getReviewInfo End!");

        return rDTO;
    }

    @Transactional
    @Override
    public void updateReviewInfo(ReviewDTO pDTO) {


        //수정

        log.info(this.getClass().getName() + ".updateReviewInfo Start!");

        Long reviewSeq = pDTO.reviewSeq();

        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());
        String userId = CmmUtil.nvl(pDTO.userId());
        String rating = CmmUtil.nvl(String.valueOf(pDTO.rating()));
        String author = CmmUtil.nvl(pDTO.author());
        String imageUrl = CmmUtil.nvl(pDTO.imageUrl());
        String regDt = CmmUtil.nvl(pDTO.regDt());

        // regDt 값이 비어 있으면 현재 날짜로 설정
        if (regDt.isEmpty()) {
            regDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }

        log.info("reviewSeq : " + reviewSeq);
        log.info("title : " + title);
        log.info("contents : " + contents);
        log.info("userId : " + userId);
        log.info("rating : " + rating);
        log.info("author : " + author);
        log.info("imageUrl : " + imageUrl);
        log.info("regDt : " + regDt);

        // 수정할 값들을 빌더를 통해 엔티티에 저장하기
        ReviewEntity pEntity = ReviewEntity.builder()
                .reviewSeq(reviewSeq).title(title).contents(contents).userId(userId).rating(Long.valueOf(rating))
                .author(author).imageUrl(imageUrl).regDt(regDt)
                .build();

        // 데이터 수정하기
        reviewRepository.save(pEntity);

        log.info(this.getClass().getName() + ".updateReviewInfo End!");
    }


    @Override
    public void deleteReviewInfo(ReviewDTO pDTO) throws Exception {

        //삭제

        log.info(this.getClass().getName() + ".deleteReviewInfo Start!");

        Long reviewSeq = pDTO.reviewSeq();

        log.info("reviewSeq : " + reviewSeq);

        // 데이터 수정하기
        reviewRepository.deleteById(reviewSeq);

        log.info(this.getClass().getName() + ".deleteReviewInfo End!");
    }

    @Override
    public void insertReviewInfo(ReviewDTO pDTO) throws Exception {

        //독후감 추가

        log.info(this.getClass().getName() + ".InsertReviewInfo Start!");

        Long rating = Long.valueOf(CmmUtil.nvl(String.valueOf(pDTO.rating())));
        String author = CmmUtil.nvl(pDTO.author());
        String imageUrl = CmmUtil.nvl(pDTO.imageUrl());
        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());
        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("title : " + title);
        log.info("contents : " + contents);
        log.info("userId : " + userId);
        log.info("imageUrl : " + imageUrl);
        log.info("author : " + author);
        log.info("rating : " + rating);

        // imageUrl이 "null" 문자열이 아닌 경우에만 변환
        if (!"null".equals(imageUrl)) {
            // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
            // JPA에 자동 증가 설정을 해놨음
            ReviewEntity pEntity = ReviewEntity.builder()
                    .title(title).contents(contents).userId(userId).imageUrl(imageUrl).author(author)
                    .rating(rating)
                    .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            reviewRepository.save(pEntity);
        } else {
            log.error("imageUrl is null. Cannot insert review info.");
        }

        log.info(this.getClass().getName() + ".InsertReviewInfo End!");

    }

}
