package com.example.demo.userSec;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSecRepository extends JpaRepository<UserSec, Long> {
    Optional<UserSec> findUserEntityByUsername(String username);
    @Query("SELECT user FROM UserSec user WHERE user.idSubject=:idSubject AND user.subjectType=:subjectType")
    Optional<UserSec> findUserEntityByIdSubject(@Param("idSubject") Long idSubject, @Param("subjectType") SubjectType subjectType);
}
