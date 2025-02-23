package com.wi.tickethahn.dtos.Ticket;

import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReq {

    
    @NotNull(message = "Title is mandatory")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    @NotNull(message = "Category is mandatory")
    private Category category;

    private Status status;

    private UUID assignedTo_id;
}
