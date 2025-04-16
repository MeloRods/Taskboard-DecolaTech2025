package com.taskboard;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskColumnRepository extends JpaRepository<TaskColumn, Long> {
}