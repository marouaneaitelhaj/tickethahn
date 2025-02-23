package com.wi.tickethahn.services.impl;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.enums.Action;
import com.wi.tickethahn.enums.Status;
import com.wi.tickethahn.exceptions.NotFoundEx;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.AuditLogService;
import com.wi.tickethahn.services.inter.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ModelMapper modelMapper;

    @Override
    public Ticket createTicket(@Valid TicketReq ticket) {
        Ticket ticketEntity = modelMapper.map(ticket, Ticket.class);
        Optional.ofNullable(ticket.getAssignedTo_id())
                .ifPresent(userId -> ticketEntity.setAssignedTo(userRepository.findById(userId).orElse(null)));
        ticketEntity.setStatus(Optional.ofNullable(ticket.getStatus()).orElse(Status.New));
        return ticketRepository.save(ticketEntity);
    }

    @Override
    public Ticket updateTicket(@Valid TicketReq ticket, UUID id) {
        Ticket ticketEntity = ticketRepository.findById(id).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        checkIfStatusChanged(ticketEntity, ticket.getStatus());
        modelMapper.map(ticket, ticketEntity);
        Optional.ofNullable(ticket.getAssignedTo_id())
                .ifPresent(userId -> ticketEntity.setAssignedTo(userRepository.findById(userId).orElse(null)));
        return ticketRepository.save(ticketEntity);
    }

    public void checkIfStatusChanged(Ticket ticket, Status newStatus) {
        if (ticket.getStatus() != newStatus) {
            AuditLogReq auditLog = new AuditLogReq();
            auditLog.setTicketId(ticket.getId());
            auditLog.setOldValue(ticket.getStatus().toString());
            auditLog.setNewValue(newStatus.toString());
            auditLog.setAction(Action.Status_Changed);
            auditLogService.createAuditLog(auditLog);
        }
    }
}
