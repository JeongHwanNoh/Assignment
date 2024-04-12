package kopo.poly.repository;

import kopo.poly.repository.entity.NoticeEntity;
import kopo.poly.repository.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {

    Optional<UserInfoEntity> findByUserId(String userId);

    Optional<UserInfoEntity> findByUserIdAndPassword(String userId, String password);
    List<UserInfoEntity> findAllByOrderByUserIdDesc();

    UserInfoEntity findByPassword(String password);



}
