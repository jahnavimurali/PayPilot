package com.paypilot.service;

import com.paypilot.model.User;
import com.paypilot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    void signup_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User signedUpUser = userService.signup(user);

        assertNotNull(signedUpUser);
        assertEquals(user.getEmail(), signedUpUser.getEmail());
    }

    @Test
    void signup_UserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> userService.signup(user));
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);

        User loggedInUser = userService.login(user.getEmail(), "password");

        assertNotNull(loggedInUser);
        assertEquals(user.getEmail(), loggedInUser.getEmail());
    }

    @Test
    void login_InvalidEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.login(user.getEmail(), "password"));
    }

    @Test
    void login_InvalidPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> userService.login(user.getEmail(), "wrong"));
    }

    @Test
    void getUserById_SuccessAndNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.getUserById(1L);
        assertNotNull(found);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(userService.getUserById(2L));
    }
}
