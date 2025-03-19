package com.sjy.LitHub.account.entity;

import com.sjy.LitHub.account.entity.authenum.Role;
import com.sjy.LitHub.account.entity.authenum.Tier;
import com.sjy.LitHub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTime {

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(name = "user_nickname", nullable = false, unique = true, length = 50)
    private String nickName;

    @Column(name = "user_password", nullable = false, length = 60)
    private String password;

    @Column(name = "profile_image_url_small", nullable = false)
    private String profileImageUrlSmall;

    @Column(name = "profile_image_url_large", nullable = false)
    private String profileImageUrlLarge;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 10)
    @Builder.Default
    private Tier tier = Tier.BRONZE;

    @Column(name = "points", nullable = false)
    @Builder.Default
    private Integer point = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void encodePassword(BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
