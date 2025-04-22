package com.sjy.LitHub.file.repository.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.file.entity.PostGenFile;

@Repository
public interface PostGenFileRepository extends JpaRepository<PostGenFile, Long>, PostGenFileRepositoryCustom {

	// 특정 유저의 특정 파일명이면서 아직 게시글에 연결되지 않은 임시 이미지 조회 (주로 마크다운 개별 삭제에 사용)
	Optional<PostGenFile> findByFileNameAndUserIdAndPostIsNull(String fileName, Long userId);

	// 마크다운 본문에 사용된 파일명 목록 기반으로 이미지들 조회 (게시글 생성 시 used 이미지 연결에 사용)
	List<PostGenFile> findAllByFileNameInAndUserIdAndTypeCode(List<String> fileNames, Long userId, PostGenFile.TypeCode typeCode);

	// 특정 유저의 특정 파일명을 조회하면서 타입까지 제한하는 메서드 (현재 사용 안 하면 제거 가능)
	Optional<PostGenFile> findByFileNameAndUserIdAndTypeCode(String fileName, Long userId, PostGenFile.TypeCode typeCode);
}