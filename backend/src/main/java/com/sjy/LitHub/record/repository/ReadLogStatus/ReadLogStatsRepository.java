package com.sjy.LitHub.record.repository.ReadLogStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.record.entity.ReadLogStats;

@Repository
public interface ReadLogStatsRepository extends JpaRepository<ReadLogStats, Long>, ReadLogStatsRepositoryCustom {

	@Modifying
	@Query("DELETE FROM ReadLogStats rls WHERE rls.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
}