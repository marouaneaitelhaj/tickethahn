package com.wi.tickethahn.dtos.Comment;

import java.util.UUID;
import lombok.AllArgsConstructor;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReq {
    @NotNull
    private UUID ticket_id;

    @NotNull
    private UUID user_id;

    @NotBlank
    private String message;
}
