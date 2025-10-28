package ng.darum.employee.service;

import lombok.extern.slf4j.Slf4j;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.dto.ServerResponse;
import ng.darum.employee.entity.Department;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.repository.EmployeeRepository;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class EmployeeService {
	@Autowired
	EmployeeRepository employeeRepository;

	public ServerResponse<Object> createEmployee(EmployeeRequest employeeRequest){
		Employee employee = Employee.builder()
				.firstName(employeeRequest.getFirstName())
				.lastName(employeeRequest.getLastName())
				.employeeId(employeeRequest.getEmployeeId())
				.department(employeeRequest.getDepartment())
				.build();
		copyNonNullProperties(employeeRequest,employee);
		Employee saved=employeeRepository.save(employee);
		return ServerResponse.builder()
						.status("success")
						.message("Employee created successfully")
						.data(saved).build();
	}

	//update department
	public Employee updateEmployee(Long id, Employee employee){
		Employee target = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
		copyNonNullProperties(employee,target);
		return employeeRepository.save(target);
	}

	//delete department
	public void deleteEmployee(Long id){
		if(!employeeRepository.existsById(id)){
			throw new RuntimeException("Department does not exist");
		}
		employeeRepository.deleteById(id);

	}

	//get department
	public Employee findEmployeeById(Long id){
		return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
	}

	//get all departments
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	}








	protected void copyNonNullProperties(Object src, Object target) {
		try {
			final BeanWrapperImpl srcWrap = new BeanWrapperImpl(src);
			final BeanWrapperImpl trgWrap = new BeanWrapperImpl(target);
			Arrays.stream(srcWrap.getPropertyDescriptors()).forEach(pd -> {
				String name = pd.getName();
				if ("class".equals(name)) return;
				Object value = srcWrap.getPropertyValue(name);
				if (value != null && trgWrap.isWritableProperty(name)) {
					log.debug("Updating property: {} with value: {}", name, value);
					trgWrap.setPropertyValue(name, value);
				}
			});
		} catch (Exception e) {
			log.error("Error copying properties from source to target", e);
			throw new RuntimeException("Failed to copy properties: " + e.getMessage(), e);
		}
	}

}
