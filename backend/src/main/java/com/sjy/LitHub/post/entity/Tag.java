package com.sjy.LitHub.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.sjy.LitHub.global.entity.BaseTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "tag",
	indexes = {
		@Index(name = "idx_tag_name", columnList = "name", unique = true)
	}
)
public class Tag extends BaseTime {

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	@OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PostTag> postTags = new ArrayList<>();

	public static Tag of(String name) {
		return Tag.builder().name(name).build();
	}
}