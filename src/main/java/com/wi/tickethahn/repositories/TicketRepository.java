package com.wi.tickethahn.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.enums.Status;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List <Ticket> findByAssignedTo_id(UUID id);
    List <Ticket> findByStatus(Status status);
}
