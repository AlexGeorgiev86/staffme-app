package staffme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staffme.model.entity.Category;
import staffme.model.entity.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findAllByCategory(Category category);
}
