package Nhom5.ThachTrongKhang.controllers;

import Nhom5.ThachTrongKhang.entities.Book;
import Nhom5.ThachTrongKhang.entities.Category;
import Nhom5.ThachTrongKhang.entities.User;
import Nhom5.ThachTrongKhang.repositories.IUserRepository;
import Nhom5.ThachTrongKhang.services.BookService;
import Nhom5.ThachTrongKhang.services.CategoryService;
import Nhom5.ThachTrongKhang.services.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final IUserRepository userRepository;
    private final BookService bookService;
    private final CategoryService categoryService;
    private final ExcelService excelService;

    // User Management
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/lock")
    @ResponseBody
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        user.setLocked(true);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã khóa tài khoản"));
    }

    @PostMapping("/users/{id}/unlock")
    @ResponseBody
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        user.setLocked(false);
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã mở khóa tài khoản"));
    }

    // Book Management with Excel
    @GetMapping("/books")
    public String manageBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        return "admin/books";
    }

    @GetMapping("/books/export")
    public ResponseEntity<Resource> exportBooks() throws IOException {
        List<Book> books = bookService.getAllBooks();
        byte[] excelData = excelService.exportBooksToExcel(books);
        
        ByteArrayResource resource = new ByteArrayResource(excelData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @PostMapping("/books/import")
    @ResponseBody
    public ResponseEntity<?> importBooks(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "File không được để trống"));
        }
        
        try {
            List<ExcelService.BookImportDto> importedBooks = excelService.importBooksFromExcel(file);
            int successCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (ExcelService.BookImportDto dto : importedBooks) {
                try {
                    // Find category by name
                    Category category = categoryService.getAllCategories().stream()
                            .filter(c -> c.getName().equalsIgnoreCase(dto.getCategoryName()))
                            .findFirst()
                            .orElse(null);
                    
                    if (category == null) {
                        errorCount++;
                        errors.append("Dòng với tiêu đề '").append(dto.getTitle())
                              .append("': Không tìm thấy danh mục '").append(dto.getCategoryName()).append("'\n");
                        continue;
                    }
                    
                    Book book;
                    if (dto.getId() != null) {
                        // Update existing book
                        Optional<Book> bookOpt = bookService.getBookById(dto.getId());
                        book = bookOpt.orElse(new Book());
                    } else {
                        // Create new book
                        book = new Book();
                    }
                    
                    book.setTitle(dto.getTitle());
                    book.setAuthor(dto.getAuthor());
                    book.setPrice(dto.getPrice());
                    book.setCategory(category);
                    book.setImageUrl(dto.getImageUrl());
                    
                    bookService.addBook(book);
                    successCount++;
                    
                } catch (Exception e) {
                    errorCount++;
                    errors.append("Lỗi với sách '").append(dto.getTitle()).append("': ")
                          .append(e.getMessage()).append("\n");
                }
            }
            
            String message = String.format("Import hoàn tất: %d thành công, %d lỗi", successCount, errorCount);
            if (errorCount > 0) {
                message += "\nChi tiết lỗi:\n" + errors.toString();
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", message,
                "successCount", successCount,
                "errorCount", errorCount
            ));
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false, 
                "message", "Lỗi đọc file: " + e.getMessage()
            ));
        }
    }
}
