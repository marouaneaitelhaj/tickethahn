package com.wi.tickethahn.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.dtos.Ticket.TicketRsp;
import com.wi.tickethahn.dtos.Ticket.TicketStatusUpdateRequest;

import java.util.List;
import java.util.UUID;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.enums.Status;
import com.wi.tickethahn.services.inter.TicketService;
import jakarta.validation.Valid;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {


    private final TicketService ticketService;


    @PostMapping
    public ResponseEntity<TicketRsp> createTicket(@Valid @RequestBody TicketReq ticketreq) {
        TicketRsp ticket = ticketService.createTicket(ticketreq);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping
    public ResponseEntity<List<TicketRsp>> getAllTickets() {
        List<TicketRsp> tickets = ticketService.findAll();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketRsp> getTicketById(@PathVariable UUID id) {
        TicketRsp ticket = ticketService.findById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketRsp> updateTicket(@Valid @RequestBody TicketReq ticketreq, @PathVariable UUID id) {
        TicketRsp ticket = ticketService.updateTicket(ticketreq, id);
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketRsp>> getMyTickets() {
        List<TicketRsp> tickets = ticketService.findAll();
        return ResponseEntity.ok(tickets);
    }


    @PutMapping("/change-status")
    public ResponseEntity<TicketRsp> changeStatus(@RequestBody TicketStatusUpdateRequest ticketStatusUpdateRequest) {
        TicketRsp ticket = ticketService.updateStatus(ticketStatusUpdateRequest.getStatus(), ticketStatusUpdateRequest.getId());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketRsp>> getTicketsByStatus(@PathVariable Status status) {
        List<TicketRsp> tickets = ticketService.findByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    
}
