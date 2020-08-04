package staffme.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import staffme.error.CandidateNotFoundException;
import staffme.model.entity.Candidate;
import staffme.model.service.CandidateServiceModel;
import staffme.repository.CandidateRepository;
import staffme.service.CandidateService;
import staffme.service.CategoryService;
import staffme.service.CloudinaryService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;

    public CandidateServiceImpl(CandidateRepository candidateRepository, ModelMapper modelMapper, CategoryService categoryService, CloudinaryService cloudinaryService) {
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public CandidateServiceModel addCandidate(CandidateServiceModel candidateServiceModel, MultipartFile imageFile) throws IOException {
        candidateServiceModel
                .setCategory(this.categoryService.findByName(candidateServiceModel.getCategory().getCategoryName()));
        String imageUrl = this.cloudinaryService.uploadImage(imageFile);
        candidateServiceModel.setImageUrl(imageUrl);

        Candidate candidate = this.modelMapper.map(candidateServiceModel, Candidate.class);

        return this.modelMapper.map(this.candidateRepository.saveAndFlush(candidate), CandidateServiceModel.class);
    }

    @Override
    public List<CandidateServiceModel> findAllCandidatesWithCategory(String category) {

        return this.candidateRepository.findAll()
                .stream()
                .filter(candidate -> candidate.getCategory().getCategoryName().name().equals(category))
                .map(candidate -> this.modelMapper.map(candidate, CandidateServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public CandidateServiceModel findById(String id) {
        return this.candidateRepository
                .findById(id)
                .map(c -> this.modelMapper.map(c, CandidateServiceModel.class))
                .orElseThrow(() ->
                        new CandidateNotFoundException("Candidate with the given id was not found!"));

    }

    @Override
    public void deleteById(String id, Boolean isEmployee) throws IOException {
        Candidate candidate = this.candidateRepository.findById(id).orElseThrow(() ->
                new CandidateNotFoundException("Candidate with the given id was not found!"));

        if (!isEmployee) {
            String imgUrl = candidate.getImageUrl();
            String cloudinaryId = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
            this.cloudinaryService.deleteImage(cloudinaryId);
        }

        this.candidateRepository.deleteById(id);
    }
}
