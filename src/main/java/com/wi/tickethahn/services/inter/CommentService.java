package com.wi.tickethahn.services.inter;

import java.util.List;

import com.wi.tickethahn.dtos.Comment.CommentReq;
import com.wi.tickethahn.dtos.Comment.CommentRes;

public interface CommentService {
    CommentRes createComment(CommentReq comment);

    List<CommentRes> findAll();

}
