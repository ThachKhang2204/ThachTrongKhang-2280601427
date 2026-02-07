package Nhom5.ThachTrongKhang.viewmodels;
import Nhom5.ThachTrongKhang.entities.Book;
import jakarta.validation.constraints.NotNull; 
import lombok.Builder; 
@Builder 
public record BookPostVm(String title, String author, Double price, 
Long categoryId, String imageUrl) { 
    public static BookPostVm from(@NotNull Book book) { 
        return new BookPostVm(book.getTitle(), book.getAuthor(), 
book.getPrice(), book.getCategory().getId(), book.getImageUrl()); 
    } 
} 
