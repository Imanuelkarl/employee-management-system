package ng.darum.employee.service;

import lombok.extern.slf4j.Slf4j;
import ng.darum.employee.entity.Department;
import ng.darum.employee.repository.DepartmentRepository;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    //create department
    public Department createDepartment(Department department){
        if(departmentRepository.existsByName(department.getName())){
            throw new RuntimeException("Department Already exists");

        }
        log.info(department.getName());
        department.setSlug(department.getName().toLowerCase()); // Simple slug setup for now
        return departmentRepository.save(department);
    }

    //update department
    public Department updateDepartment(Long id,Department department){
        Department target = departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
        copyNonNullProperties(department,target);
        return departmentRepository.save(target);
    }

    //delete department
    public void deleteDepartment(Long id){
        if(!departmentRepository.existsById(id)){
            throw new RuntimeException("Department does not exist");
        }
        departmentRepository.deleteById(id);

    }

    //get department
    public Department findDepartmentById(Long id){
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    //get all departments
    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
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
