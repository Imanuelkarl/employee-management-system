package ng.darum.auth.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
public class Employee {
    private Long id;
    private Long userId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String status;
    private String department;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
