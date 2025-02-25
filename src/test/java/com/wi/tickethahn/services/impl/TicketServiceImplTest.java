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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

        // Mocked DTO for request
        ticketReq = new TicketReq();
        ticketReq.setAssignedTo_id(userId);
        ticketReq.setStatus(Status.In_Progress);

        // Mocked response DTO
        ticketRsp = new TicketRsp();
        ticketRsp.setId(ticketId);
        ticketRsp.setStatus(Status.In_Progress);
    }

    // ---------------------- createTicket ----------------------
    @Test
    void createTicket_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(ticketReq, Ticket.class)).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        TicketRsp result = ticketService.createTicket(ticketReq);

        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        assertEquals(Status.In_Progress, result.getStatus());
        verify(userRepository).findById(userId);
        verify(ticketRepository).save(ticket);
        verify(modelMapper).map(ticket, TicketRsp.class);
    }

    @Test
    void createTicket_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(modelMapper.map(ticketReq, Ticket.class)).thenReturn(ticket);

        assertThrows(NotFoundEx.class, () -> ticketService.createTicket(ticketReq));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ---------------------- updateTicket ----------------------
    @Test
    void updateTicket_Success() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Stub the mapping from ticketReq into ticket to simulate updating the status:
        doAnswer(invocation -> {
            TicketReq req = invocation.getArgument(0);
            Ticket target = invocation.getArgument(1);
            target.setStatus(req.getStatus());
            return null;
        }).when(modelMapper).map(eq(ticketReq), eq(ticket));
        // Simulate that save returns the updated ticket
        ticket.setStatus(Status.In_Progress); // updated state
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        TicketRsp result = ticketService.updateTicket(ticketReq, ticketId);

        assertNotNull(result);
        assertEquals(Status.In_Progress, result.getStatus());
        verify(ticketRepository).findById(ticketId);
        verify(modelMapper).map(ticket, TicketRsp.class);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicket_TicketNotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        assertThrows(NotFoundEx.class, () -> ticketService.updateTicket(ticketReq, ticketId));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ---------------------- checkIfStatusChanged ----------------------
    @Test
    void checkIfStatusChanged_StatusIsDifferent_ShouldCreateAuditLog() {
        ticket.setStatus(Status.New);
        ticketService.checkIfStatusChanged(ticket, Status.In_Progress);

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
        ticket.setStatus(Status.New);
        ticketService.checkIfStatusChanged(ticket, Status.New);
        verify(auditLogService, never()).createAuditLog(any(AuditLogReq.class));
    }

    // ---------------------- getTicketByUser ----------------------
    @Test
    void getTicketByUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        when(ticketRepository.findByAssignedTo_id(userId)).thenReturn(tickets);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        List<TicketRsp> result = ticketService.getTicketByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketId, result.get(0).getId());
        verify(userRepository).findById(userId);
        verify(ticketRepository).findByAssignedTo_id(userId);
    }

    @Test
    void getTicketByUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundEx.class, () -> ticketService.getTicketByUser(userId));
        verify(ticketRepository, never()).findByAssignedTo_id(any(UUID.class));
    }

    // ---------------------- findAll ----------------------
    @Test
    void findAll_Success() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        List<TicketRsp> result = ticketService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketRsp, result.get(0));
        verify(ticketRepository).findAll();
    }

    // ---------------------- findById ----------------------
    @Test
    void findById_Success() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        TicketRsp result = ticketService.findById(ticketId);

        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        verify(ticketRepository).findById(ticketId);
    }

    @Test
    void findById_NotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        assertThrows(NotFoundEx.class, () -> ticketService.findById(ticketId));
    }

    // ---------------------- findByStatus ----------------------
    @Test
    void findByStatus_Success() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        when(ticketRepository.findByStatus(Status.New)).thenReturn(tickets);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);

        List<TicketRsp> result = ticketService.findByStatus(Status.New);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketRsp, result.get(0));
        verify(ticketRepository).findByStatus(Status.New);
    }

    // ---------------------- updateStatus ----------------------
    @Test
    void updateStatus_Success() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        // Simulate that after updating, the ticket has status Resolved.
        ticket.setStatus(Status.Resolved);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        doReturn(ticketRsp).when(modelMapper).map(ticket, TicketRsp.class);
        ticketRsp.setStatus(Status.Resolved);

        TicketRsp result = ticketService.updateStatus(Status.Resolved, ticketId);

        assertNotNull(result);
        assertEquals(Status.Resolved, result.getStatus());
        verify(ticketRepository).findById(ticketId);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateStatus_TicketNotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        assertThrows(NotFoundEx.class, () -> ticketService.updateStatus(Status.Resolved, ticketId));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    // ---------------------- deleteTicket ----------------------
    @Test
    void deleteTicket_Success() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        ticketService.deleteTicket(ticketId);
        verify(ticketRepository).delete(ticket);
    }

    @Test
    void deleteTicket_NotFound() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());
        assertThrows(NotFoundEx.class, () -> ticketService.deleteTicket(ticketId));
        verify(ticketRepository, never()).delete(any(Ticket.class));
    }
}
