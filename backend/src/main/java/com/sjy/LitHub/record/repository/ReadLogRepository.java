package com.sjy.LitHub.record.repository;

import com.sjy.LitHub.record.ReadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadLogRepository extends JpaRepository<ReadLog, Long>, ReadLogRepositoryCustom {}