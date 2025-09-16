package com.sjy.LitHub.account.model.res;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.lang.NonNull;

import com.sjy.LitHub.record.entity.StudyGroupStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class StudyGroupHistoryDTO {

	@NonNull
	private String title;

	@NonNull
	private String content;

	private int totalMinutes;

	@NonNull
	private LocalDateTime createdAt;

	@Builder.Default
	@NonNull
	private StudyGroupStatus status = StudyGroupStatus.ENDED;

	@NonNull
	private UserBriefDTO  owner;

	@NonNull
	private List<UserBriefDTO > participants;

	@SuppressWarnings("unused")
	public StudyGroupHistoryDTO(
		String title,
		String content,
		int totalMinutes,
		LocalDateTime createdAt,
		UserBriefDTO owner,
		List<UserBriefDTO> participants
	) {
		this.title = title;
		this.content = content;
		this.totalMinutes = totalMinutes;
		this.createdAt = createdAt;
		this.status = StudyGroupStatus.ENDED;
		this.owner = owner;
		this.participants = participants;
	}
}