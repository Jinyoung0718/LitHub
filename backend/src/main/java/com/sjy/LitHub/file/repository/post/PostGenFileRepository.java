package com.sjy.LitHub.file.repository.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.file.entity.PostGenFile;

@Repository
public interface PostGenFileRepository extends JpaRepository<PostGenFile, Long>, PostGenFileRepositoryCustom {

	// 특정 유저의 특정 storageKey면서 아직 게시글에 연결되지 않은 임시 이미지 조회
	Optional<PostGenFile> findByStorageKeyAndUserIdAndPostIsNull(String storageKey, Long userId);

	// 마크다운 본문에 사용된 storageKey 목록 기반으로 이미지 조회 (게시글 생성/수정 시 사용)
	List<PostGenFile> findAllByStorageKeyInAndUserIdAndTypeCode(
		List<String> storageKeys,
		Long userId,
		PostGenFile.TypeCode typeCode
	);
}