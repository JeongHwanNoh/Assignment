package kopo.poly.service;

import kopo.poly.dto.BookDTO;

import java.util.List;

public interface IBookService {
    List<BookDTO> searchBooks(String searchKeyword);
}
