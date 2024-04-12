package kopo.poly.repository;

import kopo.poly.repository.entity.CalendarEntity;
import kopo.poly.repository.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {

    List<CalendarEntity> findAllByOrderByCalendarSeqDesc();

    /**
     * 공지사항 리스트
     *
     */
    CalendarEntity findByCalendarSeq(Long calendarSeq);
}
