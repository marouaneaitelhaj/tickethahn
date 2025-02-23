package com.wi.tickethahn.services.inter;

import com.wi.tickethahn.entities.AuditLog;

import org.modelmapper.ModelMapper;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;

public interface AuditLogService {
    AuditLog createAuditLog(AuditLogReq auditLog);
}
