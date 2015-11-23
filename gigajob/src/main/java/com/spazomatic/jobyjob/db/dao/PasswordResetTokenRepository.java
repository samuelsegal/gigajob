package com.spazomatic.jobyjob.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spazomatic.jobyjob.db.model.PasswordResetToken;
import com.spazomatic.jobyjob.db.model.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

}