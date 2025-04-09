package com.sjy.LitHub.record.repository.ReadLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjy.LitHub.record.entity.ReadLog;

@Repository
public interface ReadLogRepository extends JpaRepository<ReadLog, Long>, ReadLogRepositoryCustom {
}