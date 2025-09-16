package com.sjy.LitHub.record.entity;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "study_group_participant", indexes = {
	@Index(name = "idx_sgp_user", columnList = "user_id"), // 특정 사용자가 참여한 스터디 조회 최적화
	@Index(name = "idx_sgp_group", columnList = "group_id") // 특정 스터디의 참가자 목록 조회 최적화
})
public class StudyGroupParticipant extends BaseTime {

	@ManyToOne(fetch = FetchType.LAZY)
	private StudyGroup group;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public static StudyGroupParticipant of(StudyGroup group, Long userId) {
		return StudyGroupParticipant.builder()
			.group(group)
			.user(new User(userId))
			.build();
	}
}