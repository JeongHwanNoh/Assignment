package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.ReviewDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService implements IReviewService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // noticeRepository 변수에 이미 메모리에 올라간 NoticeRepository 객체를 넣어줌
    // 예전에는 autowired 어노테이션를 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewDTO> getReviewList() {

        log.info(this.getClass().getName() + ".getReviewList Start!");

        // 공지사항 전체 리스트 조회하기
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

        // 공지사항 상세내역 가져오기
        ReviewEntity rEntity = reviewRepository.findByReviewSeq(pDTO.reviewSeq());

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        ReviewDTO rDTO = new ObjectMapper().convertValue(rEntity, ReviewDTO.class);

        log.info(this.getClass().getName() + ".getReviewInfo End!");

        return rDTO;
    }

    @Transactional
    @Override
    public void updateReviewInfo(ReviewDTO pDTO) {

        log.info(this.getClass().getName() + ".updateReviewInfo Start!");

        Long reviewSeq = pDTO.reviewSeq();

        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());
        String userId = CmmUtil.nvl(pDTO.userId());
        String rating = CmmUtil.nvl(pDTO.rating());
        String author = CmmUtil.nvl(pDTO.author());
        String imageUrl = CmmUtil.nvl(pDTO.image_url());
        String regDt = CmmUtil.nvl(pDTO.regDt());

        log.info("reviewSeq : " + reviewSeq);
        log.info("title : " + title);
        log.info("contents : " + contents);
        log.info("userId : " + userId);
        log.info("rating : " + rating);
        log.info("author : " + author);
        log.info("imageUrl : " + imageUrl);
        log.info("regDt : " + regDt);

        // 현재 공지사항 조회수 가져오기
        ReviewEntity rEntity = reviewRepository.findByReviewSeq(reviewSeq);

        // 수정할 값들을 빌더를 통해 엔티티에 저장하기
        ReviewEntity pEntity = ReviewEntity.builder()
                .reviewSeq(reviewSeq).title(title).contents(contents).userId(userId).rating(rating)
                .author(author).imageUrl(imageUrl).regDt(regDt)
                .build();

        // 데이터 수정하기
        reviewRepository.save(pEntity);

        log.info(this.getClass().getName() + ".updateReviewInfo End!");

    }

    @Override
    public void deleteReviewInfo(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteReviewInfo Start!");

        Long reviewSeq = pDTO.reviewSeq();

        log.info("reviewSeq : " + reviewSeq);

        // 데이터 수정하기
        reviewRepository.deleteById(reviewSeq);


        log.info(this.getClass().getName() + ".deleteReviewInfo End!");
    }

    @Override
    public void insertReviewInfo(ReviewDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".InsertReviewInfo Start!");

        String rating = CmmUtil.nvl(pDTO.rating());
        String author = CmmUtil.nvl(pDTO.author());
        String imageUrl = CmmUtil.nvl(pDTO.image_url());
        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());
        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("title : " + title);
        log.info("contents : " + contents);
        log.info("userId : " + userId);
        log.info("imageUrl : " + imageUrl);
        log.info("author : " + author);
        log.info("rating : " + rating);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        ReviewEntity pEntity = ReviewEntity.builder()
                .title(title).contents(contents).userId(userId).imageUrl(imageUrl).author(author)
                .rating(rating)
                .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        reviewRepository.save(pEntity);

        log.info(this.getClass().getName() + ".InsertReviewInfo End!");

    }
}
