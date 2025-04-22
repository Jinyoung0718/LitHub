package com.sjy.LitHub.global.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FanOutMessage {
	private Long userId;
	private Long postId;
}