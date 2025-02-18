package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.security.Guest;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PostPage extends Page {
    private final PostService postService;

    public PostPage(PostService postService) {
        this.postService = postService;
    }

    @Guest
    @GetMapping({"/post/{id}", "/post/"})
    public String postPage(@PathVariable(required=false) String id, Model model) {
        Post post = null;
        try {
            post = postService.findById(Long.valueOf(id));
        } catch (NumberFormatException ignored) {
        }
        model.addAttribute("currentPost", post);
        model.addAttribute("comment", new Comment());
        return "PostPage";
    }

    @PostMapping("/post/{id}")
    public String writeCommentPost(@PathVariable String id,
                                   @Valid @ModelAttribute Comment comment,
                                   BindingResult bindingResult,
                                   HttpSession httpSession, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPost", postService.findById(Long.valueOf(id)));
            return "PostPage";
        }
        postService.writeComment(postService.findById(Long.valueOf(id)), comment);
        putMessage(httpSession, "Comment created successful");
        return "redirect:/post/" + id;
    }
}
