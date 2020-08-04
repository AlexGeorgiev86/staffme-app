package staffme.model.service;

import java.math.BigDecimal;

public class CandidateServiceModel extends BaseServiceModel {

    private String name;
    private BigDecimal cost;
    private String description;
    private String imageUrl;
    private CategoryServiceModel category;

    public CandidateServiceModel() {
    }

    public CandidateServiceModel(String name, BigDecimal cost, String description, String imageUrl, CategoryServiceModel category) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CategoryServiceModel getCategory() {
        return category;
    }

    public void setCategory(CategoryServiceModel category) {
        this.category = category;
    }
}
