package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_INFO")
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Cacheable

public class UserInfoEntity implements Serializable {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @NonNull
    @Column(name = "USER_NAME", length = 100, nullable = false)
    private String userName;

    @NonNull
    @Column(name = "NICK_NAME", length = 100, nullable = false)
    private String nickName;

    @NonNull
    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @NonNull
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @NonNull
    @Column(name = "ADDR1", nullable = false)
    private String addr1;

    @NonNull
    @Column(name = "ADDR2", nullable = false)
    private String addr2;

    @NonNull
    @Column(name = "REG_ID", updatable = false)
    private String regId;

    @NonNull
    @Column(name = "REG_DT", updatable = false)
    private String regDt;

    @Column(name = "CHG_ID")
    private String chgId;

    @Column(name = "CHG_DT")
    private String chgDt;

    @Column(name = "GENDER", length = 100, nullable = false)
    private String gender;

    @Column(name = "GENRE", length = 100, nullable = false)
    private String genre;
}