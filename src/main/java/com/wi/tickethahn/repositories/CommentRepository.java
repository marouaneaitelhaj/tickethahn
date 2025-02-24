package com.wi.tickethahn.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wi.tickethahn.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    
}
