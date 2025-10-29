package ng.darum.auth.service;

import ng.darum.auth.components.JwtUtil;
import ng.darum.auth.dto.*;
import ng.darum.auth.entity.User;
import ng.darum.auth.enums.Role;
import ng.darum.auth.feign.EmployeeInterface;
import ng.darum.auth.repository.UserRepository;
import ng.darum.auth.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmployeeInterface employeeInterface;

    @Mock
    private JwtUtil jwtUtil;

    private User mockUser;
    private UserRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder()
                .id(1L)
                .email("john@example.com")
                .passHash("hashedPass")
                .role(Role.ADMIN)
                .build();

        mockRequest = UserRequest.builder()
                .email("john@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();
    }

    // --------------------------------------------
    // CREATE USER
    // --------------------------------------------
    @Test
    void testCreateUser_SuccessfullyCreatesUser() {
        when(userRepository.existsByEmail(mockRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(mockRequest.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = authenticationService.createUser(mockRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_ThrowsError_WhenUserAlreadyExists() {
        when(userRepository.existsByEmail(mockRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.createUser(mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User already exists");

        verify(userRepository, never()).save(any());
    }

    // --------------------------------------------
    // LOGIN USER
    // --------------------------------------------
    @Test
    void testLoginUser_SuccessfulLogin_ReturnsAuthResponse() {
        when(userRepository.findByEmail(mockRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(mockRequest.getPassword(), mockUser.getPassHash())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getRole())).thenReturn("mockToken");

        AuthResponse response = authenticationService.loginUser(mockRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockToken");
        assertThat(response.getEmail()).isEqualTo(mockUser.getEmail());
    }

    @Test
    void testLoginUser_ThrowsError_WhenUserNotFound() {
        when(userRepository.findByEmail(mockRequest.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.loginUser(mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No user exists");
    }

    @Test
    void testLoginUser_ThrowsError_WhenPasswordIncorrect() {
        when(userRepository.findByEmail(mockRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.loginUser(mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Incorrect password");
    }

    // --------------------------------------------
    // DELETE USER
    // --------------------------------------------
    @Test
    void testDeleteUser_SuccessfullyDeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).deleteById(1L);

        String response = authenticationService.deleteUser(1L);

        assertThat(response).isEqualTo("Success");
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_ThrowsError_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.deleteUser(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No User with id:1 found");
    }

    // --------------------------------------------
    // UPDATE USER
    // --------------------------------------------
    @Test
    void testUpdateUser_ChangesEmailAndPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserRequest updateRequest = UserRequest.builder()
                .email("new@example.com")
                .password("newpass")
                .role(Role.EMPLOYEE)
                .build();

        UserResponse response = authenticationService.updateUser(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getRole()).isEqualTo(Role.EMPLOYEE);
        verify(userRepository).save(any(User.class));
    }

    // --------------------------------------------
    // FIND USER BY ID
    // --------------------------------------------
    @Test
    void testFindUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse response = authenticationService.findUserById(1L);

        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testFindUserById_ThrowsError_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.findUserById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No user was found");
    }
}
