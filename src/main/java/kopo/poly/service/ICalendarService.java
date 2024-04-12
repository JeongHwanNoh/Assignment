package kopo.poly.service;

import kopo.poly.dto.CalendarDTO;

import java.util.List;

public interface ICalendarService {

    /**
     * 공지사항 전체 가져오기
     */
    List<CalendarDTO> getCalendarList();

    /**
     * 해당 공지사항 수정하기
     *
     * @param pDTO 공지사항 수정하기 위한 정보
     */
    void updateCalendarInfo(CalendarDTO pDTO) throws Exception;

    /**
     * 해당 공지사항 삭제하기
     *
     * @param pDTO 공지사항 삭제하기 위한 정보
     */
    void deleteCalendarInfo(CalendarDTO pDTO) throws Exception;

    /**
     * 해당 공지사항 저장하기
     *
     * @param pDTO 공지사항 저장하기 위한 정보
     */
    void insertCalendarInfo(CalendarDTO pDTO) throws Exception;

}
