package staffme.model.binding;

import org.springframework.web.multipart.MultipartFile;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;

import java.math.BigDecimal;

public class CandidateAddBindingModel {

    private String name;
    private String description;
    private MultipartFile image;
    private CategoryName category;

    public CandidateAddBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public CategoryName getCategory() {
        return category;
    }

    public void setCategory(CategoryName category) {
        this.category = category;
    }
}
