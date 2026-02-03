package Nhom5.ThachTrongKhang.entities;

import Nhom5.ThachTrongKhang.entities.Category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String author;
    private Double price;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
