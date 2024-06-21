package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final ICommentService commentService;

    @ResponseBody
    @PostMapping(value = "commentInsert")
    public MsgDTO commentInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".commentInsert Start!");

        String msg = ""; // 메시지 내용

        MsgDTO dto; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            Long noticeSeq = Long.parseLong(CmmUtil.nvl(request.getParameter("noticeSeq")));
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String comment = CmmUtil.nvl(request.getParameter("comment"));

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session user_id : " + userId);
            log.info("comment : " + comment);
            log.info("noticeSeq : " + noticeSeq);

            // 데이터 저장하기 위해 DTO에 저장하기
            CommentDTO pDTO = CommentDTO.builder().noticeSeq(noticeSeq).userId(userId).comment(comment).build();

            commentService.insertComment(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentInsert End!");
        }

        return dto;
    }
    @ResponseBody
    @PostMapping(value = "commentUpdate")
    public MsgDTO commentUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".commentUpdate Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq")); // 글번호(PK)
            String comment = CmmUtil.nvl(request.getParameter("comment")); // 제목
            String noticeSeq = CmmUtil.nvl(request.getParameter("noticeSeq"));

            log.info("userId : " + userId);
            log.info("comment : " + comment);
            log.info("commentSeq : " + commentSeq);
            log.info("noticeSeq : " + noticeSeq);

            CommentDTO pDTO = CommentDTO.builder().noticeSeq(Long.valueOf(noticeSeq)).userId(userId).commentSeq(Long.parseLong(commentSeq))
                    .comment(comment).build();

            // 게시글 수정하기 DB
            commentService.updateComment(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentUpdate End!");

        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "commentDelete")
    public MsgDTO noticeDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".commentDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String noticeSeq = CmmUtil.nvl(request.getParameter("noticeSeq"));
            String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq")); // 글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("noticeSeq : " + noticeSeq);
            log.info("commentSeq : " + commentSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CommentDTO pDTO = CommentDTO.builder().noticeSeq(Long.parseLong(noticeSeq)).commentSeq(Long.parseLong(commentSeq)).build();

            commentService.deleteComment(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentDelete End!");

        }

        return dto;
    }

}
