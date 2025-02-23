package com.wi.tickethahn.entity;


import java.time.LocalDateTime;

import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.enums.Status;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private User assignedTo;
    private LocalDateTime creationDate = LocalDateTime.now();
}
