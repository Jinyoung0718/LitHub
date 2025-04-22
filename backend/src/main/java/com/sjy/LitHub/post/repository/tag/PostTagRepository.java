package com.sjy.LitHub.post.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.post.entity.PostTag;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long>, PostTagRepositoryCustom {
}