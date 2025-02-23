package com.wi.tickethahn.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.entities.AuditLog;
import com.wi.tickethahn.repositories.AuditLogRepository;
import com.wi.tickethahn.services.inter.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final ModelMapper modelMapper;
    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLog createAuditLog(@Valid AuditLogReq auditLog) {
        try {
            AuditLog auditLogEntity = modelMapper.map(auditLog, AuditLog.class);
            return auditLogRepository.save(auditLogEntity);
        } catch (Exception e) {
            log.error("Error creating audit log: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }
}
