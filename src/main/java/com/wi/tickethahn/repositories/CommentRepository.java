package com.wi.tickethahn.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wi.tickethahn.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTicketId(UUID ticketId);
}
