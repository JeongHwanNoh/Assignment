package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record CalendarDTO(

        Long calendarSeq,
        String title,
        String userId,
        String start,
        String end,
        String description

) {
}
