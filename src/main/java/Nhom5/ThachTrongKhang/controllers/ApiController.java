package Nhom5.ThachTrongKhang.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Nhom5.ThachTrongKhang.entities.Book;
import Nhom5.ThachTrongKhang.entities.Category;
import Nhom5.ThachTrongKhang.services.BookService;
import Nhom5.ThachTrongKhang.services.CategoryService;
import Nhom5.ThachTrongKhang.viewmodels.BookGetVm;
import Nhom5.ThachTrongKhang.viewmodels.BookPostVm;
import Nhom5.ThachTrongKhang.viewmodels.CategoryGetVm;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApiController {
    private final BookService bookService;

    private final CategoryService categoryService;

    @GetMapping("/books")
    public ResponseEntity<Map<String, Object>> getAllBooks(Integer pageNo,
            Integer pageSize, String sortBy) {
        Page<Book> bookPage = bookService.getAllBooks(
                pageNo == null ? 0 : pageNo,
                pageSize == null ? 20 : pageSize,
                sortBy == null ? "id" : sortBy);
        
        List<BookGetVm> books = bookPage.getContent().stream()
                .map(BookGetVm::from)
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", books);
        response.put("totalPages", bookPage.getTotalPages());
        response.put("totalElements", bookPage.getTotalElements());
        response.put("currentPage", bookPage.getNumber());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id)
                .map(BookGetVm::from)
                .orElse(null));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id) {
        try {
            bookService.deleteBookById(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete book: " + e.getMessage());
        }
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(String keyword) {
        return ResponseEntity.ok(bookService.searchBook(keyword)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @PostMapping("/books")
    public ResponseEntity<BookGetVm> createBook(@Valid @RequestBody BookPostVm bookPostVm) {
        Category category = categoryService.getCategoryById(bookPostVm.categoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        Book book = Book.builder()
                .title(bookPostVm.title())
                .author(bookPostVm.author())
                .price(bookPostVm.price())
                .imageUrl(bookPostVm.imageUrl())
                .category(category)
                .build();

        bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookGetVm.from(book));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookGetVm> updateBook(@PathVariable Long id,
            @Valid @RequestBody BookPostVm bookPostVm) {
        Book existingBook = bookService.getBookById(id).orElse(null);
        if (existingBook == null) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryService.getCategoryById(bookPostVm.categoryId())
                .orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        existingBook.setTitle(bookPostVm.title());
        existingBook.setAuthor(bookPostVm.author());
        existingBook.setPrice(bookPostVm.price());
        existingBook.setImageUrl(bookPostVm.imageUrl());
        existingBook.setCategory(category);

        bookService.updateBook(existingBook);
        return ResponseEntity.ok(BookGetVm.from(existingBook));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryGetVm>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()
                .stream()
                .map(CategoryGetVm::from)
                .toList());
    }
}
