package staffme.model.entity;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "candidates")
public class Candidate extends BaseEntity {

    private String name;
    private BigDecimal cost;
    private String description;
    private String imageUrl;
    private Boolean isAvailable = true;
    private Category category;

    public Candidate() {
    }

    public Candidate(String name, BigDecimal cost, String description, String imageUrl, Category category) {
        this.name = name;
        this.cost = cost;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name = "image_url", nullable = false)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Column(name = "cost")
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    @Column(name = "is_available")
    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
    @ManyToOne
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
