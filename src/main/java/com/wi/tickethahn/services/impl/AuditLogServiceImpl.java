package com.wi.tickethahn.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.entities.AuditLog;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.repositories.AuditLogRepository;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.services.inter.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final ModelMapper modelMapper;
    private final AuditLogRepository auditLogRepository;
    private final TicketRepository ticketRepository;
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);


    @Override
    public AuditLog createAuditLog(@Valid AuditLogReq auditLog) {
        AuditLog auditLogEntity = modelMapper.map(auditLog, AuditLog.class);
        auditLogEntity.setId(null);
        logger.info("Creating audit log for ticket with id: {}, action: {}, ticket : {} ", auditLogEntity.getId(), auditLogEntity.getAction(), auditLogEntity.getTicket());
        return auditLogRepository.save(auditLogEntity);
    }

    @Override
    public List<AuditLog> getAuditLogs(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        return auditLogRepository.findByTicket(ticket);
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }
}
