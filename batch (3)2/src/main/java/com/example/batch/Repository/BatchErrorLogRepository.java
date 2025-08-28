package com.example.batch.Repository;

import com.example.batch.Entities.BatchErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchErrorLogRepository extends JpaRepository<BatchErrorLog, Long> {
}
