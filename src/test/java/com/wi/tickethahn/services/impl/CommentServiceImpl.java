package com.wi.tickethahn.services.impl;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentReq commentReq;
    private Comment commentEntity;
    private Comment savedCommentEntity;
    private CommentRes commentRes;
    private User user;
    private Ticket ticket;
    private UUID userId;
    private UUID ticketId;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        // Set up the request
        commentReq = new CommentReq();
        commentReq.setUser_id(userId);
        commentReq.setTicket_id(ticketId);
        commentReq.setMessage("Sample comment");

        // Set up the entities
        user = new User();
        user.setId(userId);
        user.setUsername("User Name");

        ticket = new Ticket();
        ticket.setId(ticketId);

        commentEntity = new Comment();
        // Typically, you'd set ID to null or not set it at all before saving
        // but let's just show how we might do it. The service sets ID on the entity if needed.
        commentEntity.setMessage(commentReq.getMessage());

        savedCommentEntity = new Comment();
        savedCommentEntity.setId(commentId);
        savedCommentEntity.setMessage(commentReq.getMessage());
        savedCommentEntity.setUser(user);
        savedCommentEntity.setTicket(ticket);

        // Set up the response
        commentRes = new CommentRes();
        commentRes.setId(commentId);
        commentRes.setMessage(commentReq.getMessage());
        // commentRes.se(userId);
        // commentRes.setTicket(ticketId);
    }

    // ------------------------------------------------------------
    // createComment (Success)
    // ------------------------------------------------------------
    @Test
    void createComment_Success() {
        // Given
        when(modelMapper.map(commentReq, Comment.class)).thenReturn(commentEntity);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentRepository.save(commentEntity)).thenReturn(savedCommentEntity);
        when(modelMapper.map(savedCommentEntity, CommentRes.class)).thenReturn(commentRes);

        // When
        CommentRes result = commentService.createComment(commentReq);

        // Then
        assertNotNull(result);
        assertEquals(commentId, result.getId());
        assertEquals(commentReq.getMessage(), result.getMessage());
        verify(commentRepository).save(commentEntity);
    }

    // ------------------------------------------------------------
    // createComment (User Not Found)
    // ------------------------------------------------------------
    @Test
    void createComment_UserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(NotFoundEx.class, () -> commentService.createComment(commentReq));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ------------------------------------------------------------
    // createComment (Ticket Not Found)
    // ------------------------------------------------------------
    @Test
    void createComment_TicketNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(NotFoundEx.class, () -> commentService.createComment(commentReq));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
