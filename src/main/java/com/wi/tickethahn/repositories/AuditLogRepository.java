package com.wi.tickethahn.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.wi.tickethahn.entities.Ticket;

import com.wi.tickethahn.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByTicket(Ticket ticket);
}