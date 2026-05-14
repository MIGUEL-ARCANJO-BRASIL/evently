package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.CategoryResponseDTO;
import fametro.edu.br.evently.event.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("Nome da categoria é obrigatório");
        }
        try {
            CategoryResponseDTO response = categoryService.createCategory(name);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
