package com.src.articleservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tags_name",
                        columnNames = "name"
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
}
