package com.cheecheng.devopsbuddy.backend.persistence.domain.backend;

import javax.persistence.*;
import java.util.Objects;

/**
 * See comment on User.java
 */
@Entity
@Table(name = "user_role")
public class UserRole {

    //@EmbeddedId
    //private UserRoleId id;

    //@ManyToOne
    //@MapsId("userId")
    //private User user;

    //@ManyToOne
    //@MapsId("roleId")
    //private Role role;

    // high-performance-java-persistence.pdf, p. 219 doesn't work, follow what's on video.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public UserRole() {
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return id == userRole.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}


