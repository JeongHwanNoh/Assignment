package kopo.poly.dto;

import lombok.Builder;
import lombok.Setter;

@Builder
public record MsgDTO(
        int result, // 성공 : 1 / 실패 : 그 외
        String msg // 메시지

) {
}
