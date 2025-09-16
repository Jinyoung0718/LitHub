package com.sjy.LitHub.file.util.prod;

import org.springframework.stereotype.Component;

import com.sjy.LitHub.global.config.AppConfig;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3FileUtil {

	private final S3Client s3Client;

	private String getBucket() {
		return AppConfig.getS3Bucket();
	}

	public void upload(String key, byte[] bytes, String contentType) {
		s3Client.putObject(
			PutObjectRequest.builder()
				.bucket(getBucket())
				.key(key)
				.contentType(contentType)
				.build(),
			RequestBody.fromBytes(bytes)
		);
	}

	public void delete(String key) {
		s3Client.deleteObject(DeleteObjectRequest.builder()
			.bucket(getBucket())
			.key(key)
			.build()
		);
	}

	public void copy(String sourceKey, String targetKey) {
		s3Client.copyObject(CopyObjectRequest.builder()
			.sourceBucket(getBucket())
			.sourceKey(sourceKey)
			.destinationBucket(getBucket())
			.destinationKey(targetKey)
			.build()
		);
	}
}