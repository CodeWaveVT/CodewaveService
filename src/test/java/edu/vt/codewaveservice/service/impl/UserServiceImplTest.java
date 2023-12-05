package edu.vt.codewaveservice.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.vt.codewaveservice.common.BaseResponse;
import edu.vt.codewaveservice.mapper.UserMapper;
import edu.vt.codewaveservice.model.entity.User;
import edu.vt.codewaveservice.model.dto.UserLoginRequest;
import edu.vt.codewaveservice.model.dto.UserRegisterRequest;
import edu.vt.codewaveservice.model.dto.UserResetRequest;
import edu.vt.codewaveservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userRegisterTest() {
        UserRegisterRequest mockRequest = new UserRegisterRequest();
        mockRequest.setUserAccount("test@example.com");
        mockRequest.setUserPassword("password");
        mockRequest.setCheckPassword("password");
        mockRequest.setValidateCode("1234");

        HttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("test@example.com", "1234");

        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        BaseResponse response = userService.userRegister(mockRequest, mockSession);

        assertNotNull(response);
        assertEquals("register success", response.getData());
        verify(userMapper, times(1)).insert(any(User.class));
    }


    @Test
    void forgetPasswordTest() {
        UserResetRequest mockRequest = new UserResetRequest();
        mockRequest.setUserAccount("test@example.com");
        mockRequest.setUserPassword("newPassword");
        mockRequest.setCheckPassword("newPassword");
        mockRequest.setValidateCode("1234");

        HttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("test@example.com", "1234");

        when(userMapper.selectCount(any())).thenReturn(1L); // 假设用户存在
        when(userMapper.update(any(), any())).thenReturn(1); // 假设更新操作成功

        BaseResponse response = userService.forgetPassword(mockRequest, mockSession);

        assertNotNull(response);
        assertEquals("reset password success", response.getData());
        verify(userMapper, times(1)).update(any(User.class), any());
    }


    @Test
    void doLoginTest() {
        UserLoginRequest mockRequest = new UserLoginRequest();
        mockRequest.setUserAccount("test@example.com");
        mockRequest.setUserPassword("password");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUserAccount("test@example.com");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = new MockHttpSession();
        when(mockHttpServletRequest.getSession()).thenReturn(mockSession);

        when(userMapper.selectOne(any())).thenReturn(mockUser);

        BaseResponse response = userService.doLogin(mockRequest, mockHttpServletRequest);

        assertNotNull(response);
        assertTrue(response.getData() instanceof User);
        User returnedUser = (User) response.getData();
        assertEquals(mockUser.getId(), returnedUser.getId());
        assertEquals(mockUser.getUserAccount(), returnedUser.getUserAccount());
        verify(userMapper, times(1)).selectOne(any());
    }


    @Test
    void doLogoutTest() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = new MockHttpSession();
        when(mockRequest.getSession()).thenReturn(mockSession);

        BaseResponse response = userService.doLogout(mockRequest);

        assertNotNull(response);
        assertEquals("logout success", response.getData());
    }

}
