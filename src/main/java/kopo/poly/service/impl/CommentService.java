package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.CommentDTO;
import kopo.poly.repository.CommentRepository;
import kopo.poly.repository.entity.CommentEntity;
import kopo.poly.repository.entity.CommentPK;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;



    //댓글 리스트를 보여주는 코드
    @Override
    public List<CommentDTO> getCommentList(CommentDTO pDTO) {

        log.info(this.getClass().getName() + ".getCommentList Start!");

        Long noticeSeq = pDTO.noticeSeq();

        log.info("noticeSeq : " + noticeSeq);

        //노티스 번호에 맞는 댓글만 불러옴
        List<CommentEntity> rList = commentRepository.findByNoticeSeqOrderByCommentSeqAsc(noticeSeq);

        List<CommentDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<>() {
                });

        log.info(this.getClass().getName() + ".getCommentList End!");

        return nList;
    }


    //댓글 수정
    @Override
    public int updateComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateComment Start!");

        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = pDTO.commentSeq();

        CommentPK commentPK = CommentPK.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .build();

        int res = 0;

        Optional<CommentEntity> rEntity = commentRepository.findById(commentPK);

        if(rEntity.isPresent()){

            String userId = pDTO.userId();
            String comment = pDTO.comment();
            String chgDt = DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss");
            String regDt = rEntity.get().getRegDt();

            log.info("noticeSeq : " + noticeSeq);
            log.info("commentSeq : " + commentSeq);
            log.info("userId : " + userId);
            log.info("comment : " + comment);
            log.info("chgDt : " + chgDt);

            // 댓글 내용 DB에 저장 Update
            CommentEntity pEntity = CommentEntity.builder()
                    .noticeSeq(noticeSeq)
                    .commentSeq(commentSeq)
                    .userId(userId)
                    .comment(comment)
                    .regDt(regDt)
                    .chgDt(chgDt)
                    .build();

            commentRepository.save(pEntity);

            res = 1;
        }

        log.info(this.getClass().getName() + ".updateComment End!");

        return res;
    }

   //댓글 삭제
    @Override
    public void deleteComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteComment Start!");

        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = pDTO.commentSeq();

        log.info("noticeSeq : " + noticeSeq);
        log.info("commentSeq : " + commentSeq);

        CommentPK commentPK = CommentPK.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .build();

        // 데이터 삭제하기
        commentRepository.deleteById(commentPK);

        log.info(this.getClass().getName() + ".deleteComment End!");

    }


    //댓글 추가
    @Override
    public void insertComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertComment Start!");

        String userId = CmmUtil.nvl(pDTO.userId());
        String comment = CmmUtil.nvl(pDTO.comment());
        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = commentRepository.getMaxCommentsSeq(noticeSeq);

        log.info("userId : " + userId);
        log.info("comment : " + comment);
        log.info("noticeSeq : " + noticeSeq);
        log.info("commentSeq : " + commentSeq);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        CommentEntity pEntity = CommentEntity.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .userId(userId)
                .comment(comment)
                .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 댓글 저장하기
        commentRepository.save(pEntity);

        log.info(this.getClass().getName() + ".insertComment End!");

    }

}