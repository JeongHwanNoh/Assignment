package kopo.poly.service;

import kopo.poly.dto.BookDTO;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IBookService {
    List<BookDTO> searchBooks(String searchKeyword);

    BookDTO getBookDetail(String title);

    void insertBookInfo(BookDTO pDTO) throws Exception;

    void deleteBookInfo(BookDTO pDTO) throws Exception;

    List<BookDTO> getBookList(String userId);
}
