package com.rohit.taskmanager.repo;

import com.rohit.taskmanager.entity.Status;
import com.rohit.taskmanager.entity.Task;
import com.rohit.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.FileChannel;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndStatus(User user, Status status, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
