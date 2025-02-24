package com.example.entities;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.example.enums.Category;
import com.example.enums.Priority;
import com.example.enums.Status;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String id;
    private String title;
    private String description;
    private String priority;
    private String category;
    private String status;

    private User assignedTo;
    private String assignedToId;

    private User createdBy;

    List<AuditLog> auditLogs;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
