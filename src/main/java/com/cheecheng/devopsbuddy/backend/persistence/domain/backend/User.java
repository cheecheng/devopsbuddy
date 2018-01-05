package com.cheecheng.devopsbuddy.backend.persistence.domain.backend;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This is difference from the video. In video, it creates the intermediary UserRole table, around time 10:15.
 *
 * @Entity
 * @Table(name = "user_role")
 * public class UserRole {
 *     @Id
 *     @ManyToOne(fetch = FetchType.EAGER)
 *     @JoinColumn(name = "user_id")
 *     private User user;
 *
 *     @Id
 *     @ManyToOne(fetch = FetchType.EAGER)
 *     @JoinColumn(name = "role_id")
 *     private Role role;
 * }
 *
 * But here I don't create this table, I follow
 * page 64 of "Hibernate Tips - More than 70 solutions to common Hibernate problems.pdf"
 * https://vladmihalcea.com/2017/05/10/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/
 *
 * and let Hibernate generates the intermediary table.
 *
 * ** BUT **
 * According to high-performance-java-persistence.pdf, p. 219,
 * The most efficient JPA relationships are the ones where the foreign key side is controlled by a
 * child-side @ManyToOne or @OneToOne association. For this reason, the many-to-many table relationship is
 * best mapped with two bidirectional @OneToMany associations. The entity removal and the element order changes are
 * more efficient than the default @ManyToMany relationship and the junction entity can also map additional columns
 * (e.g. created_on, created_by).
 *
 * So, it's better to manually create the intermediary table entity, like the video.
 * See the following for more information,
 * high-performance-java-persistence.pdf,
 * p. 213, 10.5.2 Bidirectional @ManyToMany
 * p. 215, 10.5.3 The @OneToMany alternative
 */
@Entity
public class User implements UserDetails{

    // Don't forget no-arg constructor

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;

    private String password;

    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Length(max = 500)
    private String description;

    private String country;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;

    private boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")   // define column name on User table that will be used as foreign key
    private Plan plan;

    /*
    @ManyToOne -> Many users with the same plan
    When looking at relationship annotation, always look at it from the perspective
    of the entity where the annotation is being defined.
    e.g. Entity = User, Plan property, @ManyToOne, so, User (many), Plan (one)
     */

    /* User is the owner of Role */
    // CAUTION: DO NOT USE CascadeType.ALL because the CascadeType.REMOVE
    // might end-up deleting more than we’re expecting
    // See https://vladmihalcea.com/2015/03/05/a-beginners-guide-to-jpa-and-hibernate-cascade-types/
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
                fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    /*
    Need "fetch = FetchType.EAGER", or integration test will fail with following exception,
    org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role:
    com.cheecheng.devopsbuddy.backend.persistence.domain.backend.User.roles, could not initialize proxy - no Session

	at com.cheecheng.devopsbuddy.test.integration.RepositoriesIntegrationTest.testCreateNewUser(RepositoriesIntegrationTest.java:87)
     */

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(r -> authorities.add(new Authority(r.getName())));
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

/*
Hibernate:
    drop table plan if exists
Hibernate:
    drop table role if exists
Hibernate:
    drop table user if exists
Hibernate:
    drop table user_roles if exists
Hibernate:
    create table plan (
        id integer not null,
        name varchar(255),
        primary key (id)
    )
Hibernate:
    create table role (
        id integer not null,
        name varchar(255),
        primary key (id)
    )
Hibernate:
    create table user (
        id bigint generated by default as identity,
        country varchar(255),
        description varchar(500),
        email varchar(255),
        enabled boolean not null,
        first_name varchar(255),
        last_name varchar(255),
        password varchar(255),
        phone_number varchar(255),
        profile_image_url varchar(255),
        stripe_customer_id varchar(255),
        username varchar(255),
        plan_id integer,
        primary key (id)
    )
Hibernate:
    create table user_roles (
        users_id bigint not null,
        roles_id integer not null,
        primary key (users_id, roles_id)
    )
Hibernate:
    alter table user
        add constraint FKeos0c7nc1mvicjcxbkxxolohc
        foreign key (plan_id)
        references plan
Hibernate:
    alter table user_roles
        add constraint FKj9553ass9uctjrmh0gkqsmv0d
        foreign key (roles_id)
        references role
Hibernate:
    alter table user_roles
        add constraint FK7ecyobaa59vxkxckg6t355l86
        foreign key (users_id)
        references user

 */