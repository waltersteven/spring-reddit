package com.waltersteven.reddit.service;

import com.waltersteven.reddit.dto.PostRequest;
import com.waltersteven.reddit.dto.PostResponse;
import com.waltersteven.reddit.exceptions.PostNotFoundException;
import com.waltersteven.reddit.exceptions.SubredditNotFoundException;
import com.waltersteven.reddit.mapper.PostMapper;
import com.waltersteven.reddit.model.Post;
import com.waltersteven.reddit.model.Subreddit;
import com.waltersteven.reddit.model.User;
import com.waltersteven.reddit.repository.PostRepository;
import com.waltersteven.reddit.repository.SubredditRepository;
import com.waltersteven.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final AuthService authService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubredditRepository subredditRepository;
    private final PostMapper postMapper;

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));

        postRepository.save(postMapper.mapDtoToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));

        return postMapper.mapPostToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));

        return postRepository.findAllBySubreddit(subreddit)
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return postRepository.findAllByUser(user)
                .stream()
                .map(postMapper::mapPostToDto)
                .collect(Collectors.toList());
    }
}
