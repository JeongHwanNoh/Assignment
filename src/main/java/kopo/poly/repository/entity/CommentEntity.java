package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMMENT")
@DynamicInsert
@DynamicUpdate
@Builder
@Cacheable
@Entity
@IdClass(CommentPK.class)
public class CommentEntity {

    @Id
    @Column(name = "notice_seq")
    private Long noticeSeq;

    @Id
    @Column(name = "comment_seq")
    private Long commentSeq;

    @NonNull
    @Column(name = "comment", nullable = false)
    private String comment;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "reg_dt", updatable = false)
    private String regDt;

    @Column(name = "chg_dt")
    private String chgDt;


}
