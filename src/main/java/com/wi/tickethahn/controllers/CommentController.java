package com.wi.tickethahn.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wi.tickethahn.dtos.Comment.CommentReq;
import com.wi.tickethahn.dtos.Comment.CommentRes;
import com.wi.tickethahn.services.inter.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);


    @PostMapping
    // @PreAuthorize("hasRole('ROLE_IT_Support')")
    public ResponseEntity<CommentRes> createComment(@Valid @RequestBody CommentReq comment) {
        CommentRes commentRes = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentRes);
    }

    @GetMapping
    public ResponseEntity<List<CommentRes>> getAllComments() {
        logger.info("Fetching all comments");
        List<CommentRes> commentRes = commentService.findAll();
        return ResponseEntity.ok(commentRes);
    }
}
