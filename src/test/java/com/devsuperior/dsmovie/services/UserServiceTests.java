package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.devsuperior.dsmovie.tests.UserDetailsFactory.createCustomAdminUser;
import static com.devsuperior.dsmovie.tests.UserFactory.createUserEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil userUtil;

    private String existingUsername;
    private String nonExistingUsername;
    UserEntity foundedUser;
    List<UserDetailsProjection> userDetailsProjections;

    @BeforeEach
    void setUp() {
        existingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gmail.com";

        foundedUser = createUserEntity();

        userDetailsProjections = createCustomAdminUser(existingUsername);
    }

    @Test
    void authenticatedShouldReturnUserEntityWhenUserExists() {
        when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(foundedUser));

        when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

        UserEntity result = service.authenticated();

        assertNotNull(result);
        assertEquals(existingUsername, result.getUsername());
    }

    @Test
    void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

        assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(userDetailsProjections);

        UserDetails result = service.loadUserByUsername(existingUsername);

        assertNotNull(result);
        assertEquals(existingUsername, result.getUsername());
    }

    @Test
    void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUsername);
        });
    }
}
