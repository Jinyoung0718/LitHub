package com.sjy.LitHub.record.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sjy.LitHub.account.entity.User;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "study_group", indexes = {
	@Index(name = "idx_study_group_owner", columnList = "owner_id"), // 방장 기준 스터디 조회 최적화
	@Index(name = "idx_study_group_status", columnList = "status"), // 상태별 조회 최적화
	@Index(name = "idx_study_group_status_updated", columnList = "status, updated_at") // 상태별 + 최신순 정렬 조회 최적화
})
public class StudyGroup extends BaseTime {

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 150)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Builder.Default
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyGroupParticipant> studyGroupParticipants = new ArrayList<>();

	@Column(name = "total_minutes")
	private Integer totalMinutes;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StudyGroupStatus status;

	public static StudyGroup of(String title, String content, User owner) {
		return StudyGroup.builder()
			.title(title)
			.content(content)
			.owner(owner)
			.status(StudyGroupStatus.WAITING)
			.build();
	}

	public void markAsEnded(int minutes, Set<Long> participantIds) {
		this.totalMinutes = minutes;
		this.status = StudyGroupStatus.ENDED;

		for (Long userId : participantIds) {
			this.addParticipant(StudyGroupParticipant.of(this, userId));
		}
	}

	public void markAsCanceled() {
		this.status = StudyGroupStatus.CANCELED;
	}

	public void markAsStarted() {
		this.status = StudyGroupStatus.RUNNING;
	}

	public void addParticipant(StudyGroupParticipant participant) {
		this.studyGroupParticipants.add(participant);
	}
}