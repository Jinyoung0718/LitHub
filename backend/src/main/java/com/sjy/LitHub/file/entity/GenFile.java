package com.sjy.LitHub.file.entity;

import java.util.Objects;

import com.sjy.LitHub.file.util.FileConstant;
import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class GenFile extends BaseTime {

	private int fileNo;

	@Column(length = 10, nullable = false)
	private String fileExt;

	@Column(nullable = false)
	private String storageKey;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GenFile other)) return false;
		return this.getId() != null && this.getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getId());
	}

	// 엔티티별로 소유자 id 반환
	protected abstract long getOwnerModelId();

	// 파일이 저장될 경로 규칙을 정의
	protected abstract String buildStorageKey(long ownerId, int size);

	// 저장 키 초기화 (DB 저장 전에 호출)
	public void initStorageKey(long ownerId, int size) {
		this.storageKey = buildStorageKey(ownerId, size);
	}

	public String getPublicUrl() {
		return FileConstant.publicUrl(this.storageKey);
	}
}