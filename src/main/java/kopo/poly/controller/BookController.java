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

        model.addAttribute("SS_USER_ID", userId);
        model.addAttribute("rList", rList);

        if (!keyword.isEmpty()) {
            List<BookDTO> books = bookService.searchBooks(keyword);

            //책 검색을 위한 코드와 불러오는 코드

            model.addAttribute("books", books);
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("books", Collections.emptyList());
        }

        log.info(this.getClass().getName() + ".searchBook End");
        return "search/book";
    }

    @GetMapping("/detail")

    //상세보기
    public String showBookDetail(@RequestParam("isbn") String isbn, Model model, HttpSession session) {
        log.info(this.getClass().getName() + ".showBookDetail start");

        BookDTO bookDetail = bookService.getBookDetail(isbn);

        String userId = (String) session.getAttribute("SS_USER_ID");

        List<BookDTO> rList = Optional.ofNullable(bookService.getBookList(userId))
                .orElseGet(ArrayList::new);

        model.addAttribute("SS_USER_ID", userId);
        model.addAttribute("rList", rList);

        if (bookDetail != null) {
            model.addAttribute("bookDetail", bookDetail);
        } else {
            model.addAttribute("error", "Book details not found.");
        }

        log.info(this.getClass().getName() + ".showBookDetail End");
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
                    .isbn(pDTO.isbn())
                    .build();

            /*
             * 찜하기 등록하기위한 비즈니스 로직을 호출
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

            log.info("bookSeq : " + bookSeq);

            BookDTO pDTO = BookDTO.builder().bookSeq(Long.parseLong(bookSeq)).build();

            // 찜하기 삭제하기 DB
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
