package com.waltersteven.reddit.repository;

import com.waltersteven.reddit.model.Post;
import com.waltersteven.reddit.model.Subreddit;
import com.waltersteven.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findAllByUser(User user);
}
