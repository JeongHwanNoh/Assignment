package kopo.poly.repository;

import kopo.poly.repository.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    Optional<LikeEntity> findByNoticeSeqAndUserId(Long noticeSeq, String userId);

    Long countByNoticeSeq(Long noticeSeq);

    void deleteByNoticeSeqAndUserId(Long noticeSeq, String userId);
}
