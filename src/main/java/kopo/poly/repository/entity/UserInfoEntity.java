package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_INFO")
@DynamicInsert
@DynamicUpdate
@Builder
@Getter
@Entity
@Cacheable
public class UserInfoEntity implements Serializable {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @NonNull
    @Column(name = "USER_NAME", length = 100, nullable = false)
    private String userName;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @NonNull
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "ADDR1", nullable = false)
    private String addr1;

    @Column(name = "ADDR2", nullable = false)
    private String addr2;

    @NonNull
    @Column(name = "REG_ID", updatable = false)
    private String regId;

    @Column(name = "REG_DT", updatable = false)
    private String regDt;

    @Column(name = "CHG_ID")
    private String chgId;

    @Column(name = "CHG_DT")
    private String chgDt;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "PROVIDER")
    private String provider;

    @Column(name = "GENRE", length = 100, nullable = false)
    private String genre;
}

