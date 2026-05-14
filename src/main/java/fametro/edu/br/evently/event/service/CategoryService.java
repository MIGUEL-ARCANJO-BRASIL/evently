package fametro.edu.br.evently.event.service;

import fametro.edu.br.evently.event.model.Category;
import fametro.edu.br.evently.event.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    public fametro.edu.br.evently.event.dto.CategoryResponseDTO createCategory(String nameCategory) {
        this.categoryRepository.findbyName(nameCategory).ifPresent(
                c -> {
                    throw new RuntimeException("Categoria com esse nome já existe");
                }
        );
        var category = Category.builder().name(nameCategory).build();
        var savedCategory = this.categoryRepository.save(category);
        return new fametro.edu.br.evently.event.dto.CategoryResponseDTO(savedCategory.getId(), savedCategory.getName());
    }
}
