package com.wi.tickethahn.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.wi.tickethahn.dtos.Comment.CommentReq;
import com.wi.tickethahn.dtos.Comment.CommentRes;
import com.wi.tickethahn.entities.Comment;
import com.wi.tickethahn.entities.Ticket;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.exceptions.NotFoundEx;
import com.wi.tickethahn.repositories.CommentRepository;
import com.wi.tickethahn.repositories.TicketRepository;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.CommentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final TicketRepository ticketRepository;
    
    @Override
    public CommentRes createComment(CommentReq comment) {
        User user = userRepository.findById(comment.getUser_id()).orElseThrow(() -> new NotFoundEx("User not found"));
        Ticket ticket = ticketRepository.findById(comment.getTicket_id()).orElseThrow(() -> new NotFoundEx("Ticket not found"));
        Comment commentEntity = modelMapper.map(comment, Comment.class);
        commentEntity.setUser(user);
        commentEntity.setTicket(ticket);
        return modelMapper.map(commentRepository.save(commentEntity), CommentRes.class);
    }

    @Override
    public List<CommentRes> findAll() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(comment -> modelMapper.map(comment, CommentRes.class)).toList();
    }
    
}
