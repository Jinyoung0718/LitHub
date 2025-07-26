package com.sjy.LitHub.global.redis.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

	private final ProxyManager<String> proxyManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String key = httpRequest.getRemoteAddr();

		Bucket bucket = proxyManager.builder().build(key, rateLimitConfig());
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {
			chain.doFilter(request, response);

		} else {
			sendRateLimitResponse(response, probe);
		}
	}

	private Supplier<BucketConfiguration> rateLimitConfig() {
		return () -> BucketConfiguration.builder()
			.addLimit(limit -> limit
				.capacity(60)
				.refillGreedy(60, Duration.ofMinutes(1)))
			.build();
	}

	private void sendRateLimitResponse(ServletResponse response, ConsumptionProbe probe) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
		httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		httpResponse.setHeader("Retry-After",
			String.valueOf(TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())));
		httpResponse.getWriter().write("한 번에 너무 많은 요청은 허용하지 않습니다");
	}
}