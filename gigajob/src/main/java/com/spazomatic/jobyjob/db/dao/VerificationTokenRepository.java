package com.spazomatic.jobyjob.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

}