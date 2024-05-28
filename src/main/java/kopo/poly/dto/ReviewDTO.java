package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ReviewDTO(

        Long reviewSeq,
        String userId,
        String title,
        String author,
        Long rating,
        String contents,
        String regDt,
        String imageUrl
) {
}
