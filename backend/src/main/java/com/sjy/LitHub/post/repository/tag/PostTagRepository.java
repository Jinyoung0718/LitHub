package com.sjy.LitHub.post.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjy.LitHub.post.entity.PostTag;

public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagRepositoryCustom {
}