package staffme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staffme.model.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {
}
