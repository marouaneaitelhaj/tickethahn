package com.wi.tickethahn.services.inter;

import java.util.UUID;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.dtos.Ticket.TicketRsp;
import com.wi.tickethahn.entities.Ticket;
import java.util.List;
import com.wi.tickethahn.enums.Status;

public interface TicketService {
    TicketRsp createTicket(TicketReq ticket);
    TicketRsp updateTicket(TicketReq ticket, UUID id);
    TicketRsp updateStatus(Status status, UUID id);
    void checkIfStatusChanged(Ticket ticket, Status new_status);
    List<TicketRsp> getTicketByUser(UUID id);
    List<TicketRsp> findAll();
    TicketRsp findById(UUID id);
    List<TicketRsp> findByStatus(Status status);
    void deleteTicket(UUID id);
}
