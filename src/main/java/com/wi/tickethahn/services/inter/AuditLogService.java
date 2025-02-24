package com.wi.tickethahn.services.inter;

import com.wi.tickethahn.entities.AuditLog;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.dtos.AuditLog.AuditLogRes;

public interface AuditLogService {
    AuditLog createAuditLog(AuditLogReq auditLog);
    List<AuditLogRes> getAuditLogs(UUID ticketId);
    List<AuditLogRes> findAll();
}
