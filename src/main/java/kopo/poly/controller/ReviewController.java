package kopo.poly.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.IBookService;
import kopo.poly.service.INoticeService;
import kopo.poly.service.IReviewService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequestMapping(value = "/review")
@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final IReviewService reviewService;

    private final IBookService bookService;

    @GetMapping("/searchpop")
    public String searchpop(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model, HttpSession session) {
        log.info(this.getClass().getName() + ".searchBook start");

        String userId = (String) session.getAttribute("SS_USER_ID");

        List<ReviewDTO> books = Collections.emptyList();

        if (!keyword.isEmpty()) {
            books = reviewService.searchBooks(keyword);
            log.info("Keyword: " + keyword);
            log.info("Books: " + books);
        } else {
            log.info("No keyword provided, skipping search.");
        }

        List<BookDTO> rList = Optional.ofNullable(bookService.getBookList(userId))
                .orElseGet(ArrayList::new);

        model.addAttribute("SS_USER_ID", userId);
        model.addAttribute("rList", rList);

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        log.info("Model attribute 'keyword': " + model.getAttribute("keyword"));
        log.info("Model attribute 'books': " + model.getAttribute("books"));

        log.info(this.getClass().getName() + ".searchBook End");

        return "review/searchpop";
    }

    @GetMapping(value = "reviewReg")
    public String reviewReg() {

        log.info(this.getClass().getName() + ".reviewReg Start!");

        log.info(this.getClass().getName() + ".reviewReg End!");

        // 함수 처리가 끝나고 보여줄 HTML (Thymeleaf) 파일명
        // templates/notice/noticeReg.html
        return "review/reviewReg";
    }

    @GetMapping(value = "reviewList")
    public String reviewList(HttpSession session, ModelMap model)
            throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".reviewList Start!");

        // 로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트 출력하는 함수에서 로그인 한 것처럼 Session 값을 생성함
        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");
        log.info("SS_USER_ID : " + SS_USER_ID);

        // 공지사항 리스트 조회하기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<ReviewDTO> rList = Optional.ofNullable(reviewService.getReviewList())
                .orElseGet(ArrayList::new);

        model.addAttribute("SS_USER_ID", SS_USER_ID);

        log.info("rList : " + rList);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".reviewList End!");

        // 함수 처리가 끝나고 보여줄 HTML (Thymeleaf) 파일명
        // templates/notice/noticeList.html
        return "review/reviewList";

    }

    @ResponseBody
    @PostMapping(value = "ReviewInsert")
    public MsgDTO reviewInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".reviewInsert Start!");

        String msg = ""; // 메시지 내용

        MsgDTO dto; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용
            String author = CmmUtil.nvl(request.getParameter("author")); // 내용
            String imageUrl = CmmUtil.nvl(request.getParameter("imageUrl"));
            Long rating = Long.parseLong(CmmUtil.nvl(request.getParameter("rating")));

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session user_id : " + userId);
            log.info("title : " + title);
            log.info("contents : " + contents);
            log.info("author : " + author);
            log.info("imageUrl : " + imageUrl);
            log.info("rating : " + rating);

            // 데이터 저장하기 위해 DTO에 저장하기
            ReviewDTO pDTO = ReviewDTO.builder().userId(userId).title(title).author(author)
                    .contents(contents).imageUrl(imageUrl).rating(rating).build();

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            reviewService.insertReviewInfo(pDTO);

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

            log.info(this.getClass().getName() + ".reviewInsert End!");
        }

        return dto;
    }

    @GetMapping(value = "reviewInfo")
    public String reviewInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".reviewInfo Start!");

        String reviewSeq = CmmUtil.nvl(request.getParameter("reviewSeq"), "0");

        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");
        log.info("SS_USER_ID : " + SS_USER_ID);

        model.addAttribute("SS_USER_ID", SS_USER_ID);

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("reviewSeq : " + reviewSeq);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        ReviewDTO pDTO = ReviewDTO.builder().reviewSeq(Long.parseLong(reviewSeq)).build();

        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        ReviewDTO rDTO = Optional.ofNullable(reviewService.getReviewInfo(pDTO, true))
                .orElseGet(() -> ReviewDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);


        log.info(this.getClass().getName() + ".reviewInfo End!");

        return "review/reviewInfo";
    }

    @GetMapping(value = "reviewEditInfo")
    public String reviewEditInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".reviewEditInfo Start!");

        String reviewSeq = CmmUtil.nvl(request.getParameter("reviewSeq"));

        log.info("reviewSeq : " + reviewSeq);

        ReviewDTO pDTO = ReviewDTO.builder().reviewSeq(Long.parseLong(reviewSeq)).build();

        ReviewDTO rDTO = Optional.ofNullable(reviewService.getReviewInfo(pDTO, false))
                .orElseGet(() -> ReviewDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".reviewEditInfo End!");

        return "review/reviewEditInfo";
    }

    @ResponseBody
    @PostMapping(value = "reviewUpdate")
    public MsgDTO reviewUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".reviewUpdate Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String reviewSeq = CmmUtil.nvl(request.getParameter("reviewSeq")); // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용
            String author = CmmUtil.nvl(request.getParameter("author")); // 작성자
            String imageUrl = CmmUtil.nvl(request.getParameter("imageUrl"));
            Long rating = Long.parseLong(CmmUtil.nvl(request.getParameter("rating")));
            String regDt = CmmUtil.nvl(request.getParameter("regDt"));

            // regDt 값이 비어 있으면 현재 날짜로 설정
            if (regDt.isEmpty()) {
                regDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

            log.info("reviewSeq : " + reviewSeq);
            log.info("session user_id : " + userId);
            log.info("title : " + title);
            log.info("contents : " + contents);
            log.info("author : " + author);
            log.info("imageUrl : " + imageUrl);
            log.info("rating : " + rating);
            log.info("regDt : " + regDt);

            // 데이터 저장하기 위해 DTO에 저장하기
            ReviewDTO pDTO = ReviewDTO.builder().userId(userId).title(title).author(author)
                    .reviewSeq(Long.parseLong(reviewSeq)).contents(contents).imageUrl(imageUrl).regDt(regDt)
                    .rating(rating).build();

            reviewService.updateReviewInfo(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".reviewUpdate End!");

        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "reviewDelete")
    public MsgDTO reviewDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".reviewDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String reviewSeq = CmmUtil.nvl(request.getParameter("reviewSeq")); // 글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("reviewSeq : " + reviewSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            ReviewDTO pDTO = ReviewDTO.builder().reviewSeq(Long.parseLong(reviewSeq)).build();

            // 게시글 삭제하기 DB
            reviewService.deleteReviewInfo(pDTO);

            msg = "삭제되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".noticeDelete End!");

        }

        return dto;
    }

}
