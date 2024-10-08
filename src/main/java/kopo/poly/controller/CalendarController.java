package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.entity.CalendarEntity;
import kopo.poly.service.ICalendarService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequestMapping(value = "/calendar")
@RequiredArgsConstructor
@Controller
public class CalendarController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final ICalendarService calendarService;

    @GetMapping(value = "info")
    public String calendarinfo(HttpSession session, ModelMap model) throws Exception {

        String SS_USER_ID = (String) session.getAttribute("SS_USER_ID");

        model.addAttribute("SS_USER_ID", SS_USER_ID);

        log.info("SS_USER_ID : " + SS_USER_ID);

        // 세션에서 사용자 아이디 가져오기
        String userId = (String) session.getAttribute("SS_USER_ID");
        log.info("User ID: " + userId);

        // 리스트 조회하기
        List<CalendarDTO> rList = Optional.ofNullable(calendarService.getCalendarList(userId))
                .orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info("확인" + rList);

        // 함수 처리가 끝나고 보여줄 HTML (Thymeleaf) 파일명
        return "calendar/info";
    }

    @ResponseBody
    @PostMapping(value = "calendarInsert")
    public MsgDTO calendarInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".calendarInsert Start!");

        String msg = ""; // 메시지 내용

        MsgDTO dto; // 결과 메시지 구조

        try {
            //캘린더 저장할 데이터들
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String start = CmmUtil.nvl(request.getParameter("start")); // 공지글 여부
            String end = CmmUtil.nvl(request.getParameter("end")); // 내용
            String description = CmmUtil.nvl(request.getParameter("description")); // 내용

            log.info("session user_id : " + userId);
            log.info("title : " + title);
            log.info("start : " + start);
            log.info("end : " + end);
            log.info("description : " + description);

            // 데이터 저장하기 위해 DTO에 저장하기
            CalendarDTO pDTO = CalendarDTO.builder().userId(userId).title(title)
                    .start(start).end(end).description(description).build();

            calendarService.insertCalendarInfo(pDTO);

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

            log.info(this.getClass().getName() + ".CalendarInsert End!");
        }

        return dto;
    }

    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "calendarUpdate")
    public MsgDTO calemdarUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".calendarUpdate Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            String calendarSeq = CmmUtil.nvl(request.getParameter("calendarSeq")); // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String start = CmmUtil.nvl(request.getParameter("start")); // 공지글 여부
            String end = CmmUtil.nvl(request.getParameter("end")); // 내용
            String description = CmmUtil.nvl(request.getParameter("description"));

            log.info("userId : " + userId);
            log.info("calendarSeq : " + calendarSeq);
            log.info("title : " + title);
            log.info("start : " + start);
            log.info("end : " + end);
            log.info("description : " + description);


            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CalendarDTO pDTO = CalendarDTO.builder().userId(userId).calendarSeq(Long.parseLong(calendarSeq))
                    .title(title).start(start).end(end).description(description).build();

            // 게시글 수정하기 DB
            calendarService.updateCalendarInfo(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".calendarUpdate End!");

        }

        return dto;
    }
    @ResponseBody
    @PostMapping(value = "calendarDelete")
    public MsgDTO calendarDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".calendarDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String calendarSeq = CmmUtil.nvl(request.getParameter("calendarSeq")); // 글번호(PK)

            log.info("calendarSeq : " + calendarSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CalendarDTO pDTO = CalendarDTO.builder().calendarSeq(Long.parseLong(calendarSeq)).build();

            // 캘린더 삭제하기 DB
            calendarService.deleteCalendarInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".calendarDelete End!");

        }

        return dto;
    }
}
