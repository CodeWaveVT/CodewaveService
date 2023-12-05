package edu.vt.codewaveservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.google.gson.Gson;
import edu.vt.codewaveservice.common.ResultUtils;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.model.dto.UserResetRequest;
import edu.vt.codewaveservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;


class UserControllerTest {
    private MockMvc mockMvc;
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }
    @Test
    void userRegister() throws Exception {
        UserRegisterRequest mockRequest = new UserRegisterRequest();
        mockRequest.setUserAccount("test@123.com");
        mockRequest.setUserPassword("test");
        mockRequest.setCheckPassword("test");
        mockRequest.setValidateCode("1234");

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("test@123.com", "1234");

        when(userService.userRegister(eq(mockRequest), eq(mockSession)))
                .thenReturn(ResultUtils.success("register success"));

        Gson gson = new Gson();

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession)
                        .content(gson.toJson(mockRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).userRegister(any(UserRegisterRequest.class), any(HttpSession.class));
    }

    @Test
    void userLogin() throws Exception {
        UserLoginRequest mockLoginRequest = new UserLoginRequest();
        mockLoginRequest.setUserAccount("test@123.com");
        mockLoginRequest.setUserPassword("test");

        when(userService.doLogin(eq(mockLoginRequest), any(HttpServletRequest.class)))
                .thenReturn(ResultUtils.success("login success"));

        Gson gson = new Gson();

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(mockLoginRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).doLogin(any(UserLoginRequest.class), any(HttpServletRequest.class));
    }


    @Test
    void userLogout() throws Exception {
        when(userService.doLogout(any(HttpServletRequest.class)))
                .thenReturn(ResultUtils.success("logout success"));

        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isOk());

        verify(userService, times(1)).doLogout(any(HttpServletRequest.class));
    }


    @Test
    void resetPassword() throws Exception {
        UserResetRequest mockResetRequest = new UserResetRequest();
        mockResetRequest.setUserAccount("test@123.com");
        mockResetRequest.setUserPassword("newPassword");
        mockResetRequest.setCheckPassword("newPassword");
        mockResetRequest.setValidateCode("1234");

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("test@123.com", "1234");

        when(userService.forgetPassword(eq(mockResetRequest), eq(mockSession)))
                .thenReturn(ResultUtils.success("password reset success"));

        Gson gson = new Gson();

        mockMvc.perform(post("/user/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession)
                        .content(gson.toJson(mockResetRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).forgetPassword(any(UserResetRequest.class), any(HttpSession.class));
    }

}