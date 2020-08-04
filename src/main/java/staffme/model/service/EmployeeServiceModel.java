package staffme.model.service;

import java.math.BigDecimal;

public class EmployeeServiceModel extends BaseServiceModel {

    private String name;
    private BigDecimal cost;
    private String description;
    private String imageUrl;
    private CategoryServiceModel category;
    private Boolean isAvailable = true;

    public EmployeeServiceModel() {
    }

    public EmployeeServiceModel(String name, BigDecimal cost, String description, String imageUrl, CategoryServiceModel category, Boolean isAvailable) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
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
