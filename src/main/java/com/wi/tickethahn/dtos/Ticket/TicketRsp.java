package com.wi.tickethahn.dtos.Ticket;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


import com.wi.tickethahn.dtos.AuditLog.AuditLogReq;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.enums.Status;
import lombok.Data;

@Data
public class TicketRsp {
private UUID id;
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;

    private User assignedTo;

    private User createdBy;

    List<AuditLogReq> auditLogs;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
