package com.wi.tickethahn.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wi.tickethahn.dtos.AuditLog.AuditLogRes;
import com.wi.tickethahn.services.inter.AuditLogService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;


    @GetMapping("/{ticketId}")
    public ResponseEntity<List<AuditLogRes>> getAuditLogs(@PathVariable UUID ticketId) {
        List<AuditLogRes> auditLogs = auditLogService.getAuditLogs(ticketId);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping
    public ResponseEntity<List<AuditLogRes>> getAllAuditLogs() {
        List<AuditLogRes> auditLogs = auditLogService.findAll();
        return ResponseEntity.ok(auditLogs);
    }
}
