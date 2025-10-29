package ng.darum.employee.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="employees")
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private String employeeId;
	private String firstName;
	private String lastName;
	private String status;
	private Long departmentId;
	
	@CreationTimestamp
	@Column(updatable = false)
	LocalDateTime createdAt;

	@UpdateTimestamp
	LocalDateTime updatedAt;

}
