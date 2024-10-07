package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTICE_LIKES")
@DynamicInsert
@DynamicUpdate
@Builder
@Cacheable
@Entity
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_seq")
    private Long likeSeq;

    @NonNull
    @Column(name = "notice_seq", nullable = false)
    private Long noticeSeq;


    @NonNull
    @Column(name = "user_id", nullable = false)
    private String userId;

}
