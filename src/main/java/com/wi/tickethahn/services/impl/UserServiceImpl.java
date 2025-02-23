package com.wi.tickethahn.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.wi.tickethahn.dtos.User.UserReq;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
        private final ModelMapper modelMapper;

    @Override
    public User createUser(UserReq user) {
        User userEntity = modelMapper.map(user, User.class);
        return userRepository.save(userEntity);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
}
