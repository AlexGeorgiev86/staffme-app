package staffme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staffme.model.entity.Employee;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
