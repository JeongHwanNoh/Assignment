package kopo.poly.controller;


import kopo.poly.dto.BookDTO;
import kopo.poly.service.IBookService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 * */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
public class BookController {

    private final IBookService bookService;

    @GetMapping("/book")
    public String searchBook(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {

        log.info(this.getClass().getName() + ".searchBook start");

        if (!keyword.isEmpty()) {
            List<BookDTO> books = bookService.searchBooks(keyword);
            model.addAttribute("books", books);
        }
        model.addAttribute("keyword", keyword);

        log.info(this.getClass().getName() + ".searchBook End");

        return "search/book";
    }
}