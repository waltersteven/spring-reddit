package com.waltersteven.reddit.repository;

import com.waltersteven.reddit.model.Post;
import com.waltersteven.reddit.model.User;
import com.waltersteven.reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
