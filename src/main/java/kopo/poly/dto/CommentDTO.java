package kopo.poly.dto;

import lombok.Builder;

@Builder
public record CommentDTO(

        Long noticeSeq,
        Long commentSeq,
        String comment,
        String userId, // 작성자
        String regDt, // 등록일
        String chgDt

) {

}