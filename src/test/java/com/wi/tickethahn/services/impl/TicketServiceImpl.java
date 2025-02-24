package com.wi.tickethahn.services.impl;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.dtos.Ticket.TicketRsp;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.enums.Action;
import com.wi.tickethahn.enums.Status;
import com.wi.tickethahn.exceptions.NotFoundEx;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.AuditLogService;
import com.wi.tickethahn.services.inter.TicketService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private UUID ticketId;
    private UUID userId;
    private Ticket ticket;
    private User user;
    private TicketReq ticketReq;
    private TicketRsp ticketRsp;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Mocked entities
        user = new User();
        user.setId(userId);
        user.setUsername("Test User");

        ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setStatus(Status.New);

        // Mocked DTOs
        ticketReq = new TicketReq();
        ticketReq.setAssignedTo_id(userId);
        ticketReq.setStatus(Status.In_Progress);

        ticketRsp = new TicketRsp();
        ticketRsp.setId(ticketId);
        ticketRsp.setStatus(Status.In_Progress);
    }

    // ------------------------------------------------------------
    // createTicket
    // ------------------------------------------------------------
    @Test
    void createTicket_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // When we map ticketReq to Ticket entity
        when(modelMapper.map(ticketReq, Ticket.class)).thenReturn(ticket);
        // When we save it
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        // Then we map the saved ticket back to a response
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        TicketRsp result = ticketService.createTicket(ticketReq);

        // Then
        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        assertEquals(Status.In_Progress, result.getStatus());
        verify(userRepository).findById(userId);
        verify(ticketRepository).save(ticket);
        verify(modelMapper).map(ticket, TicketRsp.class);
    }

    @Test
    void createTicket_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(modelMapper.map(ticketReq, Ticket.class)).thenReturn(ticket);

        // When / Then
        assertThrows(NotFoundEx.class, () -> ticketService.createTicket(ticketReq));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ------------------------------------------------------------
    // updateTicket
    // ------------------------------------------------------------
    @Test
    void updateTicket_Success() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // We also want to ensure that the "map" from request to entity is triggered
        // For simplicity, let's just stub it with no changes to the 'ticket' object
        doAnswer(invocation -> {
            // invocation.getArgument(0) is ticketReq
            // invocation.getArgument(1) is the target entity (ticket)
            return null;
        }).when(modelMapper).map(eq(ticketReq), eq(ticket));

        // When
        TicketRsp result = ticketService.updateTicket(ticketReq, ticketId);

        // Then
        assertNotNull(result);
        assertEquals(Status.In_Progress, result.getStatus());
        verify(ticketRepository).findById(ticketId);
        verify(modelMapper).map(ticket, TicketRsp.class);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicket_TicketNotFound() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(NotFoundEx.class, () -> ticketService.updateTicket(ticketReq, ticketId));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ------------------------------------------------------------
    // checkIfStatusChanged
    // ------------------------------------------------------------
    @Test
    void checkIfStatusChanged_StatusIsDifferent_ShouldCreateAuditLog() {
        // ticket's current status is New
        // We'll check if we pass In_Progress, it triggers an audit log
        ticketService.checkIfStatusChanged(ticket, Status.In_Progress);

        // Capture the AuditLogReq
        ArgumentCaptor<AuditLogReq> captor = ArgumentCaptor.forClass(AuditLogReq.class);
        verify(auditLogService).createAuditLog(captor.capture());

        AuditLogReq capturedLog = captor.getValue();
        assertEquals(ticketId, capturedLog.getTicketId());
        assertEquals(Status.New.toString(), capturedLog.getOldValue());
        assertEquals(Status.In_Progress.toString(), capturedLog.getNewValue());
        assertEquals(Action.Status_Changed, capturedLog.getAction());
    }

    @Test
    void checkIfStatusChanged_StatusIsSame_ShouldNotCreateAuditLog() {
        // ticket's status is New, we pass in New again
        ticketService.checkIfStatusChanged(ticket, Status.New);
        verify(auditLogService, never()).createAuditLog(any(AuditLogReq.class));
    }

    // ------------------------------------------------------------
    // getTicketByUser
    // ------------------------------------------------------------
    @Test
    void getTicketByUser_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);

        when(ticketRepository.findByAssignedTo_id(userId)).thenReturn(tickets);
        // Convert each Ticket to TicketRsp
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        List<TicketRsp> result = ticketService.getTicketByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketId, result.get(0).getId());
        verify(userRepository).findById(userId);
        verify(ticketRepository).findByAssignedTo_id(userId);
    }

    @Test
    void getTicketByUser_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundEx.class, () -> ticketService.getTicketByUser(userId));
        verify(ticketRepository, never()).findByAssignedTo_id(any(UUID.class));
    }

    // ------------------------------------------------------------
    // findAll
    // ------------------------------------------------------------
    @Test
    void findAll_Success() {
        // Given
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        List<TicketRsp> result = ticketService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketRsp, result.get(0));
        verify(ticketRepository).findAll();
    }

    // ------------------------------------------------------------
    // findById
    // ------------------------------------------------------------
    @Test
    void findById_Success() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        TicketRsp result = ticketService.findById(ticketId);

        // Then
        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void findById_NotFound() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundEx.class, () -> ticketService.findById(ticketId));
    }

    // ------------------------------------------------------------
    // findByStatus
    // ------------------------------------------------------------
    @Test
    void findByStatus_Success() {
        // Given
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        when(ticketRepository.findByStatus(Status.New)).thenReturn(tickets);
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        List<TicketRsp> result = ticketService.findByStatus(Status.New);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketRsp, result.get(0));
        verify(ticketRepository).findByStatus(Status.New);
    }

    // ------------------------------------------------------------
    // updateStatus
    // ------------------------------------------------------------
    @Test
    void updateStatus_Success() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(modelMapper.map(ticket, TicketRsp.class)).thenReturn(ticketRsp);

        // When
        TicketRsp result = ticketService.updateStatus(Status.Resolved, ticketId);

        // Then
        assertNotNull(result);
        assertEquals(Status.Resolved, result.getStatus());
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateStatus_TicketNotFound() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundEx.class, () -> ticketService.updateStatus(Status.Resolved, ticketId));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ------------------------------------------------------------
    // deleteTicket
    // ------------------------------------------------------------
    @Test
    void deleteTicket_Success() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        // When
        ticketService.deleteTicket(ticketId);

        // Then
        verify(ticketRepository).delete(ticket);
    }

    @Test
    void deleteTicket_NotFound() {
        // Given
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundEx.class, () -> ticketService.deleteTicket(ticketId));
        verify(ticketRepository, never()).delete(any(Ticket.class));
    }
}
