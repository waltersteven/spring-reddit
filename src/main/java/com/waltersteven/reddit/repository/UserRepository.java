package com.waltersteven.reddit.repository;

import com.waltersteven.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
