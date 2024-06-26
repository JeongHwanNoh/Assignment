package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record UserInfoDTO(

        String userId,
        String userName,
        String password,
        String nickName,
        String email,
        String addr1,
        String gender,
        String addr2,
        String regId,
        String regDt,
        String chgId,
        String chgDt,
        String existsYn,
        String genre,
        int authNumber,
        int mailNumber

) {

}
