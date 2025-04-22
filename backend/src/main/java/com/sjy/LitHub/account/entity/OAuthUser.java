package com.sjy.LitHub.account.entity;

import com.sjy.LitHub.account.entity.authenum.ProviderInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "oauth_account", indexes = {
    @Index(name = "idx_user_provider", columnList = "user_id, provider") // 소셜 연동 여부 확인용
})
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 10)
    private ProviderInfo provider;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;
}