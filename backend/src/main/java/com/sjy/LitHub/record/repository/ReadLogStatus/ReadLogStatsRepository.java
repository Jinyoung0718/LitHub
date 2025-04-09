package com.sjy.LitHub.record.repository.ReadLogStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.record.entity.ReadLogStats;

@Repository
public interface ReadLogStatsRepository extends JpaRepository<ReadLogStats, Long>, ReadLogStatsRepositoryCustom {
}