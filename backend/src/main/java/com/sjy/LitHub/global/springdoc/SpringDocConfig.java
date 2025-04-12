package com.sjy.LitHub.global.springdoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API 서버", version = "v1"))
@SecurityScheme(
        name = "accessToken",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "AccessToken"
)
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi groupApiAll() {
        return GroupedOpenApi.builder()
                .group("All")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupApiAuth() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupApiMyPage() {
        return GroupedOpenApi.builder()
                .group("MyPage")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupApiUserInfo() {
        return GroupedOpenApi.builder()
            .group("Token Info")
            .pathsToMatch("/api/info/**")
            .build();
    }

    @Bean
    public GroupedOpenApi groupApiFriend() {
        return GroupedOpenApi.builder()
                .group("Follow && Following")
                .pathsToMatch("/api/friends/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupApiPost() {
        return GroupedOpenApi.builder()
            .group("Post")
            .pathsToMatch("/api/posts/**")
            .build();
    }
}