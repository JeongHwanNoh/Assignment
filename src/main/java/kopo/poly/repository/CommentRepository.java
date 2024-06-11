package kopo.poly.repository;

import kopo.poly.repository.entity.CommentEntity;
import kopo.poly.repository.entity.CommentPK;
import kopo.poly.repository.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, CommentPK> {

    List<CommentEntity> findAllByOrderByCommentSeqDesc();

    List<CommentEntity> findByNoticeSeqOrderByCommentSeqAsc(Long noticeSeq);

    @Transactional(readOnly = true)
    @Query(value = "SELECT COALESCE(MAX(COMMENT_SEQ), 0)+1 FROM COMMENT WHERE NOTICE_SEQ = ?1",
            nativeQuery = true)
    Long getMaxCommentsSeq(Long noticeSeq);


}
