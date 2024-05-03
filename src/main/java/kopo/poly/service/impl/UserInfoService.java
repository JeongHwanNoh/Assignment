package kopo.poly.service.impl;

import jakarta.transaction.Transactional;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.CalendarEntity;
import kopo.poly.repository.entity.NoticeEntity;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    private final UserInfoRepository userInfoRepository;

    private final MailService mailService;


    @Override
    public List<UserInfoDTO> getUserList(String userId) {
        log.info("Fetching data for user: {}", userId);

        List<UserInfoEntity> rList = userInfoRepository.findAllByUserIdOrderByUserIdDesc(userId);

        List<UserInfoDTO> nList = rList.stream()
                .map(userInfoEntity -> UserInfoDTO.builder()
                        .userId(userInfoEntity.getUserId())
                        .password(userInfoEntity.getPassword())
                        .addr1(userInfoEntity.getAddr1())
                        .addr2(userInfoEntity.getAddr2())
                        .userName(userInfoEntity.getUserName())
                        .email(userInfoEntity.getEmail())
                        .genre(userInfoEntity.getGenre())
                        .build())
                .collect(Collectors.toList());

        log.info("Calendar data fetched successfully for user: {}", userId);

        return nList;
    }

    // 아이디 중복체크
    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO;

        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("userId : " + userId);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            rDTO = UserInfoDTO.builder().existsYn("Y").build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }
        log.info(this.getClass().getName() + ".getUserIdExist End!");

        return rDTO;
    }


    // 이메일 중복체크
    @Override
    public UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");


        UserInfoDTO rDTO;


        String email = CmmUtil.nvl(pDTO.email());
        String existsYn;

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByEmail(email);


        if (rEntity.isPresent()) {
            existsYn = "Y";
        } else {
            existsYn = "N";
        }

        log.info("existsYn : " + existsYn);

        UserInfoDTO.UserInfoDTOBuilder builder = UserInfoDTO.builder().existsYn(existsYn);

        if (existsYn.equals("N")) {

            // 6자리 랜덤 숫자 생성하기
            int authNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

            log.info("authNumber : " + authNumber);

            builder.authNumber(authNumber);

            // 인증번호 발송 로직
            MailDTO dto = MailDTO.builder()
                    .title("인증번호 발송 메일")
                    .contents("인증번호는 " + authNumber + " 입니다.")
                    .toMail(email)
                    .build();

            mailService.doSendMail(dto); // 이메일 발송

        }

        rDTO = builder.build();

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }


    //회원가입
    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo start!");

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String userName = CmmUtil.nvl(pDTO.userName());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());
        String addr1 = CmmUtil.nvl(pDTO.addr1());
        String addr2 = CmmUtil.nvl(pDTO.addr2());
        String gender = CmmUtil.nvl(pDTO.gender());
        String genre = CmmUtil.nvl(pDTO.genre());

        log.info("pDTO : " + pDTO);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if (rEntity.isPresent()) {
            res = 2;
        } else {
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName)
                    .password(password)
                    .email(email)
                    .addr1(addr1).addr2(addr2)
                    .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .genre(genre)
                    .build();

            userInfoRepository.save(pEntity);

            rEntity = userInfoRepository.findByUserId(userId);

            if (rEntity.isPresent()) {
                res = 1;
            } else {
                res = 0;
            }
        }
        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return res;
    }


    //로그인
    @Override
    public int getUserLogin(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getUserLogin Start!");

        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String password = CmmUtil.nvl(pDTO.password());

        log.info("userId : " + userId);
        log.info("password : " + password);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (rEntity.isPresent()) {
            if (rEntity.isPresent()) {
                res = 1;
            }
        }
        log.info(this.getClass().getName() + ".getuserLoginCheck End!");

        return res;

    }
    @Override
    public String searchUserIdProc(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".searchUserIdProc Start!");

        String res = "";

        String userName = CmmUtil.nvl(pDTO.userName());
        String email = CmmUtil.nvl(pDTO.email());

        log.info("pDTO userName : " + userName);
        log.info("pDTO email : " + email);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserNameAndEmail(pDTO.userName(), pDTO.email());

        log.info("rEntity : " + rEntity);

        if (rEntity.isPresent()) {
            UserInfoEntity userInfoEntity = rEntity.get();
            String userId = userInfoEntity.getUserId();
            log.info("Found userId: " + userId); // userId가 성공적으로 조회되었을 때 로그 출력
            res = userId;
        }

        log.info(this.getClass().getName() + ".searchUserIdProc End!");

        return res;
    }

    @Override
    public String searchPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".searchUserIdProc Start!");

        String res = "";

        String userId = CmmUtil.nvl(pDTO.userId());
        String userName = CmmUtil.nvl(pDTO.userName());
        String email = CmmUtil.nvl(pDTO.email());

        log.info("pDTO userId : " + userId);
        log.info("pDTO userName : " + userName);
        log.info("pDTO email : " + email);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndUserNameAndEmail(pDTO.userId(), pDTO.userName(), pDTO.email());

        log.info("rEntity : " + rEntity);

        if (rEntity.isPresent()) {
            UserInfoEntity userInfoEntity = rEntity.get();
            String password = userInfoEntity.getPassword();
            log.info("Found password: " + password); // userId가 성공적으로 조회되었을 때 로그 출력
            res = password;
        }

        log.info(this.getClass().getName() + ".searchUserIdProc End!");

        return res;
    }
    @Transactional
    @Override
    public void newPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".newPasswordProc start!");

        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("userId : " + userId);

        Optional<UserInfoEntity> uEntity = userInfoRepository.findByUserId(userId);

        if (uEntity.isPresent()) {
            UserInfoEntity rEntity = uEntity.get();

            log.info("rEntity userId : " + rEntity.getUserId());
            log.info("rEntity password : " + rEntity.getPassword());
            log.info("rEntity userName : " + rEntity.getUserName());
            log.info("rEntity email : " + rEntity.getEmail());
            log.info("rEntity addr1 : " + rEntity.getAddr1());
            log.info("rEntity addr2 : " + rEntity.getAddr2());

            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(rEntity.getUserId())
                    .userName(rEntity.getUserName())
                    .password(pDTO.password())
                    .email(rEntity.getEmail())
                    .addr1(rEntity.getAddr1())
                    .addr2(rEntity.getAddr2())
                    .regId(rEntity.getRegId())
                    .regDt(rEntity.getRegDt())
                    .chgId(rEntity.getUserId())
                    .chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .genre(rEntity.getGenre())
                    .build();

            log.info("Building pEntity with addr1: " + pEntity.getAddr1() + ", addr2: " + pEntity.getAddr2());

            userInfoRepository.save(pEntity);

            log.info("User info updated in database.");
        } else {
            log.info("No user found with userId: " + userId);
        }

        log.info(this.getClass().getName() + ".newPasswordProc End!");
    }



//    @Transactional
//    @Override
//    public void newPasswordProc(UserInfoDTO pDTO) throws Exception {
//
//        log.info(this.getClass().getName() + ".newPasswordProc start!");
//
//        String userId = CmmUtil.nvl(pDTO.userId());
//
//        log.info("userId : " + userId);
//
//
//        // 사용자 ID로 사용자 엔티티 조회
//        // Select *
//        // Froe 컬렉션
//        // where UserId = (userId)
//        Optional<UserInfoEntity> uEntity = userInfoRepository.findByUserId(userId);
//
//        if (uEntity.isPresent()) {
//
//            UserInfoEntity rEntity = uEntity.get();
//
//            log.info("rEntity userId : " + rEntity.getUserId());
//            log.info("rEntity password : " + rEntity.getPassword());
//            log.info("rEntity userName : " + rEntity.getUserName());
//            log.info("rEntity email : " + rEntity.getEmail());
//            log.info("rEntity addr1 : " + rEntity.getAddr1());
//            log.info("rEntity addr2 : " + rEntity.getAddr2());
//
//            UserInfoEntity pEntity = UserInfoEntity.builder()
//                    .userId(rEntity.getUserId()).userName(rEntity.getUserName())
//                    .password(pDTO.password())
//                    .email(rEntity.getEmail())
//                    .regId(rEntity.getUserId()).regDt(rEntity.getRegDt())
//                    .chgId(rEntity.getUserId()).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
//                    .gender(rEntity.getGender()) // 추가적인 필드
//                    .genre(rEntity.getGenre())  // 추가적인 필드
//                    .build();
//
//            userInfoRepository.save(pEntity);
//
//            log.info("확인");
//
//        }
//
//        log.info(this.getClass().getName() + ".newPasswordProc End!");
//    }


    @Override
    public void updateUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updateUserInfo Start!");

        String userId = pDTO.userId();

        String userName = CmmUtil.nvl(pDTO.userName());
        String addr1 = CmmUtil.nvl(pDTO.addr1());
        String addr2 = CmmUtil.nvl(pDTO.addr2());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());
        String genre = CmmUtil.nvl(pDTO.genre());
        String gender = CmmUtil.nvl(pDTO.gender());

        log.info("userName : " + userName);
        log.info("addr1 : " + addr1);
        log.info("noticeYn : " + addr2);
        log.info("email : " + email);
        log.info("genre : " + genre);
        log.info("gender : " + gender);
        log.info("password : " + password);

        // 수정할 값들을 빌더를 통해 엔티티에 저장하기
        UserInfoEntity pEntity = UserInfoEntity.builder()
                .userId(userId).addr1(addr1).addr2(addr2).email(email).genre(genre)
                .password(pDTO.password())
                .build();

        // 데이터 수정하기
        userInfoRepository.save(pEntity);

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String userId = pDTO.userId();

        log.info("userId : " + userId);

        userInfoRepository.deleteById(userId);


        log.info(this.getClass().getName() + ".deleteUserInfo End!");

    }
}
