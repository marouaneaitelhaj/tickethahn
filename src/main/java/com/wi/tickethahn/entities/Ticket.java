package com.wi.tickethahn.entities;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
