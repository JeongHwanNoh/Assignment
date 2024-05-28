package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REVIEW")
@DynamicInsert
@DynamicUpdate
@Builder
@Entity


public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_seq")
    private Long reviewSeq;

    @NonNull
    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @NonNull
    @Column(name = "author", length = 100, nullable = false)
    private String author;

    @NonNull
    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NonNull
    @Column(name = "contents", length = 2000, nullable = false)
    private String contents;

    @NonNull
    @Column(name = "rating", nullable = false)
    private Long rating;

    @NonNull
    @Column(name = "reg_dt", nullable = false)
    private String regDt;

//    @OneToOne
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private UserInfoEntity userInfoEntity;



}
