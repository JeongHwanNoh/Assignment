package kopo.poly.controller;

import kopo.poly.dto.BookDTO;
import kopo.poly.service.IBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
public class BookController {

    private final IBookService bookService;

    @GetMapping("/book")
    public String searchBook(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
        log.info(this.getClass().getName() + ".searchBook start");

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

}
