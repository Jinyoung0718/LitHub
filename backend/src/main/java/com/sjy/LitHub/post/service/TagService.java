package com.sjy.LitHub.post.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjy.LitHub.post.entity.Tag;
import com.sjy.LitHub.post.model.res.TagResponseDTO;
import com.sjy.LitHub.post.repository.tag.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagRepository tagRepository;

	@Transactional
	public List<Tag> findOrCreateTags(List<String> tagNames) {
		if (tagNames == null || tagNames.isEmpty()) return Collections.emptyList();

		Set<String> distinctNames = tagNames.stream()
			.map(String::toLowerCase)
			.collect(Collectors.toSet());

		Map<String, Tag> tags = findExistingTags(distinctNames);
		createMissingTags(distinctNames, tags);
		persistNewTags(tags.values());

		return new ArrayList<>(tags.values());
	}

	private Map<String, Tag> findExistingTags(Set<String> names) {
		return tagRepository.findAllByNameIn(names).stream()
			.collect(Collectors.toMap(Tag::getName, Function.identity()));
	}

	private void createMissingTags(Set<String> names, Map<String, Tag> tags) {
		names.stream()
			.filter(name -> !tags.containsKey(name))
			.forEach(name -> tags.put(name, Tag.of(name)));
	}

	private void persistNewTags(Collection<Tag> allTags) {
		List<Tag> newTags = allTags.stream()
			.filter(tag -> tag.getId() == null)
			.toList();

		if (!newTags.isEmpty()) {
			tagRepository.saveAll(newTags);
		}
	}

	@Transactional(readOnly = true)
	public List<TagResponseDTO> autocomplete(String keyword) {
		if (keyword == null || keyword.isBlank()) return Collections.emptyList();

		Pageable pageable = PageRequest.of(0, 10);
		return tagRepository.searchTop10ByKeyword(keyword.trim(), pageable).stream()
			.map(TagResponseDTO::from)
			.toList();
	}
}