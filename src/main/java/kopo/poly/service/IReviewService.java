package kopo.poly.service;

import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.ReviewDTO;

import java.util.List;

public interface IReviewService {

    /**
     * 공지사항 전체 가져오기
     */
    List<ReviewDTO> searchBooks(String searchKeyword);

    List<ReviewDTO> getReviewList();

    /**
     * 공지사항 상세 정보가져오기
     *
     * @param pDTO 공지사항 상세 가져오기 위한 정보
     * @param type 조회수 증가여부(true : 증가, false : 증가안함
     */
    ReviewDTO getReviewInfo(ReviewDTO pDTO, boolean type) throws Exception;

    /**
     * 해당 공지사항 수정하기
     *
     * @param pDTO 공지사항 수정하기 위한 정보
     */
    void updateReviewInfo(ReviewDTO pDTO) throws Exception;

    /**
     * 해당 공지사항 삭제하기
     *
     * @param pDTO 공지사항 삭제하기 위한 정보
     */
    void deleteReviewInfo(ReviewDTO pDTO) throws Exception;

    /**
     * 해당 공지사항 저장하기
     *
     * @param pDTO 공지사항 저장하기 위한 정보
     */
    void insertReviewInfo(ReviewDTO pDTO) throws Exception;

}
