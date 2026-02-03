package Nhom5.ThachTrongKhang.controllers;

import Nhom5.ThachTrongKhang.entities.Book; // Sửa: dùng entities thay vì Models
import Nhom5.ThachTrongKhang.services.BookService;
import Nhom5.ThachTrongKhang.services.CategoryService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping
    public String showAllBooks(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        
        Page<Book> bookPage = bookService.getAllBooks(pageNo, pageSize, sortBy);
        
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        
        return "book/list";
    }
}