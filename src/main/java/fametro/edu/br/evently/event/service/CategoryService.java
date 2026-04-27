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
}
