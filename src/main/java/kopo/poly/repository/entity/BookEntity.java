package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOK")
@DynamicInsert
@DynamicUpdate
@Builder
@Entity


public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_seq")
    private Long bookSeq;

    @NonNull
    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;

    @NonNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NonNull
    @Column(name = "author", length = 100, nullable = false)
    private String author;

    @NonNull
    @Column(name = "image_url", length = 100, nullable = false)
    private String imageUrl;

    @NonNull
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserInfoEntity userInfoEntity;

}