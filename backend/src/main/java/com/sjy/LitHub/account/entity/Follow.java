package com.sjy.LitHub.account.entity;

import com.sjy.LitHub.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follow", uniqueConstraints = {
	@UniqueConstraint(name = "uk_follower_followee", columnNames = {"follower_id", "followee_id"}) // 중복 팔로우 방지
}, indexes = {
	@Index(name = "idx_follower", columnList = "follower_id"), // 내가 팔로우한 사람 조회
	@Index(name = "idx_followee", columnList = "followee_id")  // 나를 팔로우한 사람 조회
})
public class Follow extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "follower_id", nullable = false)
	private User follower;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followee_id", nullable = false)
	private User followee;

	public static Follow of(User follower, User followee) {
		return Follow.builder()
			.follower(follower)
			.followee(followee)
			.build();
	}
}