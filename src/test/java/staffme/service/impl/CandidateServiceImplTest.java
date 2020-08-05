package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import staffme.error.CandidateNotFoundException;
import staffme.model.entity.Candidate;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;
import staffme.model.service.CandidateServiceModel;
import staffme.model.service.CategoryServiceModel;
import staffme.repository.CandidateRepository;
import staffme.service.CategoryService;
import staffme.service.CloudinaryService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {
    @Mock
    CandidateRepository candidateRepository;
    @Mock
    CategoryService categoryService;
    @Mock
    CloudinaryService cloudinaryService;

    ModelMapper modelMapper;

    CandidateServiceImpl candidateService;

    MultipartFile imageFile;


    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();

        candidateService = new CandidateServiceImpl(this.candidateRepository,
                this.modelMapper, this.categoryService, this.cloudinaryService);
    }

    @Test
    void addCandidateShouldReturnCorrectData() throws IOException {

        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);

        CandidateServiceModel candidateServiceModel = new CandidateServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel);

        List<Candidate> fakeCandidateRepository = new ArrayList<>();

        Mockito.when(candidateRepository.saveAndFlush(any(Candidate.class)))
                .thenAnswer(invocation -> {
                    fakeCandidateRepository.add((Candidate) invocation.getArguments()[0]);
                    return fakeCandidateRepository.get(0);
                });

        Mockito.when(categoryService.findByName(candidateServiceModel
                .getCategory()
                .getCategoryName()))
                .thenReturn(categoryServiceModel);

        Mockito.when(cloudinaryService.uploadImage(imageFile)).thenReturn("img");

        CandidateServiceModel actual = candidateService.addCandidate(candidateServiceModel, imageFile);

        assertEquals(candidateServiceModel.getCategory().getCategoryName(), actual.getCategory().getCategoryName());
        assertEquals(candidateServiceModel.getCost(), actual.getCost());
        assertEquals(candidateServiceModel.getDescription(), actual.getDescription());
        assertEquals(candidateServiceModel.getName(), actual.getName());
        assertEquals(candidateServiceModel.getImageUrl(), actual.getImageUrl());
    }

    @Test
    void findAllCandidatesWithCategoryShouldReturnCorrectData_WhenExistingCategoryIsPassed() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);

        Candidate candidate = new Candidate("Pesho", new BigDecimal(1),
                "good","ïmg", category);
        CandidateServiceModel candidateServiceModel = new CandidateServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel);

        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);
        List<CandidateServiceModel> expected = new ArrayList<>();
        expected.add(candidateServiceModel);

        Mockito.when(candidateRepository.findAll()).thenReturn(candidates);

        List<CandidateServiceModel> actual = candidateService.
                findAllCandidatesWithCategory(category.getCategoryName().name());

        assertEquals(expected.get(0).getCategory().getCategoryName().name(),
                actual.get(0).getCategory().getCategoryName().name());
    }

    @Test
    void findAllCandidatesWithCategoryShouldReturnEmptyList_WhenCategoryIsNotExisting() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        Category categoryNotExisting = new Category();
        categoryNotExisting.setCategoryName(CategoryName.HOST);

        Candidate candidate = new Candidate("Pesho", new BigDecimal(1),
                "good","ïmg", category);

        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        Mockito.when(candidateRepository.findAll()).thenReturn(candidates);

        List<CandidateServiceModel> actual = candidateService.
                findAllCandidatesWithCategory(categoryNotExisting.getCategoryName().name());

        List<CandidateServiceModel> expected = new ArrayList<>();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void findByIdShouldReturnCorrectData_WhenExistingIdIsPassed() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);

        Candidate candidate = new Candidate("Pesho", new BigDecimal(1),
                "good","ïmg", category);
        CandidateServiceModel expected = new CandidateServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel);

        String id = "id";

        Mockito.when(candidateRepository.findById(id)).thenReturn(Optional.of(candidate));

        CandidateServiceModel actual = candidateService.findById(id);

        assertEquals(expected.getCategory().getCategoryName(), actual.getCategory().getCategoryName());
        assertEquals(expected.getCost(), actual.getCost());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
    }

    @Test()
    void findByIdShouldThrowException_whenCandidateDoNotExists() {

        String id = "id";

        Mockito.when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        CandidateNotFoundException thrown = assertThrows(
                CandidateNotFoundException.class,
                () -> candidateService.findById(id));

        assertTrue(thrown.getMessage().contains("Candidate with the given id was not found!"));
    }

    @Test
    void deleteCandidateShouldThrowException_whenCandidateDoNotExists() {
        String id = "id";

        Mockito.when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        CandidateNotFoundException thrown = assertThrows(
                CandidateNotFoundException.class,
                () -> candidateService.deleteById(id, false));

        assertTrue(thrown.getMessage().contains("Candidate with the given id was not found!"));
    }
}