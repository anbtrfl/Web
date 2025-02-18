package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.form.PostForm;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PostFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostForm.class.equals(clazz);
    }


    private boolean validateTags(String tags) {
        return (Arrays.stream(tags.trim().split("\\s+")).
                allMatch(str -> str.matches("[A-Za-z]{1,64}"))) ||
                tags.trim().isEmpty();
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            PostForm postForm = (PostForm) target;
            if (!validateTags(postForm.getTags())) {
                errors.rejectValue("tags", "tags.invalid", "Tags should be a words split by spaces not longer than 64 charters, summary length shouldn't be longer than 256.");
            }
        }
    }
}