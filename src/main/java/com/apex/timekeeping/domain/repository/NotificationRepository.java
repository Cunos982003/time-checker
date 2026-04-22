package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}
