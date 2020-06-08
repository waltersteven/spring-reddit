package com.waltersteven.reddit.service;

import com.waltersteven.reddit.dto.CommentDto;
import com.waltersteven.reddit.exceptions.PostNotFoundException;
import com.waltersteven.reddit.mapper.CommentMapper;
import com.waltersteven.reddit.model.Comment;
import com.waltersteven.reddit.model.NotificationEmail;
import com.waltersteven.reddit.model.Post;
import com.waltersteven.reddit.model.User;
import com.waltersteven.reddit.repository.CommentRepository;
import com.waltersteven.reddit.repository.PostRepository;
import com.waltersteven.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private static final String POST_URL = "";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    @Transactional
    public void save(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentDto.getPostId().toString()));

        Comment comment = commentMapper.mapDtoToComment(commentDto, post, authService.getCurrentUser());

        commentRepository.save(comment);

        String message = mailContentBuilder.build(authService.getCurrentUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendMailNotification(message, post.getUser());
    }

    private void sendMailNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail("Your post has a new comment", user.getEmail(), message));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));

        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapCommentToDto)
                .collect(Collectors.toList());
    }
}
