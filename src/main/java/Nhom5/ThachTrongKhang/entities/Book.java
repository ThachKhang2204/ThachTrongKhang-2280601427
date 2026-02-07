package Nhom5.ThachTrongKhang.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.Hibernate;

import Nhom5.ThachTrongKhang.Validator.annotation.ValidCategoryId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 200, nullable = false) 
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters") 
    @NotBlank(message = "Title must not be blank") 
    private String title;

    @Column(name = "author", length = 100, nullable = false)
    @Size(min = 1, max = 100
    ,message="Author must be between 1 and 100 characters") @NotBlank(message="Author must not be blank")private String author;

    @Column(name = "price")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Lob
    @Column(name = "image_url", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ValidCategoryId
    @ToString.Exclude
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ItemInvoice> itemInvoices = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Book book = (Book) o;
        return getId() != null && Objects.equals(getId(),
                book.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
