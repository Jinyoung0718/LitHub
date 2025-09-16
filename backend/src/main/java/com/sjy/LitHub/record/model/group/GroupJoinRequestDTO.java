package com.sjy.LitHub.record.model.group;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupJoinRequestDTO {

	@NotNull(message = "roomId는 필수입니다.")
	private Long roomId;

	@NotNull(message = "targetUserId는 필수입니다.")
	private Long targetUserId;
}