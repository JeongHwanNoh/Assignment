package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoDTO(

        String userId,
        String userName,
        @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
        @Size(max = 16, message = "비밀번호는 16글자까지 입력가능합니다.")
        String password, // 비밀번호
        @NotBlank(message = "이메일은 필수 입력 사항입니다.")
        @Size(max = 30, message = "이메일은 30글자까지 입력가능합니다.")
        String email,
        String addr1,
        String addr2,
        String regId,
        String regDt,
        String chgId,
        String chgDt,
        String existsYn,
        String genre,
        int authNumber,
        int mailNumber

) implements Serializable {

        /**
         * 패스워드, 권한 등 회원 가입을 위한 정보 만들기
         */
        public static UserInfoDTO createUser(UserInfoDTO pDTO, String password) throws Exception {

                UserInfoDTO rDTO = UserInfoDTO.builder()
                        .userId(pDTO.userId())
                        .userName(pDTO.userName())
                        .password(password) // Spring Security 생성해준 암호화된 비밀번호
                        .email(EncryptUtil.encAES128CBC(pDTO.email()))
                        .regId(pDTO.userId())
                        .chgId(pDTO.userId())
                        .addr1(pDTO.addr1())
                        .addr2(pDTO.addr2())
                        .regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                        .genre(pDTO.genre())
                        .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                        .build();

                return rDTO;
        }

        /**
         * DTO 결과를 entity 변환하기
         * 이전 실습에서 진행한 Jackson 객체를 통해 처리도 가능함
         */
        public static UserInfoEntity of(UserInfoDTO dto) {

                UserInfoEntity entity = UserInfoEntity.builder()
                        .userId(dto.userId())
                        .userName(dto.userName())
                        .password(dto.password())
                        .email(dto.email())
                        .regId(dto.regId())
                        .regDt(dto.regDt())
                        .chgId(dto.chgId())
                        .addr1(dto.addr1())
                        .addr2(dto.addr2())
                        .genre(dto.genre())
                        .chgDt(dto.chgDt()).build();

                return entity;
        }

        /**
         * JPA로 전달받은 entity 결과를 DTO로 변환하기
         * 이전 실습에서 진행한 Jackson 객체를 통해 처리도 가능함
         */
        public static UserInfoDTO from(UserInfoEntity entity) throws Exception {

                UserInfoDTO rDTO = UserInfoDTO.builder()
                        .userId(entity.getUserId())
                        .userName(entity.getUserName())
                        .password(entity.getPassword())
                        .email(EncryptUtil.decAES128CBC(CmmUtil.nvl(entity.getEmail())))
                        .regId(entity.getRegId())
                        .regDt(entity.getRegDt())
                        .chgId(entity.getChgId())
                        .addr1(entity.getAddr1())
                        .addr2(entity.getAddr2())
                        .genre(entity.getGenre())
                        .chgDt(entity.getChgDt()).build();

                return rDTO;
        }
}
