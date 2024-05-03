package kopo.poly.repository;

import kopo.poly.repository.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {


    // 아이디 중복체크
    Optional<UserInfoEntity> findByUserId(String userId);


    // 로그인
    Optional<UserInfoEntity> findByUserIdAndPassword(String userId, String password);


    List<UserInfoEntity> findAllByUserIdOrderByUserIdDesc(String userId);


    Optional<UserInfoEntity> findByUserIdAndUserNameAndEmail(String userId, String email, String userName);
    // 아이디 찾기
  Optional<UserInfoEntity> findByUserNameAndEmail(String email, String userName);

    Optional<UserInfoEntity> findByEmail(String email);



}
