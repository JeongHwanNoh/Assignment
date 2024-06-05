package kopo.poly.repository;

import kopo.poly.repository.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    /**
     * 공지사항 리스트
     */
    List<ReviewEntity> findAllByOrderByReviewSeqDesc();

    ReviewEntity findByReviewSeq(Long reviewSeq);
}