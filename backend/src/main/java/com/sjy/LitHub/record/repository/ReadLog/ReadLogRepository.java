package com.sjy.LitHub.record.repository.ReadLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.record.entity.ReadLog;

@Repository
public interface ReadLogRepository extends JpaRepository<ReadLog, Long>, ReadLogRepositoryCustom {

	@Modifying
	@Query("DELETE FROM ReadLog rl WHERE rl.user.id = :userId")
	void deleteByUserId(@Param("userId") Long userId);
}