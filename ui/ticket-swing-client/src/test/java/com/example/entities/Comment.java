package com.example.entities;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private UUID id;

    private Ticket ticket;

    private User user;

    private String message;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
