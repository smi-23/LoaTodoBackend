package com.loatodo.loatodobackend.domain.user.entity;

import com.loatodo.loatodobackend.domain.board.entity.Board;
import com.loatodo.loatodobackend.domain.comment.entity.Comment;
import com.loatodo.loatodobackend.util.Timestamp;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
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
    private String provider;

    @Column
    private String providerId;

    @Column
    private String picture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    public void updateName(String newName) {
        if (newName != null) {
            this.name = newName;
        }
    }

    public void updateEmail(String newEmail) {
        if (newEmail != null) {
            this.email = newEmail;
        }
    }

    public void updatePassword(String newPassword) {
        if (newPassword != null) {
            this.password = newPassword;
        }
    }


    public String getRoleKey() {
        return this.role.getKey();
    }
}
