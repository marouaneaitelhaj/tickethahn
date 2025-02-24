package com.wi.tickethahn.services.inter;

import com.wi.tickethahn.entities.AuditLog;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;

public interface AuditLogService {
    AuditLog createAuditLog(AuditLogReq auditLog);
    List<AuditLog> getAuditLogs(UUID ticketId);
    List<AuditLog> findAll();
}
