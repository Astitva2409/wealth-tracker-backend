package com.wealthtracker.app.entities;

import com.wealthtracker.app.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Set<Role> allows a user to have multiple roles e.g. USER + ADMIN
    // EAGER — loaded immediately with the user (needed for Spring Security)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // ── UserDetails methods ──────────────────────────────────
    // Spring Security calls these internally to authorize requests

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override
    public String getUsername() {
        // We use email as the unique login identifier
        return email;
    }
}