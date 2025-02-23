package com.wi.tickethahn.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.entities.AuditLog;
import com.wi.tickethahn.repositories.AuditLogRepository;
import com.wi.tickethahn.services.inter.AuditLogService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final ModelMapper modelMapper;
    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLog createAuditLog(AuditLogReq auditLog) {
        AuditLog auditLogEntity = modelMapper.map(auditLog, AuditLog.class);
        return auditLogRepository.save(auditLogEntity);
    }
    
}
