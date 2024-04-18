//package kopo.poly.repository;
//
//import kopo.poly.repository.entity.MailEntity;
//import kopo.poly.repository.entity.UserInfoEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface MailRepository extends JpaRepository<MailEntity, String > {
//    Optional<UserInfoEntity> findByUserIdAndPassword(String doMail, String title, String contents);
//}
