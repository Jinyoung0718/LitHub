package com.sjy.LitHub.record.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sjy.LitHub.record.entity.StudyGroup;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>, StudyGroupRepositoryCustom {
	boolean existsByIdAndOwnerId(Long roomId, Long ownerId);
}