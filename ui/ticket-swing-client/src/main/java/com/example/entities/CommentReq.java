package com.example.entities;



import lombok.Data;

@Data
public class CommentReq {
    private String ticket_id;

    private String user_id;

    private String message;
}