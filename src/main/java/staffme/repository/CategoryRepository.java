package staffme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategoryName(CategoryName categoryName);

}
