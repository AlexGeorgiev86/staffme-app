package staffme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staffme.model.entity.Candidate;
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
}
