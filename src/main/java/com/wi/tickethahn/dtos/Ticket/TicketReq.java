package com.wi.tickethahn.dtos.Ticket;


import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;

import lombok.Data;

@Data
public class TicketReq {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private String assignedTo_id;
}
