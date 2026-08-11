package com.example.demodevops.repository;

import com.example.demodevops.model.SocialAccount;
import com.example.demodevops.model.SocialAccount.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByPlatformAndExternalPageId(SocialPlatform platform, String externalPageId);
}
