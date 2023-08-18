package com.example.data_collection.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")

@Setter @Getter
@AllArgsConstructor @Builder
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;

    @Column(name = "category_name")
    private String categoryName;

}
