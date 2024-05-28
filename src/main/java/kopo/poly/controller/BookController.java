package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.BookDTO;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.IBookService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
public class BookController {

    private final IBookService bookService;

    @GetMapping("/book")
    public String searchBook(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model, HttpSession session) {
        log.info(this.getClass().getName() + ".searchBook start");

        String userId = (String) session.getAttribute("SS_USER_ID");

        List<BookDTO> rList = Optional.ofNullable(bookService.getBookList(userId))
                .orElseGet(ArrayList::new);

        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        model.addAttribute("SS_USER_ID", SS_USER_ID);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info("확인" + rList);

        log.info("User ID: " + userId);

        List<BookDTO> books = Collections.emptyList();

        if (!keyword.isEmpty()) {
            books = bookService.searchBooks(keyword);
            log.info("Keyword: " + keyword);
            log.info("Books: " + books);
        } else {
            log.info("No keyword provided, skipping search.");
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        log.info("Model attribute 'keyword': " + model.getAttribute("keyword"));
        log.info("Model attribute 'books': " + model.getAttribute("books"));

        log.info(this.getClass().getName() + ".searchBook End");



        return "search/book";
    }

    @GetMapping("/detail")
    public String showBookDetail(@RequestParam("title") String title, Model model) {
        log.info(this.getClass().getName() + ".showBookDetail start");

        // 선택한 책의 제목을 기반으로 책 정보를 가져옴
        BookDTO bookDetail = bookService.getBookDetail(title);

        // 가져온 책 정보를 모델에 추가하여 상세보기 페이지로 전달
        model.addAttribute("bookDetail", bookDetail);

        log.info(this.getClass().getName() + ".showBookDetail End");

        // detail.html을 표시하는 View로 이동
        return "search/detail";
    }

    @ResponseBody
    @PostMapping(value = "bookInsert")
    public MsgDTO bookInsert(@RequestBody BookDTO pDTO, HttpSession session) {

        log.info(this.getClass().getName() + ".bookInsert Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            // 데이터 저장하기 위해 DTO에 사용자 아이디 설정
            pDTO = BookDTO.builder()
                    .userId(userId)
                    .title(pDTO.title())
                    .author(pDTO.author())
                    .imageUrl(pDTO.imageUrl())
                    .description(pDTO.description())
                    .build();

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            bookService.insertBookInfo(pDTO);

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
            log.info(this.getClass().getName() + ".BookInsert End!");
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "bookDelete")
    public MsgDTO bookDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".bookDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String bookSeq = CmmUtil.nvl(request.getParameter("bookSeq")); // 글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("bookSeq : " + bookSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            BookDTO pDTO = BookDTO.builder().bookSeq(Long.parseLong(bookSeq)).build();

            // 게시글 삭제하기 DB
            bookService.deleteBookInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".bookDelete End!");

        }

        return dto;
    }


}
