package com.wi.tickethahn.dtos.Comment;

import java.util.UUID;
import lombok.AllArgsConstructor;

import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRes {
    private UUID id;

    private Ticket ticket;

    private User user;

    private String message;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
