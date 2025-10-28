package ng.darum.auth.services;

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
    private KafkaProducerService kafkaProducerService;

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


        //Event publishing to kafka
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .employeeId(request.getEmployeeId())
                .status(request.getStatus())
                .department(request.getDepartment())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(savedUser.getRole())
                .build();

        kafkaProducerService.publishUserCreatedEvent(event);

        return userToUserResponse(savedUser);
    }

    public UserResponse loginUser(UserRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No user exists with the given email address"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassHash())){
            throw new RuntimeException("Incorrect password for user");
        }

        return userToUserResponse(user);
    }

    @Transactional
    public String deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No User with id:" + id + " found"));

        try {
            // Delete employee record first
            ResponseEntity<?> response = employeeInterface.deleteEmployee(id);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to delete employee record");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete employee record: " + e.getMessage());
        }

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

        // Update employee record if firstName, lastName, or department is provided
        if (request.getFirstName() != null || request.getLastName() != null || request.getDepartment() != null) {
            try {
                // First, get the current employee to preserve existing data
                ResponseEntity<?> employeeResponse = employeeInterface.findEmployee(id);

                if (employeeResponse.getStatusCode().is2xxSuccessful() && employeeResponse.getBody() != null) {
                    // Create updated employee object
                    Employee updatedEmployee = Employee.builder()
                            .id(id) // Assuming employee has same ID as user
                            .employeeId("EMP-" + id)
                            .userId(id)
                            .status("active")
                            .firstName(request.getFirstName() != null ? request.getFirstName() : getFirstNameFromEmployee(employeeResponse))
                            .lastName(request.getLastName() != null ? request.getLastName() : getLastNameFromEmployee(employeeResponse))
                            .department(request.getDepartment() != null ? request.getDepartment() : getDepartmentFromEmployee(employeeResponse))
                            .build();

                    ResponseEntity<?> updateResponse = employeeInterface.updateDepartment(id, updatedEmployee);
                    if (!updateResponse.getStatusCode().is2xxSuccessful()) {
                        throw new RuntimeException("Failed to update employee record");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to update employee record: " + e.getMessage());
            }
        }

        return userToUserResponse(savedUser);
    }

    public UserResponse findUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user was found with the given id"));
        return userToUserResponse(user);
    }

    /**
     * Get complete user profile including employee information
     */
    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user was found with the given id"));

        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        try {
            // Fetch employee information
            ResponseEntity<?> employeeResponse = employeeInterface.findEmployee(id);
            if (employeeResponse.getStatusCode().is2xxSuccessful() && employeeResponse.getBody() != null) {
                profileResponse.setEmployeeInfo(employeeResponse.getBody());
            }
        } catch (Exception e) {
            // Log but don't fail if employee service is unavailable
            System.err.println("Failed to fetch employee information: " + e.getMessage());
        }

        return profileResponse;
    }

    /**
     * Get all users with their employee information
     */
    public UserProfileResponse getAllUsersWithEmployeeInfo() {
        // Implementation to get all users and their employee info
        // This would depend on your specific requirements
        return null;
    }

    private UserResponse userToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    // Helper methods to extract data from employee response
    private String getFirstNameFromEmployee(ResponseEntity<?> employeeResponse) {

        return "Unknown";
    }

    private String getLastNameFromEmployee(ResponseEntity<?> employeeResponse) {
        // Implement based on your Employee response structure
        return "Unknown";
    }

    private String getDepartmentFromEmployee(ResponseEntity<?> employeeResponse) {
        // Implement based on your Employee response structure
        return "Unknown";
    }
}