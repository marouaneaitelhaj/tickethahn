package com.wi.tickethahn.services.impl;

import java.util.Optional;
import java.util.UUID;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.dtos.Ticket.TicketRsp;
import com.wi.tickethahn.dtos.User.UserReq;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.enums.Action;
import com.wi.tickethahn.enums.Status;
import com.wi.tickethahn.exceptions.NotFoundEx;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.AuditLogService;
import java.util.List;
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
    public TicketRsp createTicket(TicketReq ticket) {
        Ticket ticketEntity = modelMapper.map(ticket, Ticket.class);
        Optional.ofNullable(ticket.getAssignedTo_id()).ifPresent(userId -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundEx("User not found"));
            ticketEntity.setAssignedTo(user);
        });
        ticketEntity.setStatus(Optional.ofNullable(ticket.getStatus()).orElse(Status.New));
        Ticket savedTicket = ticketRepository.save(ticketEntity);
        return modelMapper.map(savedTicket, TicketRsp.class);
    }

    @Override
    public TicketRsp updateTicket(TicketReq ticket, UUID id) {
        Ticket ticketEntity = ticketRepository.findById(id).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        checkIfStatusChanged(ticketEntity, ticket.getStatus());
        modelMapper.map(ticket, ticketEntity);
        Optional.ofNullable(ticket.getAssignedTo_id()).ifPresent(userId -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundEx("User not found"));
            ticketEntity.setAssignedTo(user);
        });
        ticketEntity.setStatus(Optional.ofNullable(ticket.getStatus()).orElse(Status.New));
        Ticket savedTicket = ticketRepository.save(ticketEntity);
        return modelMapper.map(savedTicket, TicketRsp.class);
    }

    
    @Override
    public void checkIfStatusChanged(Ticket ticket, Status newStatus) {
        if (newStatus != null && ticket.getStatus() != newStatus) {
            AuditLogReq auditLog = new AuditLogReq();
            auditLog.setTicketId(ticket.getId());
            auditLog.setOldValue(ticket.getStatus().toString());
            auditLog.setNewValue(newStatus.toString());
            auditLog.setAction(Action.Status_Changed);
            auditLogService.createAuditLog(auditLog);
        }
    }

    @Override
    public List<TicketRsp> getTicketByUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundEx("User not found"));
        List<Ticket> list = ticketRepository.findByAssignedTo_id(user.getId());
        return list.stream().map(ticket -> modelMapper.map(ticket, TicketRsp.class)).toList();
    }

    @Override
    public List<TicketRsp> findAll() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream().map(ticket -> modelMapper.map(ticket, TicketRsp.class)).toList();
    }

    @Override
    public TicketRsp findById(UUID id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        return modelMapper.map(ticket, TicketRsp.class);
    }

    @Override
    public List<TicketRsp> findByStatus(Status status) {
        List<Ticket> list = ticketRepository.findByStatus(status);
        return list.stream().map(ticket -> modelMapper.map(ticket, TicketRsp.class)).toList();
    }

    @Override
    public TicketRsp updateStatus(Status status, UUID id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        this.checkIfStatusChanged(ticket, status);
        ticket.setStatus(status);
        ticket = ticketRepository.save(ticket);
        return modelMapper.map(ticket, TicketRsp.class);
    }

    @Override
    public void deleteTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        ticketRepository.delete(ticket);
    }

    @Override
    public List<TicketRsp> findByUser(UserReq user) {
        User userEntity = modelMapper.map(user, User.class);
        List<Ticket> list = ticketRepository.findByAssignedTo(userEntity);
        return list.stream().map(ticket -> modelMapper.map(ticket, TicketRsp.class)).toList();
    }
}
