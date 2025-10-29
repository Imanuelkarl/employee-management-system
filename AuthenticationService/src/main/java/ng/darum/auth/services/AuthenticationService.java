package ng.darum.auth.services;

import ng.darum.auth.components.JwtUtil;
import ng.darum.auth.dto.*;
import ng.darum.auth.entity.User;
import ng.darum.auth.feign.EmployeeInterface;
import ng.darum.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeInterface employeeInterface;

    @Autowired
    JwtUtil jwtUtil;

    @Transactional
    public UserResponse createUser(UserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("User already exists with this email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .role(request.getRole())
                .passHash(passwordEncoder.encode(request.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
       return userToUserResponse(savedUser);
    }

    public AuthResponse loginUser(UserRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No user exists with the given email address"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassHash())){
            throw new RuntimeException("Incorrect password for user");
        }

        String token =jwtUtil.generateToken(user.getEmail(),user.getRole());
        return AuthResponse.builder()
                .email(user.getEmail())
                .id(user.getId())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Transactional
    public String deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No User with id:" + id + " found"));

        // Delete user
        userRepository.deleteById(id);
        return "Success";
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean userUpdated = false;

        // Update user fields if provided
        if(request.getEmail() != null && !request.getEmail().equals(user.getEmail())){
            if(userRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
            userUpdated = true;
        }

        if(request.getRole() != null && !request.getRole().equals(user.getRole())){
            user.setRole(request.getRole());
            userUpdated = true;
        }

        if(request.getPassword() != null && !request.getPassword().isEmpty()){
            user.setPassHash(passwordEncoder.encode(request.getPassword()));
            userUpdated = true;
        }

        User savedUser = userUpdated ? userRepository.save(user) : user;

        return userToUserResponse(savedUser);
    }

    public UserResponse findUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user was found with the given id"));
        return userToUserResponse(user);
    }
    private UserResponse userToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}