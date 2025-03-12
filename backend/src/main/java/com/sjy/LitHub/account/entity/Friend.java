package com.sjy.LitHub.account.entity;

import com.sjy.LitHub.account.entity.authenum.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend", indexes = {
        @Index(name = "idx_requester", columnList = "requester_id"),
        @Index(name = "idx_receiver", columnList = "receiver_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester; // 친구 요청을 보낸 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 친구 요청을 받은 사람

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}