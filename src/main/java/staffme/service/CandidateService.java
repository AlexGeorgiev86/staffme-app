package staffme.service;

import org.springframework.web.multipart.MultipartFile;
import staffme.model.service.CandidateServiceModel;

import java.io.IOException;
import java.util.List;

public interface CandidateService {
    CandidateServiceModel addCandidate(CandidateServiceModel candidateServiceModel, MultipartFile imageFile) throws IOException;

    List<CandidateServiceModel> findAllCandidatesWithCategory(String category);
    CandidateServiceModel findById(String id);
    void deleteById(String id, Boolean isEmployee) throws IOException;
}
