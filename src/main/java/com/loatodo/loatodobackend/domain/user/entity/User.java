package com.loatodo.loatodobackend.domain.user.entity;

import com.loatodo.loatodobackend.domain.user.dto.UpdateUserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.loatodo.loatodobackend.util.Timestamp;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.persistence.*;
import lombok.*;

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

    // 정보 업데이트 메소드
//    public void updateUserInfo(UpdateUserDto userDto) {
////        if (newPassword != null) {
////            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
////            this.password = encoder.encode(newPassword);
////        }
//        if (userDto.getPassword() != null) {
//            this.password = userDto.getPassword();
//        }
//        if (userDto.getEmail() != null) {
//            this.email = userDto.getEmail();
//        }
//        if (userDto.getName() != null) {
//            this.name = userDto.getName();
//        }
//    }
    public void updateUserInfo(String newPassword, String newName, String newEmail) {
        if (newPassword != null) {
            this.password = newPassword;
        }
        if (newName != null) {
            this.name = newName;
        }
        if (newEmail != null) {
            this.email = newEmail;
        }
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
