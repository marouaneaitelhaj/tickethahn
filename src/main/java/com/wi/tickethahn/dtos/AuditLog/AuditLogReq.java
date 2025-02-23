package com.wi.tickethahn.dtos.AuditLog;

import java.util.Date;
import java.util.UUID;

import com.wi.tickethahn.enums.Action;

import lombok.Data;


@Data
public class AuditLogReq {
    private UUID ticketId;
    private Action action;
    private String oldValue;
    private String newValue;
    private Date timestamp = new Date();
}
