package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.repository.CalendarRepository;
import kopo.poly.repository.entity.CalendarEntity;
import kopo.poly.service.ICalendarService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarService implements ICalendarService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // noticeRepository 변수에 이미 메모리에 올라간 NoticeRepository 객체를 넣어줌
    // 예전에는 autowired 어노테이션를 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final CalendarRepository calendarRepository;

    // 서비스 계층의 getCalendarList 메서드 수정
    @Override
    public List<CalendarDTO> getCalendarList(String userId) {
        log.info("Fetching calendar data for user: {}", userId);

        List<CalendarEntity> rList = calendarRepository.findAllByUserIdOrderByCalendarSeqDesc(userId);

        List<CalendarDTO> nList = rList.stream()
                .map(calendarEntity -> CalendarDTO.builder()
                        .calendarSeq(calendarEntity.getCalendarSeq())
                        .title(calendarEntity.getTitle())
                        .userId(calendarEntity.getUserId())
                        .start(calendarEntity.getStart())
                        .end(calendarEntity.getEnd())
                        .description(calendarEntity.getDescription())
                        .build())
                .collect(Collectors.toList());

        log.info("Calendar data fetched successfully for user: {}", userId);

        return nList;
    }


    @Override
    public void updateCalendarInfo(CalendarDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updateCalendarInfo Start!");

        Long calendarSeq = pDTO.calendarSeq();

        String title = CmmUtil.nvl(pDTO.title());
        String userId = CmmUtil.nvl(pDTO.userId());
        String start = CmmUtil.nvl(pDTO.start());
        String end = CmmUtil.nvl(pDTO.end());
        String description = CmmUtil.nvl(pDTO.description());

        log.info("calendarSeq : " + calendarSeq);
        log.info("userId : " + userId);
        log.info("title : " + title);
        log.info("start : " + start);
        log.info("end : " + end);
        log.info("description : " + description);

        // 수정할 값들을 빌더를 통해 엔티티에 저장하기
        CalendarEntity pEntity = CalendarEntity.builder()
                .calendarSeq(calendarSeq).title(title).userId(userId).start(start).end(end).description(description)
                .build();

        // 데이터 수정하기
        calendarRepository.save(pEntity);

    }

    @Override
    public void deleteCalendarInfo(CalendarDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCalendarInfo Start!");

        Long calendarSeq = pDTO.calendarSeq();

        log.info("CalendarSeq : " + calendarSeq);

        // 데이터 수정하기
        calendarRepository.deleteById(calendarSeq);


        log.info(this.getClass().getName() + ".deleteCalendarInfo End!");

    }

    @Override
    public void insertCalendarInfo(CalendarDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".InsertNoticeInfo Start!");

        String title = CmmUtil.nvl(pDTO.title());
        String userId = CmmUtil.nvl(pDTO.userId());
        String start = CmmUtil.nvl(pDTO.start());
        String end = CmmUtil.nvl(pDTO.end());
        String description = CmmUtil.nvl(pDTO.description());

        log.info("title : " + title);
        log.info("userId : " + userId);
        log.info("start : " + start);
        log.info("end : " + end);
        log.info("description : " + description);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        CalendarEntity pEntity = CalendarEntity.builder()
                .title(title).userId(userId).start(start).end(end).description(description)
                .build();

        // 공지사항 저장하기
        calendarRepository.save(pEntity);

        log.info(this.getClass().getName() + ".InsertCalendarInfo End!");

    }
}

