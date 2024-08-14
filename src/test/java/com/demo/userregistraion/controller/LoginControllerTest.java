package com.demo.userregistraion.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.demo.userregistration.controller.LoginController;
import com.demo.userregistration.exception.LoginException;
import com.demo.userregistration.service.LoginService;
import com.demo.userregistration.vo.DeleteRequest;
import com.demo.userregistration.vo.DeleteResponse;
import com.demo.userregistration.vo.LoginRequest;
import com.demo.userregistration.vo.LoginResponse;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private DeleteRequest deleteRequest;
    private DeleteResponse deleteResponse;

    @BeforeEach
    public void setUp() {
        loginRequest = new LoginRequest();
        loginResponse = new LoginResponse();
        deleteRequest = new DeleteRequest();
        deleteResponse = new DeleteResponse();
    }

    @Test
    public void testLoginUser_Success() throws LoginException {
        loginResponse.setStatus("200");
        when(loginService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = loginController.loginUser(loginRequest);

        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().getStatus().equals("200"));
    }

    @Test
    public void testLoginUser_BadRequest() throws LoginException {
        loginResponse.setStatus("400");
        when(loginService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = loginController.loginUser(loginRequest);

        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        assert(response.getBody().getStatus().equals("400"));
    }

    @Test
    public void testLoginUser_Exception() throws LoginException {
        doThrow(new LoginException("Exception occurred while logging in the user")).when(loginService).loginUser(any(LoginRequest.class));

        try {
            loginController.loginUser(loginRequest);
        } catch (LoginException le) {
            assert(le.getMessage().equals("Exception occurred while logging in the user"));
        }
    }

    @Test
    public void testDeleteUser_Success() throws LoginException {
        when(loginService.existsByEmail(any(String.class))).thenReturn(false);
        when(loginService.deleteUser(any(DeleteRequest.class))).thenReturn(deleteResponse);

        ResponseEntity<DeleteResponse> response = loginController.deleteUser(deleteRequest);

        assert(response.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testDeleteUser_EmailNotExist() {
        when(loginService.existsByEmail(any(String.class))).thenReturn(true);

        ResponseEntity<DeleteResponse> response = loginController.deleteUser(deleteRequest);

        assert(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        assert(response.getBody().getStatus().equals("400"));
        assert(response.getBody().getMessage().equals("EmailAddress does not exist"));
    }

    @Test
    public void testDeleteUser_Exception() throws LoginException {
        when(loginService.existsByEmail(any(String.class))).thenReturn(false);
        doThrow(new LoginException("Exception occurred while deleting the user")).when(loginService).deleteUser(any(DeleteRequest.class));

        try {
            loginController.deleteUser(deleteRequest);
        } catch (LoginException le) {
            assert(le.getMessage().equals("Exception occurred while deleting the user"));
        }
    }
}