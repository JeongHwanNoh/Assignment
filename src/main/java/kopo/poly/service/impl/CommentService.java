package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.CommentRepository;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.repository.entity.CommentEntity;
import kopo.poly.repository.entity.NoticeEntity;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDTO> getCommentList() {

        log.info(this.getClass().getName() + ".getCommentList Start!");

        List<CommentEntity> rList = commentRepository.findAllByOrderByCommentSeqDesc();

        List<CommentDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<>() {
                });

        log.info(this.getClass().getName() + ".getCommentList End!");

        return nList;
    }

    @Transactional
    @Override
    public void updateCommentInfo(CommentDTO pDTO) {

        log.info(this.getClass().getName() + ".updateCommentInfo Start!");

        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = pDTO.commentSeq();

        String comment = CmmUtil.nvl(pDTO.comment());
        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("noticeSeq : " + noticeSeq);
        log.info("commentSeq : " + commentSeq);
        log.info("comment : " + comment);
        log.info("userId : " + userId);

        CommentEntity pEntity = CommentEntity.builder()
                .noticeSeq(noticeSeq).commentSeq(commentSeq).userId(userId)
                .build();

        // 데이터 수정하기
        commentRepository.save(pEntity);

        log.info(this.getClass().getName() + ".updateCommentInfo End!");

    }

    @Override
    public void deleteCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCommentInfo Start!");

        Long commentSeq = pDTO.commentSeq();

        log.info("commentSeq : " + commentSeq);

        commentRepository.deleteById(commentSeq);


        log.info(this.getClass().getName() + ".deleteCommentInfo End!");
    }

    @Override
    public void insertCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".InsertCommentInfo Start!");

        String comment = CmmUtil.nvl(pDTO.comment());
        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("comment : " + comment);
        log.info("userId : " + userId);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        CommentEntity pEntity = CommentEntity.builder()
                .conment(comment).userId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        commentRepository.save(pEntity);

        log.info(this.getClass().getName() + ".InsertNoticeInfo End!");

    }
}
