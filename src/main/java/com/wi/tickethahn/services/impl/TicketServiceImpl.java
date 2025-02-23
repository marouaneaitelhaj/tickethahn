package com.wi.tickethahn.services.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.enums.Status;
import com.wi.tickethahn.exceptions.NotFoundEx;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.TicketService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Ticket createTicket(TicketReq ticket) {
        Ticket ticketEntity = modelMapper.map(ticket, Ticket.class);
        UUID userId = ticket.getAssignedTo_id();
        if (userId != null) {
            ticketEntity.setAssignedTo(userRepository.findById(userId).orElse(null));
        }
        if (ticket.getStatus() == null) {
            ticketEntity.setStatus(Status.New);
        } else {
            ticketEntity.setStatus(ticket.getStatus());
        }
        return ticketRepository.save(ticketEntity);
    }

    @Override
    public Ticket updateTicket(TicketReq ticket, UUID id) {
        Ticket ticketEntity = ticketRepository.findById(id).orElse(null);
        if (ticketEntity == null) {
            throw new NotFoundEx("Ticket not found");
        }
        modelMapper.map(ticket, ticketEntity);
        UUID userId = ticket.getAssignedTo_id();
        if (userId != null) {
            ticketEntity.setAssignedTo(userRepository.findById(userId).orElse(null));
        }
        return ticketRepository.save(ticketEntity);
    }
}
