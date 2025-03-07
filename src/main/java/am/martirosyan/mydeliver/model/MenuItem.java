package am.martirosyan.mydeliver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "image_path")
    private String imagePath;
}

