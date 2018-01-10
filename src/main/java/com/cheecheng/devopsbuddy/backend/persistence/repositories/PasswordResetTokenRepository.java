package com.cheecheng.devopsbuddy.backend.persistence.repositories;

import com.cheecheng.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    @Query("select prt from PasswordResetToken prt inner join prt.user u where prt.user.id = ?1")
    //@Query("select prt from PasswordResetToken prt inner join prt.user u where u.id = ?1")
    Set<PasswordResetToken> findAllByUserId(long userId);
}
