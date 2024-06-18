package com.loatodo.loatodobackend.domain.user.entity;

import com.loatodo.loatodobackend.util.Timestamp;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column
    private String picture;

    @Column
    private String provider;

    @Column
    private String providerId;

//    @Builder
//    public User(String username, String name, String email, String picture, UserRole role) {
//        this.username = username;
//        this.name = name;
//        this.email = email;
//        this.picture = picture;
//        this.role = role;
//    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
