package kopo.poly.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {

    private Long likeSeq;  // 좋아요 시퀀스 ID
    private Long noticeSeq;  // 게시물 시퀀스 ID
    private String userId;   // 사용자 ID

}
