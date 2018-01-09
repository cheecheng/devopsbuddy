package com.cheecheng.devopsbuddy.backend.persistence.domain.backend;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * **** NOT USED ****
 *
 * MUST implements Serializable
 * or will get this error:
 * org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory'
 * defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration.class]:
 * Invocation of init method failed; nested exception is javax.persistence.PersistenceException:
 * [PersistenceUnit: default] Unable to build Hibernate SessionFactory
 * Caused by: org.hibernate.MappingException: Composite-id class must implement Serializable:
 * com.cheecheng.devopsbuddy.backend.persistence.domain.backend.UserRoleId
 */
@Embeddable
public class UserRoleId implements Serializable {

    private long userId;
    private int roleId;

    public UserRoleId() {
    }

    public UserRoleId(long userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return userId == that.userId &&
                roleId == that.roleId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, roleId);
    }
}
