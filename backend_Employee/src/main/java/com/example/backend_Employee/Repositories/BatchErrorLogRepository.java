package com.example.backend_Employee.Repositories;


import com.example.backend_Employee.Entities.BatchErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchErrorLogRepository extends JpaRepository<BatchErrorLog, Long> {

    List<BatchErrorLog> findByFileName(String fileName);
}
