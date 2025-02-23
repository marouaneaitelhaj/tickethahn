package com.wi.tickethahn.services.inter;

import java.util.List;

import com.wi.tickethahn.dtos.User.UserReq;
import com.wi.tickethahn.entities.User;

public interface UserService {
    User createUser(UserReq user);
    List<User> getAllUsers();
}
