package com.loatodo.loatodobackend;

import jakarta.persistence.*;

@Entity
@Table(name = "test")
public class TestMakeEntities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
}
