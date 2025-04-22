package com.sjy.LitHub.global.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackNotifier {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${slack.webhook.url}")
	private String webhookUrl;

	public void sendFanOutFailure(Long userId, Long postId) {
		Map<String, Object> blockPayload = Map.of(
			"blocks", List.of(
				Map.of(
					"type", "header",
					"text", Map.of(
						"type", "plain_text",
						"text", "FanOut 실패 발생",
						"emoji", true
					)
				),
				Map.of(
					"type", "section",
					"fields", List.of(
						Map.of(
							"type", "mrkdwn",
							"text", "*작성자 ID:*\n" + userId
						),
						Map.of(
							"type", "mrkdwn",
							"text", "*게시글 ID:*\n" + postId
						)
					)
				),
				Map.of(
					"type", "context",
					"elements", List.of(
						Map.of(
							"type", "plain_text",
							"text", "DLQ에 저장됨 - 빠른 확인 바랍니다.",
							"emoji", true
						)
					)
				)
			)
		);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(blockPayload, headers);
		try {
			restTemplate.postForEntity(webhookUrl, request, String.class);
		} catch (Exception e) {
			log.error("[SlackNotifier] Slack 전송 실패: {}", e.getMessage(), e);
		}
	}
}