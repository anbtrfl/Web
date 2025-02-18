package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.repository.PostRepository;
import ru.itmo.wp.repository.TagRepository;
import ru.itmo.wp.repository.UserRepository;

import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public void writeComment(Post post, Comment comment) {
        post.getComments().add(comment);
        comment.setPost(post);
        comment.setUser(post.getUser());
        postRepository.save(post);
    }

    public Post findById(Long id) {
        return id == null ? null : postRepository.findById(id).orElse(null);
    }

    public void writePost(User user, PostForm postForm) {
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setText(postForm.getText());
        String[] tags = postForm.getTags().trim().split(" ");
        if (tags.length != 0 && !tags[0].equals("")) {
            post.setTags(new HashSet<>());
            for (String tagName : tags) {
                Tag tag = tagRepository.findTagByName(tagName);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    try {
                        tag = tagRepository.save(tag);
                    } catch (Exception e) {
                        tag = tagRepository.findTagByName(tagName);
                    }
                }
                post.getTags().add(tag);
            }
        }
        user.addPost(post);
        userRepository.save(user);
    }
}
