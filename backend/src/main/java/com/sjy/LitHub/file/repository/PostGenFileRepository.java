package com.sjy.LitHub.file.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjy.LitHub.file.entity.PostGenFile;

public interface PostGenFileRepository extends JpaRepository<PostGenFile, Long>, PostGenFileRepositoryCustom {

	Optional<PostGenFile> findByFileNameAndUserIdAndPostIsNull(String fileName, Long userId);

	List<PostGenFile> findByPostIdAndTypeCode(Long postId, PostGenFile.TypeCode typeCode);

	List<PostGenFile> findAllByFileNameInAndUserIdAndTypeCode(List<String> fileNames, Long userId, PostGenFile.TypeCode typeCode);
}