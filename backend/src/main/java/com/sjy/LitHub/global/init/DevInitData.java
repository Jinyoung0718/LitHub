package com.sjy.LitHub.global.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Profile("dev")
@Configuration
@Slf4j
public class DevInitData {

    private static final String API_URL = "http://localhost:8080/v3/api-docs";
    private static final String OUTPUT_FILE = "apiV1.json";
    private static final String FRONTEND_SCHEMA_PATH = "../frontend/src/lib/backend/apiV1/schema.d.ts";

    @Bean
    public ApplicationRunner devApplicationRunner() {
        return args -> {
            generateApiJsonFile();
            executeCommand(List.of(
                    "cmd.exe", "/c",
                    "npx --package typescript --package openapi-typescript --package punycode " +
                            "openapi-typescript " + OUTPUT_FILE + " -o " + FRONTEND_SCHEMA_PATH
            ));
        };
    }

    private void executeCommand(List<String> command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(log::info);
            }

            int exitCode = process.waitFor();
            log.info("프로세스 종료 코드: {}", exitCode);
        } catch (Exception e) {
            log.error("명령어 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void generateApiJsonFile() {
        Path filePath = Path.of(DevInitData.OUTPUT_FILE);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DevInitData.API_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Files.writeString(filePath, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                log.info("JSON 데이터가 {}에 저장되었습니다.", filePath.toAbsolutePath());
            } else {
                log.error("오류: HTTP 상태 코드 {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("API JSON 파일 생성 중 오류 발생: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // InterruptedException 처리
        }
    }
}