package com.wi.tickethahn.services.impl;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.dtos.AuditLog.AuditLogRes;
import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.AuditLog;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.enums.Action;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.repositories.AuditLogRepository;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.services.inter.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLogReq auditLogReq;
    private AuditLog auditLog;
    private AuditLogRes auditLogRes;
    private Ticket ticket;
    private TicketReq ticketReq;
    private UUID ticketId;
    private UUID auditLogId;

    @BeforeEach
    void setUp() {
        auditLogReq = new AuditLogReq();
        auditLogReq.setTicketId(UUID.randomUUID());
        auditLogReq.setAction(Action.Status_Changed);
        auditLogReq.setOldValue("OLD");
        auditLogReq.setNewValue("NEW");

        auditLogId = UUID.randomUUID();
        ticketId = UUID.randomUUID();

        ticket = new Ticket();
        ticket.setId(ticketId);

        auditLog = new AuditLog();
        auditLog.setId(auditLogId);
        auditLog.setAction(auditLogReq.getAction());
        auditLog.setOldValue(auditLogReq.getOldValue());
        auditLog.setNewValue(auditLogReq.getNewValue());
        auditLog.setTicket(ticket);

        auditLogRes = new AuditLogRes();
        auditLogRes.setId(auditLogId);
        auditLogRes.setAction(auditLog.getAction());
        auditLogRes.setOldValue(auditLog.getOldValue());
        auditLogRes.setNewValue(auditLog.getNewValue());
        auditLogRes.setTicket(ticketReq);

        ticketReq = new TicketReq();
        ticketReq.setAssignedTo_id(auditLogId);
        ticketReq.setDescription("Description");
        ticketReq.setPriority(Priority.HIGH);
    }

    // ------------------------------------------------------------
    // createAuditLog
    // ------------------------------------------------------------
    @Test
    void createAuditLog_Success() {
        // Given
        // We'll mock the mapping from AuditLogReq -> AuditLog
        when(modelMapper.map(auditLogReq, AuditLog.class)).thenReturn(auditLog);
        // The ID is reset in the service, so let's simulate that effect
        auditLog.setId(null);

        // Mock the repository save
        // Suppose we "save" the auditLog and it returns a new object with the ID set
        AuditLog savedLog = new AuditLog();
        savedLog.setId(auditLogId);
        savedLog.setAction(auditLogReq.getAction());
        savedLog.setOldValue(auditLogReq.getOldValue());
        savedLog.setNewValue(auditLogReq.getNewValue());
        savedLog.setTicket(ticket);

        when(auditLogRepository.save(auditLog)).thenReturn(savedLog);

        // When
        AuditLog result = auditLogService.createAuditLog(auditLogReq);

        // Then
        assertNotNull(result);
        assertEquals(auditLogId, result.getId());
        assertEquals(auditLogReq.getAction(), result.getAction());
        verify(modelMapper).map(auditLogReq, AuditLog.class);
        verify(auditLogRepository).save(auditLog);
    }

    // ------------------------------------------------------------
    // getAuditLogs
    // ------------------------------------------------------------
    @Test
    void getAuditLogs_Success() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        List<AuditLog> auditLogs = new ArrayList<>();
        auditLogs.add(auditLog);

        when(auditLogRepository.findByTicket(ticket)).thenReturn(auditLogs);

        // Mock modelMapper for each entity -> response
        when(modelMapper.map(auditLog, AuditLogRes.class)).thenReturn(auditLogRes);

        // When
        List<AuditLogRes> result = auditLogService.getAuditLogs(ticketId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(auditLogId, result.get(0).getId());
        verify(ticketRepository).findById(ticketId);
        verify(auditLogRepository).findByTicket(ticket);
    }

    @Test
    void getAuditLogs_TicketNotFound() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> auditLogService.getAuditLogs(ticketId));
        verify(auditLogRepository, never()).findByTicket(any(Ticket.class));
    }

    // ------------------------------------------------------------
    // findAll
    // ------------------------------------------------------------
    @Test
    void findAll_Success() {
        // Given
        List<AuditLog> auditLogs = new ArrayList<>();
        auditLogs.add(auditLog);

        when(auditLogRepository.findAll()).thenReturn(auditLogs);
        when(modelMapper.map(auditLog, AuditLogRes.class)).thenReturn(auditLogRes);

        // When
        List<AuditLogRes> result = auditLogService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(auditLogRes, result.get(0));
        verify(auditLogRepository).findAll();
        // (Optional) Check if modelMapper is called for each item
    }
}
