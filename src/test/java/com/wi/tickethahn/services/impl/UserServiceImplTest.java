package com.wi.tickethahn.services.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.wi.tickethahn.dtos.User.UserReq;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.repositories.UserRepository;







@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserReq userReq;
    private User user;

    @BeforeEach
    void setUp() {
        userReq = new UserReq();
        userReq.setUsername("John Doe");
        userReq.setEmail("john.doe@example.com");

        user = new User();
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    void testCreateUser() {
        when(modelMapper.map(userReq, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(userReq);

        assertEquals(user.getUsername(), createdUser.getUsername());
        assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.get(0).getUsername());
        assertEquals(user.getEmail(), result.get(0).getEmail());
    }
}
