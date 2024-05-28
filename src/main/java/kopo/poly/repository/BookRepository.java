package kopo.poly.repository;
import kopo.poly.repository.entity.BookEntity;
import kopo.poly.repository.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {
    List<BookEntity> findByTitleContaining(String searchKeyword);

    BookEntity findByBookSeq(Long bookSeq);

    List<BookEntity> findAllByOrderByBookSeqDesc();

}